package controllers;
import models.Borrower;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BorrowerController {
    private Connection connection;
    public BorrowerController(Connection connection) {
        this.connection = connection;
    }

    public void addBorrower(String lastName, String firstName, String email, String telephone, String CIN, int memberNumber) throws SQLException {
        if (lastName.isEmpty() || firstName.isEmpty() || email.isEmpty() || telephone.isEmpty() || CIN.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs.");
            return;
        }

        String query = "INSERT INTO borrowers (borrower_lastName, borrower_firstName, borrower_email, borrower_telephone, borrower_CIN, member_number) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, lastName);
            statement.setString(2, firstName);
            statement.setString(3, email);
            statement.setString(4, telephone);
            statement.setString(5, CIN);
            statement.setInt(6, memberNumber);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("L'emprunteur a été ajouté avec succès !");
            } else {
                System.out.println("Échec de l'ajout de l'emprunteur.");
            }
        }
    }
    public boolean isMemberNumberUnique(int memberNumber) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM borrowers WHERE member_number = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, memberNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count == 0;
            }
        }

        return false;
    }
    public List<Borrower> getAllBorrowers() throws SQLException {
        List<Borrower> borrowers = new ArrayList<>();

        String query = "SELECT * FROM borrowers";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int borrowerId = resultSet.getInt("borrower_id");
                String lastName = resultSet.getString("borrower_lastName");
                String firstName = resultSet.getString("borrower_firstName");
                String email = resultSet.getString("borrower_email");
                String telephone = resultSet.getString("borrower_telephone");
                String CIN = resultSet.getString("borrower_CIN");
                int memberNumber = resultSet.getInt("member_number");

                Borrower borrower = new Borrower(borrowerId, lastName, firstName, email, telephone, CIN, memberNumber);
                borrowers.add(borrower);
            }
        }
        return borrowers;
    }

    public void updateBorrower(int memberNumber, String newLastName, String newFirstName, String newEmail, String newTelephone, String newCIN) throws SQLException {
        // Construction dynamique de la requête SQL
        StringBuilder queryBuilder = new StringBuilder("UPDATE borrowers SET ");
        List<String> setClauses = new ArrayList<>();

        if (!newLastName.isEmpty()) {
            setClauses.add("borrower_lastName = ?");
        }

        if (!newFirstName.isEmpty()) {
            setClauses.add("borrower_firstName = ?");
        }

        if (!newEmail.isEmpty()) {
            setClauses.add("borrower_email = ?");
        }

        if (!newTelephone.isEmpty()) {
            setClauses.add("borrower_telephone = ?");
        }

        if (!newCIN.isEmpty()) {
            setClauses.add("borrower_CIN = ?");
        }

        if (setClauses.isEmpty()) {
            System.out.println("Aucune valeur fournie pour la mise à jour de l'emprunteur.");
            return;
        }

        queryBuilder.append(String.join(", ", setClauses));
        queryBuilder.append(" WHERE member_number = ?");

        String query = queryBuilder.toString();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int parameterIndex = 1;

            if (!newLastName.isEmpty()) {
                statement.setString(parameterIndex++, newLastName);
            }

            if (!newFirstName.isEmpty()) {
                statement.setString(parameterIndex++, newFirstName);
            }

            if (!newEmail.isEmpty()) {
                statement.setString(parameterIndex++, newEmail);
            }

            if (!newTelephone.isEmpty()) {
                statement.setString(parameterIndex++, newTelephone);
            }

            if (!newCIN.isEmpty()) {
                statement.setString(parameterIndex++, newCIN);
            }

            statement.setInt(parameterIndex, memberNumber);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Les informations de l'emprunteur ont été mises à jour avec succès !");
            } else {
                System.out.println("Aucun emprunteur trouvé avec ce numéro de membre.");
            }
        }
    }


    private boolean doesBorrowerExist(int borrowerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM borrowers WHERE borrower_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, borrowerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }



}

