import controllers.AuthorController;
import controllers.BookController;
import models.Author;
import models.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class LibraryManagementApp {
    public static void main(String[] args) {
        DatabaseConnector dbConnector = new DatabaseConnector("jdbc:mysql://localhost:3306/library_management", "root", "");

        if (dbConnector.connect()) {
            System.out.println("Connexion réussie à la base de données.");

            AuthorController authorController = new AuthorController(dbConnector.getConnection());
            BookController bookController = new BookController(dbConnector.getConnection(), authorController);


            try {
                Scanner scanner = new Scanner(System.in);
                int choice;
                do {
                    System.out.println("Menu :");
                    System.out.println("1. Ajouter un auteur");
                    System.out.println("2. Afficher tous les auteurs");
                    System.out.println("3. Mettre à jour un auteur");
                    System.out.println("4. Supprimer un auteur");
                    System.out.println("5. Ajouter un livre");
                    System.out.println("6. Afficher Les livres Disponible ");
                    System.out.println("7. Rechercher un livre ");
                    System.out.println("8. Quitter");
                    System.out.print("Choisissez une option : ");

                    choice = scanner.nextInt();
                    scanner.nextLine();

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
                            scanner.nextLine();

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
                            System.out.println("Ajouter un livre :");
                            List<Author> authors = authorController.getAllAuthors();
                            System.out.println("Liste des auteurs disponibles :");

                            for (int i = 0; i < authors.size(); i++) {
                                System.out.println((i + 1) + ". " + authors.get(i).getAuthorFullName());
                            }

                            System.out.println("Entrez le numéro de l'auteur ou 0 pour créer un nouvel auteur :");
                            int authorChoice = scanner.nextInt();
                            scanner.nextLine();

                            Author author;

                            if (authorChoice == 0) {
                                System.out.println("Entrez le nom complet de l'auteur :");
                                String authorFullName = scanner.nextLine();
                                System.out.println("Entrez la biographie de l'auteur :");
                                String authorBio = scanner.nextLine();

                                authorController.addAuthor(authorFullName, authorBio);

                                author = authorController.getAuthorByFullName(authorFullName);
                            } else if (authorChoice > 0 && authorChoice <= authors.size()) {
                                author = authors.get(authorChoice - 1);
                            } else {
                                System.out.println("Choix invalide. Veuillez choisir un numéro d'auteur valide.");
                                continue; // Revenir au menu principal.
                            }

                            System.out.println("Entrez le titre du livre :");
                            String bookTitle = scanner.nextLine();
                            System.out.println("Entrez la description du livre :");
                            String bookDescription = scanner.nextLine();
                            System.out.println("Entrez l'ISBN du livre :");
                            int bookISBN = scanner.nextInt();
                            scanner.nextLine();
                            System.out.println("Entrez la quantité du livre :");
                            int bookQuantity = scanner.nextInt();
                            scanner.nextLine();

                            boolean bookState = true;
                            bookController.addBook(bookTitle, bookDescription, bookISBN, bookQuantity, bookState, author);
                            break;
                        case 6:
                            try {
                                List<Book> availableBooks = bookController.getAllAvailableBooks();

                                if (availableBooks.isEmpty()) {
                                    System.out.println("Aucun livre disponible.");
                                } else {
                                    System.out.println("Livres disponibles :");
                                    for (Book book : availableBooks) {
                                        System.out.println("Titre : " + book.getBookTitle());
                                        System.out.println("Description : " + book.getBookDescription());
                                        System.out.println("ISBN : " + book.getBookISBN());
                                        System.out.println("Quantité : " + book.getBookQuantity());
                                        System.out.println("Auteur : " + book.getBookAuthor().getAuthorFullName());
                                        System.out.println();
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 7:
                            System.out.println("Recherche de livres par titre ou auteur :");
                            do {
                                System.out.print("Entrez le titre ou le nom de l'auteur : ");
                                String searchTerm = scanner.nextLine();

                                List<Book> matchingBooks = bookController.searchBooks(searchTerm);

                                if (matchingBooks == null) {
                                    System.out.println("Aucun résultat trouvé pour la recherche : " + searchTerm);
                                    System.out.println("Voulez-vous réessayer ? (oui/non)");
                                    String retryChoice = scanner.nextLine();
                                    if (!retryChoice.equalsIgnoreCase("oui")) {
                                        break;
                                    }
                                } else {
                                    if (searchTerm.equalsIgnoreCase(searchTerm)) {
                                        System.out.println("Livres de l'auteur " + searchTerm + " :");
                                        for (Book book : matchingBooks) {
                                            System.out.println("Titre : " + book.getBookTitle());
                                            System.out.println("Description : " + book.getBookDescription());
                                            System.out.println("ISBN : " + book.getBookISBN());
                                            System.out.println("Quantité : " + book.getBookQuantity());
                                            System.out.println("Auteur : " + book.getBookAuthor().getAuthorFullName());
                                            System.out.println();
                                        }
                                    } else {
                                        System.out.println("Livres correspondants :");
                                        for (Book book : matchingBooks) {
                                            System.out.println("Titre : " + book.getBookTitle());
                                            System.out.println("Description : " + book.getBookDescription());
                                            System.out.println("ISBN : " + book.getBookISBN());
                                            System.out.println("Quantité : " + book.getBookQuantity());
                                            System.out.println("Auteur : " + book.getBookAuthor().getAuthorFullName());
                                            System.out.println();
                                        }
                                    }

                                    System.out.println("Voulez-vous réessayer ? (oui/non)");
                                    String retryChoice = scanner.nextLine();
                                    if (!retryChoice.equalsIgnoreCase("oui")) {
                                        break;
                                    }
                                }
                            } while (true);
                            break;

                        case 8:
                            System.out.println("Fin du programme.");
                            break;
                        default:
                            System.out.println("Option invalide. Veuillez choisir une option valide.");
                            break;
                    }
                }  while (choice != 8);

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
