package controllers;

import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoanController {
    private Connection connection;

    public LoanController(Connection connection) {
        this.connection = connection;
    }

    public void borrowBook(String isbnOuTitre, int numeroMembre, Date dateEmprunt, Date dateRetour) {
        String selectBookQuery = "SELECT * FROM books WHERE book_ISBN = ? OR book_title = ?";
        String insertLoanQuery = "INSERT INTO loans (loan_date, return_date, borrower_id) VALUES (?, ?, ?)";
        String insertBorrowedCopyQuery = "INSERT INTO borrowedCopies (book_id,loan_id, borrowedCopy_status) VALUES (?, ?, 'Emprunté')";

        try {
            BorrowerController borrowerController = new BorrowerController(connection);
            Borrower emprunteur = borrowerController.searchBorrowerByMemberNumber(numeroMembre);

            if (emprunteur == null) {
                borrowerController.addBorrower("", "", "", "", "", numeroMembre);

                emprunteur = borrowerController.searchBorrowerByMemberNumber(numeroMembre);

                if (emprunteur == null) {
                    System.out.println("Échec de l'ajout de l'emprunteur.");
                    return;
                }
            }

            PreparedStatement selectBookStatement = connection.prepareStatement(selectBookQuery);
            selectBookStatement.setString(1, isbnOuTitre);
            selectBookStatement.setString(2, isbnOuTitre);
            ResultSet resultSet = selectBookStatement.executeQuery();

            if (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                int bookQuantity = resultSet.getInt("book_quantity");

                if (bookQuantity > 0) {

                    Date today = new Date();

                    if (dateEmprunt.before(today)) {
                        System.out.println("La date d'emprunt doit être égale ou postérieure à la date d'aujourd'hui.");
                        return;
                    }

                    if (dateRetour.before(dateEmprunt)) {
                        System.out.println("La date de retour doit être postérieure à la date d'emprunt.");
                        return;
                    }

                    PreparedStatement insertLoanStatement = connection.prepareStatement(insertLoanQuery, Statement.RETURN_GENERATED_KEYS);
                    insertLoanStatement.setDate(1, new java.sql.Date(dateEmprunt.getTime()));
                    insertLoanStatement.setDate(2, new java.sql.Date(dateRetour.getTime()));
                    insertLoanStatement.setInt(3, emprunteur.getBorrowerId());

                    int rowsAffected = insertLoanStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        int nouvelleQuantite = bookQuantity - 1;
                        String updateBookQuantityQuery = "UPDATE books SET book_quantity = ? WHERE book_id = ?";
                        PreparedStatement updateBookQuantityStatement = connection.prepareStatement(updateBookQuantityQuery);
                        updateBookQuantityStatement.setInt(1, nouvelleQuantite);
                        updateBookQuantityStatement.setInt(2, bookId);
                        updateBookQuantityStatement.executeUpdate();

                        int loanId;
                        ResultSet generatedKeys = insertLoanStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            loanId = generatedKeys.getInt(1);

                            PreparedStatement insertBorrowedCopyStatement = connection.prepareStatement(insertBorrowedCopyQuery);
                            insertBorrowedCopyStatement.setInt(1, bookId);
                            insertBorrowedCopyStatement.setInt(2, loanId);
                            insertBorrowedCopyStatement.executeUpdate();

                            System.out.println("Le livre a été emprunté avec succès.");
                        } else {
                            System.out.println("Échec de l'emprunt du livre.");
                        }
                    } else {
                        System.out.println("Échec de l'emprunt du livre.");
                    }
                } else {
                    System.out.println("Le livre n'est pas disponible pour l'emprunt.");
                }
            } else {
                System.out.println("Le livre n'a pas été trouvé.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'emprunt du livre : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> getBorrowedBooksDetails() {
        List<String> borrowedBooksDetails = new ArrayList<>();

        String query = "SELECT loans.loan_id, loans.loan_date, loans.return_date, books.book_title, borrowers.borrower_lastName, borrowers.borrower_firstName " +
                "FROM loans " +
                "INNER JOIN borrowedCopies ON loans.loan_id = borrowedCopies.loan_id " +
                "INNER JOIN books ON borrowedCopies.book_id = books.book_id " +
                "INNER JOIN borrowers ON loans.borrower_id = borrowers.borrower_id " +
                "WHERE borrowedCopies.borrowedCopy_status = 'Emprunté'";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int loanId = resultSet.getInt("loan_id");
                Date loanDate = resultSet.getDate("loan_date");
                Date returnDate = resultSet.getDate("return_date");
                String bookTitle = resultSet.getString("book_title");
                String borrowerLastName = resultSet.getString("borrower_lastName");
                String borrowerFirstName = resultSet.getString("borrower_firstName");

                String detailsMessage = "Loan ID: " + loanId +
                        ", Loan Date: " + loanDate +
                        ", Return Date: " + returnDate +
                        ", Book Title: " + bookTitle +
                        ", Borrower: " + borrowerFirstName + " " + borrowerLastName;

                borrowedBooksDetails.add(detailsMessage);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des informations sur les livres empruntés : " + e.getMessage());
            e.printStackTrace();
        }

        return borrowedBooksDetails;
    }

    public boolean returnBorrowedBook(int isbn, int memberNumber) throws SQLException {
        String selectBorrowedCopyQuery = "SELECT bc.borrowedCopy_id, bc.book_id, l.borrower_id " +
                "FROM borrowedCopies bc " +
                "INNER JOIN books b ON bc.book_id = b.book_id " +
                "INNER JOIN loans l ON bc.loan_id = l.loan_id " +
                "INNER JOIN borrowers br ON l.borrower_id = br.borrower_id " +
                "WHERE bc.borrowedCopy_status = 'Emprunté' AND b.book_ISBN = ? AND br.member_number = ?";

        String updateBorrowedCopyQuery = "UPDATE borrowedCopies SET borrowedCopy_status = 'Returned' WHERE borrowedCopy_id = ?";

        String updateBookQuantityQuery = "UPDATE books SET book_quantity = book_quantity + 1 WHERE book_id = ?";

        try {
            PreparedStatement selectBorrowedCopyStatement = connection.prepareStatement(selectBorrowedCopyQuery);
            selectBorrowedCopyStatement.setInt(1, isbn);
            selectBorrowedCopyStatement.setInt(2, memberNumber);
            ResultSet resultSet = selectBorrowedCopyStatement.executeQuery();

            if (resultSet.next()) {
                int borrowedCopyId = resultSet.getInt("borrowedCopy_id");
                int bookId = resultSet.getInt("book_id");

                PreparedStatement updateBorrowedCopyStatement = connection.prepareStatement(updateBorrowedCopyQuery);
                updateBorrowedCopyStatement.setInt(1, borrowedCopyId);
                int borrowedCopyUpdated = updateBorrowedCopyStatement.executeUpdate();

                PreparedStatement updateBookQuantityStatement = connection.prepareStatement(updateBookQuantityQuery);
                updateBookQuantityStatement.setInt(1, bookId);
                int bookQuantityUpdated = updateBookQuantityStatement.executeUpdate();

                return borrowedCopyUpdated > 0 && bookQuantityUpdated > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw e;
        }
    }



}
