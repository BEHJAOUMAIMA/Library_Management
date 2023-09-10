package controllers;

import models.*;

import java.sql.*;
import java.util.Date;

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
            // Utilisez le contrôleur BorrowerController pour vérifier si l'utilisateur est membre
            BorrowerController borrowerController = new BorrowerController(connection);
            Borrower emprunteur = borrowerController.searchBorrowerByMemberNumber(numeroMembre);

            if (emprunteur == null) {
                // L'utilisateur n'est pas un membre, ajoutez-le en utilisant la fonction addBorrower du contrôleur BorrowerController
                borrowerController.addBorrower("", "", "", "", "", numeroMembre);

                // Réessayez d'obtenir l'emprunteur après l'ajout
                emprunteur = borrowerController.searchBorrowerByMemberNumber(numeroMembre);

                if (emprunteur == null) {
                    System.out.println("Échec de l'ajout de l'emprunteur.");
                    return;
                }
            }

            // Continuez avec l'emprunt
            PreparedStatement selectBookStatement = connection.prepareStatement(selectBookQuery);
            selectBookStatement.setString(1, isbnOuTitre);
            selectBookStatement.setString(2, isbnOuTitre);
            ResultSet resultSet = selectBookStatement.executeQuery();

            if (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                int bookQuantity = resultSet.getInt("book_quantity");

                if (bookQuantity > 0) {
                    // Le livre existe et est disponible

                    // Vérification de la validité des dates
                    Date today = new Date();

                    if (dateEmprunt.before(today)) {
                        System.out.println("La date d'emprunt doit être égale ou postérieure à la date d'aujourd'hui.");
                        return;
                    }

                    if (dateRetour.before(dateEmprunt)) {
                        System.out.println("La date de retour doit être postérieure à la date d'emprunt.");
                        return;
                    }

                    // Insérer l'emprunt
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

                            // Enregistrement de la copie empruntée dans la table borrowedCopy
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

}
