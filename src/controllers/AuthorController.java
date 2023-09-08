package controllers;
import models.Author;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AuthorController {
    private Connection connection;
    //construct
    public AuthorController(Connection connection) {
        this.connection = connection;
    }
    public void addAuthor(String fullName, String bio) throws SQLException {
        if (fullName.isEmpty() || bio.isEmpty()) {
            System.out.println("Le nom complet et la biographie ne peuvent pas être vides.");
            return;
        }

        String query = "INSERT INTO authors(author_fullname, author_bio) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, fullName);
            statement.setString(2, bio);
            statement.executeUpdate();
        }
    }
    public List<Author> getAllAuthors() throws SQLException {
        List<Author> authors = new ArrayList<>();
        String query = "SELECT * FROM authors";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("author_id");
                String fullName = resultSet.getString("author_fullname");
                String bio = resultSet.getString("author_bio");
                Author author = new Author(id, fullName, bio);
                authors.add(author);
            }
        }

        if (authors.isEmpty()) {
            System.out.println("Aucun auteur trouvé dans la base de données.");
        }

        return authors;
    }
    public void updateAuthor(int id, String fullName, String bio) throws SQLException {
        String currentFullName = getCurrentFullNameFromDatabase(id);
        String currentBio = getCurrentBioFromDatabase(id);

        if (fullName.isEmpty()) {
            fullName = currentFullName;
        }

        if (bio.isEmpty()) {
            bio = currentBio;
        }

        String query = "UPDATE authors SET author_fullname = ?, author_bio = ? WHERE author_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, fullName);
            statement.setString(2, bio);
            statement.setInt(3, id);
            statement.executeUpdate();
        }
    }
    private String getCurrentFullNameFromDatabase(int id) throws SQLException {
        String query = "SELECT author_fullname FROM authors WHERE author_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("author_fullname");
                }
            }
        }
        return "";
    }
    private String getCurrentBioFromDatabase(int id) throws SQLException {
        String query = "SELECT author_bio FROM authors WHERE author_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("author_bio");
                }
            }
        }
        return "";
    }
    public void deleteAuthor(int id) throws SQLException {
        String query = "DELETE FROM authors WHERE author_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
    public Author getAuthorByFullName(String fullName) throws SQLException {
        String query = "SELECT * FROM authors WHERE author_fullname = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, fullName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int authorId = resultSet.getInt("author_id");
                    String bio = resultSet.getString("author_bio");
                    return new Author(authorId, fullName, bio);
                }
            }
        }

        return null;
    }
    public Author getAuthorByAuthorId(int authorId) throws SQLException {
        Author author = null;

        String query = "SELECT * FROM authors WHERE author_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, authorId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String fullName = resultSet.getString("author_fullname");
                String bio = resultSet.getString("author_bio");

                author = new Author(authorId, fullName, bio);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'auteur par ID : " + e.getMessage());
        }

        return author;
    }
    public Author getAuthor(String authorName) throws SQLException {
        String query = "SELECT * FROM authors WHERE author_fullname = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, authorName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int authorId = resultSet.getInt("author_id");
                String fullName = resultSet.getString("author_full_name");
                String bio = resultSet.getString("author_bio");
                return new Author(authorId, fullName, bio);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'auteur par nom : " + e.getMessage());
        }
        return null;
    }

    private Author extractAuthorFromResultSet(ResultSet resultSet) throws SQLException {
        int authorId = resultSet.getInt("author_id");
        String fullName = resultSet.getString("author_fullname");
        String bio = resultSet.getString("author_bio");
        return new Author(authorId, fullName, bio);
    }

}
