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
        return authors;
    }
    public void updateAuthor(int id, String fullName, String bio) throws SQLException {
        String query = "UPDATE authors SET author_fullname = ?, author_bio = ? WHERE author_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, fullName);
            statement.setString(2, bio);
            statement.setInt(3, id);
            statement.executeUpdate();
        }
    }
    public void deleteAuthor(int id) throws SQLException {
        String query = "DELETE FROM authors WHERE author_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

}
