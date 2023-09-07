package controllers;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import models.Author;

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


}
