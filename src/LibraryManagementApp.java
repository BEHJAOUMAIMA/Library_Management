import controllers.AuthorController;
import models.Author;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class LibraryManagementApp {
    public static void main(String[] args) {
        // Créez une instance de DatabaseConnector en fournissant les détails de connexion
        DatabaseConnector dbConnector = new DatabaseConnector("jdbc:mysql://localhost:3306/library_management", "root", "");

        // Établissez la connexion à la base de données
        if (dbConnector.connect()) {
            System.out.println("Connexion réussie à la base de données.");

            // Créez une instance de AuthorController en fournissant la connexion
            AuthorController authorController = new AuthorController(dbConnector.getConnection());

            try {
                Scanner scanner = new Scanner(System.in);
                int choice;
                do {
                    // Afficher le menu
                    System.out.println("Menu :");
                    System.out.println("1. Ajouter un auteur");
                    System.out.println("2. Afficher tous les auteurs");
                    System.out.println("3. Mettre à jour un auteur");
                    System.out.println("4. Supprimer un auteur");
                    System.out.println("5. Quitter");
                    System.out.print("Choisissez une option : ");

                    // Lire le choix de l'utilisateur
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consommer la ligne en trop

                    switch (choice) {
                        case 1:
                            System.out.println("Entrez le nom complet de l'auteur :");
                            String fullName = scanner.nextLine();
                            System.out.println("Entrez la biographie de l'auteur :");
                            String bio = scanner.nextLine();
                            try {
                                authorController.addAuthor(fullName, bio);
                                System.out.println("L'auteur a été ajouté avec succès !");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 2:
                            try {
                                List<Author> authors = authorController.getAllAuthors();
                                for (Author author : authors) {
                                    System.out.println("ID : " + author.getAuthorId() + ", Nom complet : " + author.getAuthorFullName() + ", Bio : " + author.getAuthorBio());
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 3:
                            System.out.println("Entrez l'ID de l'auteur que vous souhaitez mettre à jour :");
                            int authorIdToUpdate = scanner.nextInt();
                            scanner.nextLine(); // Consommer la ligne en trop

                            System.out.println("Entrez le nouveau nom complet de l'auteur :");
                            String newFullName = scanner.nextLine();

                            System.out.println("Entrez la nouvelle biographie de l'auteur :");
                            String newBio = scanner.nextLine();

                            try {
                                authorController.updateAuthor(authorIdToUpdate, newFullName, newBio);
                                System.out.println("L'auteur a été mis à jour avec succès !");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 4:
                            System.out.println("Entrez l'ID de l'auteur que vous souhaitez supprimer :");
                            int authorIdToDelete = scanner.nextInt();

                            try {
                                authorController.deleteAuthor(authorIdToDelete);
                                System.out.println("L'auteur a été supprimé avec succès !");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 5:
                            System.out.println("Fin du programme.");
                            break;
                        default:
                            System.out.println("Option invalide. Veuillez choisir une option valide.");
                            break;
                    }
                } while (choice != 5);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dbConnector.disconnect();
            }
        } else {
            System.err.println("Échec de la connexion à la base de données.");
        }
    }
}
