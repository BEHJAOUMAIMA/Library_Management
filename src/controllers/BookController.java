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
    public void addBook(String title, String description, int ISBN, int quantity, boolean bookState, Author author) throws SQLException {
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
            statement.setBoolean(5, bookState);
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
            statement.setBoolean(1, true);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("book_title");
                String description = resultSet.getString("book_description");
                int ISBN = resultSet.getInt("book_ISBN");
                int quantity = resultSet.getInt("book_quantity");
                boolean bookState = resultSet.getBoolean("book_state");
                int authorId = resultSet.getInt("author_id");

                if (bookState) {
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
                boolean bookState = resultSet.getBoolean("book_state");
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


}
