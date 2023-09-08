package controllers;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import models.Author;
import models.Book;

public class BookController {
    private Connection connection;
    private AuthorController authorController;

    public BookController(Connection connection, AuthorController authorController ) {
        this.connection = connection;
        this.authorController = authorController;
    }
    public void addBook(String title, String description, int ISBN, int quantity, String bookState, Author author) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        Author existingAuthor = authorController.getAuthorByFullName(author.getAuthorFullName());

        if (existingAuthor == null) {
            System.out.println("L'auteur n'existe pas encore. Voulez-vous créer un nouvel auteur ? (oui/non)");
            String createAuthorChoice = scanner.nextLine();

            if (createAuthorChoice.equalsIgnoreCase("oui")) {
                System.out.println("Entrez la biographie de l'auteur :");
                String authorBio = scanner.nextLine();

                authorController.addAuthor(author.getAuthorFullName(), authorBio);

                existingAuthor = authorController.getAuthorByFullName(author.getAuthorFullName());
            } else {
                System.out.println("L'auteur doit exister pour ajouter le livre.");
                return;
            }
        }
        while (true) {
            String queryCheckISBN = "SELECT COUNT(*) AS count FROM books WHERE book_ISBN = ?";
            try (PreparedStatement checkISBNStatement = connection.prepareStatement(queryCheckISBN)) {
                checkISBNStatement.setInt(1, ISBN);
                ResultSet resultSet = checkISBNStatement.executeQuery();

                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    if (count > 0) {
                        System.out.println("ISBN déjà existant. Veuillez entrer un ISBN unique :");
                        ISBN = scanner.nextInt();
                        scanner.nextLine();
                    } else {
                        break;
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la vérification de l'ISBN : " + e.getMessage());
            }
        }
        while (true) {
            if (quantity <= 0) {
                System.out.println("La quantité doit être supérieure à 0. Veuillez entrer une quantité valide :");
                quantity = scanner.nextInt();
                scanner.nextLine();
            } else {
                break;
            }
        }

        String query = "INSERT INTO books(book_title, book_description, book_ISBN, book_quantity, book_state, author_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setInt(3, ISBN);
            statement.setInt(4, quantity);
            statement.setString(5, bookState);
            statement.setInt(6, existingAuthor.getAuthorId());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Le livre a été ajouté avec succès !");
            } else {
                System.out.println("Échec de l'ajout du livre.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du livre : " + e.getMessage());
        }
    }
    public List<Book> getAllAvailableBooks() throws SQLException {
        List<Book> availableBooks = new ArrayList<>();

        String query = "SELECT * FROM books WHERE book_state = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "disponible");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("book_title");
                String description = resultSet.getString("book_description");
                int ISBN = resultSet.getInt("book_ISBN");
                int quantity = resultSet.getInt("book_quantity");
                String bookState = resultSet.getString("book_state");
                int authorId = resultSet.getInt("author_id");

                if ("disponible".equals(bookState)) { // Utilisez equals ici
                    Author author = authorController.getAuthorByAuthorId(authorId);
                    Book book = new Book(bookId, title, description, ISBN, quantity, bookState, author);
                    availableBooks.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des livres disponibles : " + e.getMessage());
        }

        return availableBooks;
    }
    public List<Book> searchBooks(String searchTerm) throws SQLException {
        List<Book> matchingBooks = new ArrayList<>();

        String query = "SELECT b.* " +
                "FROM books b " +
                "INNER JOIN authors a ON b.author_id = a.author_id " +
                "WHERE b.book_title LIKE ? OR a.author_fullname LIKE ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("book_title");
                String description = resultSet.getString("book_description");
                int ISBN = resultSet.getInt("book_ISBN");
                int quantity = resultSet.getInt("book_quantity");
                String bookState = resultSet.getString("book_state");
                int authorId = resultSet.getInt("author_id");

                Author author = authorController.getAuthorByAuthorId(authorId);
                Book book = new Book(bookId, title, description, ISBN, quantity, bookState, author);
                matchingBooks.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de livres : " + e.getMessage());
        }

        return matchingBooks.isEmpty() ? null : matchingBooks;
    }

    public void updateBook(int ISBN) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        String queryCheckISBN = "SELECT * FROM books WHERE book_ISBN = ?";
        try (PreparedStatement checkISBNStatement = connection.prepareStatement(queryCheckISBN)) {
            checkISBNStatement.setInt(1, ISBN);
            ResultSet resultSet = checkISBNStatement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Aucun livre avec cet ISBN n'a été trouvé.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'ISBN : " + e.getMessage());
            return;
        }

        String queryGetBookInfo = "SELECT * FROM books WHERE book_ISBN = ?";
        try (PreparedStatement getBookInfoStatement = connection.prepareStatement(queryGetBookInfo)) {
            getBookInfoStatement.setInt(1, ISBN);
            ResultSet resultSet = getBookInfoStatement.executeQuery();

            if (resultSet.next()) {
                String currentTitle = resultSet.getString("book_title");
                String currentDescription = resultSet.getString("book_description");
                int currentQuantity = resultSet.getInt("book_quantity");
                int authorId = resultSet.getInt("author_id");

                AuthorController authorController = new AuthorController(connection);
                Author currentAuthor = authorController.getAuthorByAuthorId(authorId);
                String currentAuthorFullName = currentAuthor.getAuthorFullName();

                System.out.println("Informations actuelles du livre (laissez vide pour conserver les valeurs actuelles) :");
                System.out.println("Titre actuel : " + currentTitle);
                System.out.println("Description actuelle : " + currentDescription);
                System.out.println("Quantité actuelle : " + currentQuantity);
                System.out.println("Auteur actuel : " + currentAuthorFullName);

                System.out.println("Entrez le nouveau titre du livre (ou laissez vide) :");
                String newTitle = scanner.nextLine();
                if (newTitle.isEmpty()) {
                    newTitle = currentTitle;
                }

                System.out.println("Entrez la nouvelle description du livre (ou laissez vide) :");
                String newDescription = scanner.nextLine();
                if (newDescription.isEmpty()) {
                    newDescription = currentDescription;
                }

                System.out.println("Entrez la nouvelle quantité du livre (ou laissez vide pour conserver la quantité actuelle) :");
                String quantityInput = scanner.nextLine();
                int newQuantity = currentQuantity;

                if (!quantityInput.isEmpty()) {
                    try {
                        newQuantity = Integer.parseInt(quantityInput);
                        if (newQuantity <= 0) {
                            System.out.println("La quantité doit être supérieure à zéro. La quantité reste inchangée.");
                            newQuantity = currentQuantity;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("La quantité entrée n'est pas valide. La quantité reste inchangée.");
                    }
                }

                System.out.println("Voulez-vous également mettre à jour l'auteur de ce livre ? (oui/non)");
                String updateAuthorChoice = scanner.nextLine();

                if (updateAuthorChoice.equalsIgnoreCase("oui")) {
                    List<Author> authors = authorController.getAllAuthors();
                    System.out.println("Liste des auteurs disponibles :");

                    for (int i = 0; i < authors.size(); i++) {
                        System.out.println((i + 1) + ". " + authors.get(i).getAuthorFullName());
                    }

                    System.out.println("Entrez le numéro du nouvel auteur ou 0 pour conserver l'auteur actuel :");
                    int newAuthorChoice = scanner.nextInt();
                    scanner.nextLine();

                    Author newAuthor;

                    if (newAuthorChoice == 0) {
                        newAuthor = authorController.getAuthorByFullName(currentAuthorFullName);
                    } else if (newAuthorChoice > 0 && newAuthorChoice <= authors.size()) {
                        newAuthor = authors.get(newAuthorChoice - 1);
                    } else {
                        System.out.println("Choix invalide. L'auteur reste inchangé.");
                        return;
                    }

                    String queryUpdateAuthor = "UPDATE books SET author_id = ? WHERE book_ISBN = ?";
                    try (PreparedStatement updateAuthorStatement = connection.prepareStatement(queryUpdateAuthor)) {
                        updateAuthorStatement.setInt(1, newAuthor.getAuthorId());
                        updateAuthorStatement.setInt(2, ISBN);

                        int rowsUpdated = updateAuthorStatement.executeUpdate();

                        if (rowsUpdated > 0) {
                            System.out.println("L'auteur du livre a été mis à jour avec succès !");
                        } else {
                            System.out.println("Échec de la mise à jour de l'auteur du livre.");
                        }
                    } catch (SQLException e) {
                        System.err.println("Erreur lors de la mise à jour de l'auteur du livre : " + e.getMessage());
                    }
                }

                String queryUpdateBook = "UPDATE books SET book_title = ?, book_description = ?, book_quantity = ? WHERE book_ISBN = ?";
                try (PreparedStatement updateBookStatement = connection.prepareStatement(queryUpdateBook)) {
                    updateBookStatement.setString(1, newTitle);
                    updateBookStatement.setString(2, newDescription);
                    updateBookStatement.setInt(3, newQuantity);
                    updateBookStatement.setInt(4, ISBN);

                    int rowsUpdated = updateBookStatement.executeUpdate();

                    if (rowsUpdated > 0) {
                        System.out.println("Le livre a été mis à jour avec succès !");
                    } else {
                        System.out.println("Échec de la mise à jour du livre.");
                    }
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la mise à jour du livre : " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des informations du livre : " + e.getMessage());
        }
    }

    public void deleteBook(int ISBN) throws SQLException {
        String queryCheckISBN = "SELECT * FROM books WHERE book_ISBN = ?";
        try (PreparedStatement checkISBNStatement = connection.prepareStatement(queryCheckISBN)) {
            checkISBNStatement.setInt(1, ISBN);
            ResultSet resultSet = checkISBNStatement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Aucun livre avec cet ISBN n'a été trouvé.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'ISBN : " + e.getMessage());
            return;
        }

        String queryUpdateState = "UPDATE books SET book_state = 'deleted' WHERE book_ISBN = ?";
        try (PreparedStatement updateStateStatement = connection.prepareStatement(queryUpdateState)) {
            updateStateStatement.setInt(1, ISBN);

            int rowsUpdated = updateStateStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Le livre a été  supprimé avec succès !");
            } else {
                System.out.println("Échec de la suppression du livre.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'état du livre : " + e.getMessage());
        }
    }


}
