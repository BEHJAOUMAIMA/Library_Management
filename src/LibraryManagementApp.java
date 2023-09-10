import controllers.AuthorController;
import controllers.BookController;
import controllers.BorrowerController;
import controllers.LoanController;
import models.Author;
import models.Book;
import models.Borrower;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class LibraryManagementApp {
    public static void main(String[] args) {
        DatabaseConnector dbConnector = new DatabaseConnector("jdbc:mysql://localhost:3306/library_management", "root", "");

        if (dbConnector.connect()) {
            System.out.println("Connexion réussie à la base de données.");

            AuthorController authorController = new AuthorController(dbConnector.getConnection());
            BookController bookController = new BookController(dbConnector.getConnection(), authorController);
            BorrowerController BorrowerController = new BorrowerController(dbConnector.getConnection());
            LoanController LoanController = new LoanController(dbConnector.getConnection());

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
                    System.out.println("8. Modifier un livre ");
                    System.out.println("9. Supprimer un livre ");
                    System.out.println("10. Ajouter un Emprunteur");
                    System.out.println("11. Afficher les Emprunteurs");
                    System.out.println("12. Modifier les informations d'un emprunteur");
                    System.out.println("13. Emprunter un livre");
                    System.out.println("14. Afficher Les details des livres Empruntés ");
                    System.out.println("15. Retourner un livre ");
                    System.out.println("16. Quitter");
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
                                continue;
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

                            String bookState = "disponible";
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
                            System.out.println("Mettre à jour un livre :");
                            System.out.println("Entrez l'ISBN du livre que vous souhaitez mettre à jour :");
                            int updateISBN = scanner.nextInt();
                            scanner.nextLine();

                            bookController.updateBook(updateISBN);
                            break;
                        case 9:
                            System.out.println("Supprimer un Livre :");
                            System.out.println("Entrez l'ISBN du livre que vous souhaitez supprimé :");
                            int deleteISBN = scanner.nextInt();
                            scanner.nextLine();

                            bookController.deleteBook(deleteISBN);
                            break;
                        case 10:
                            System.out.println("Ajouter un emprunteur :");
                            String lastName, firstName, email, telephone, CIN;
                            int memberNumber;
                            do {
                                System.out.println("Numéro de membre (doit être unique) : ");
                                memberNumber = scanner.nextInt();
                                scanner.nextLine();
                                if (BorrowerController.isMemberNumberUnique(memberNumber)) {
                                    break;
                                } else {
                                    System.out.println("Ce numéro de membre existe déjà. Veuillez en choisir un autre.");
                                }
                            } while (true);

                                System.out.println("Nom de famille : ");
                                lastName = scanner.nextLine();
                                System.out.println("Prénom : ");
                                firstName = scanner.nextLine();
                                System.out.println("Email : ");
                                email = scanner.nextLine();
                                System.out.println("Téléphone : ");
                                telephone = scanner.nextLine();
                                System.out.println("CIN : ");
                                CIN = scanner.nextLine();

                                BorrowerController.addBorrower(lastName, firstName, email, telephone, CIN, memberNumber);
                            break;
                        case 11:
                            try {
                                List<Borrower> borrowers = BorrowerController.getAllBorrowers();

                                if (borrowers.isEmpty()) {
                                    System.out.println("Aucun emprunteur enregistré.");
                                } else {
                                    System.out.println("Liste des emprunteurs :");
                                    for (Borrower borrower : borrowers) {
                                        System.out.println("ID : " + borrower.getBorrowerId());
                                        System.out.println("Nom : " + borrower.getBorrowerLastName());
                                        System.out.println("Prénom : " + borrower.getBorrowerFirstName());
                                        System.out.println("Email : " + borrower.getBorrowerEmail());
                                        System.out.println("Téléphone : " + borrower.getBorrowerTelephone());
                                        System.out.println("CIN : " + borrower.getBorrowerCIN());
                                        System.out.println("Numéro de membre : " + borrower.getMemberNumber());
                                        System.out.println();
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 12:
                            System.out.print("Entrez le numéro de membre de l'emprunteur que vous souhaitez mettre à jour : ");
                            int memberNumberToUpdate = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("Nouveau nom de famille (laissez vide pour conserver la valeur actuelle) : ");
                            String newLastName = scanner.nextLine();
                            System.out.print("Nouveau prénom (laissez vide pour conserver la valeur actuelle) : ");
                            String newFirstName = scanner.nextLine();
                            System.out.print("Nouvelle adresse e-mail (laissez vide pour conserver la valeur actuelle) : ");
                            String newEmail = scanner.nextLine();
                            System.out.print("Nouveau numéro de téléphone (laissez vide pour conserver la valeur actuelle) : ");
                            String newTelephone = scanner.nextLine();
                            System.out.print("Nouveau CIN (laissez vide pour conserver la valeur actuelle) : ");
                            String newCIN = scanner.nextLine();

                            try {
                                BorrowerController.updateBorrower(memberNumberToUpdate, newLastName, newFirstName, newEmail, newTelephone, newCIN);
                            } catch (SQLException e) {
                                System.err.println("Erreur lors de la mise à jour de l'emprunteur : " + e.getMessage());
                            }
                            break;
                        case 13:
                            System.out.println("Emprunter un livre :");

                            int numeroMembre = -1;
                            String memberLastName = "";
                            String memberFirstName = "";
                            String memberEmail = "";
                            String memberTelephone = "";
                            String memberCIN = "";

                            if (numeroMembre == -1) {
                                System.out.println("Vous devez être membre pour emprunter un livre.");
                                System.out.println("Êtes-vous déjà membre de la bibliothèque ? (oui/non)");
                                String reponse = scanner.nextLine().toLowerCase();

                                if (reponse.equals("oui")) {
                                    System.out.print("Entrez votre numéro de membre : ");
                                    numeroMembre = scanner.nextInt();
                                    scanner.nextLine();
                                } else if (reponse.equals("non")) {
                                    System.out.print("Entrez votre nom de famille : ");
                                    memberLastName = scanner.nextLine();
                                    System.out.print("Entrez votre prénom : ");
                                    memberFirstName = scanner.nextLine();
                                    System.out.print("Entrez votre email : ");
                                    memberEmail = scanner.nextLine();
                                    System.out.print("Entrez votre numéro de téléphone : ");
                                    memberTelephone = scanner.nextLine();
                                    System.out.print("Entrez votre CIN : ");
                                    memberCIN = scanner.nextLine();
                                    System.out.print("Entrez un numéro de membre unique : ");
                                    numeroMembre = scanner.nextInt();
                                    scanner.nextLine();

                                    BorrowerController.addBorrower(memberLastName, memberFirstName, memberEmail, memberTelephone, memberCIN, numeroMembre);
                                    System.out.println("Vous êtes désormais membre de la bibliothèque. Vous pouvez emprunter un livre.");
                                } else {
                                    System.out.println("Réponse invalide. Veuillez répondre par 'oui' ou 'non'.");
                                    break;
                                }
                            }

                            System.out.print("Entrez l'ISBN ou le titre du livre que vous souhaitez emprunter : ");
                            String isbnOuTitre = scanner.nextLine();
                            System.out.print("Entrez la date d'emprunt (AAAA-MM-JJ) : ");
                            String dateEmpruntStr = scanner.nextLine();
                            System.out.print("Entrez la date de retour prévue (AAAA-MM-JJ) : ");
                            String dateRetourStr = scanner.nextLine();

                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date dateEmprunt = dateFormat.parse(dateEmpruntStr);
                                Date dateRetour = dateFormat.parse(dateRetourStr);

                                LoanController.borrowBook(isbnOuTitre, numeroMembre, dateEmprunt, dateRetour);
                            } catch (ParseException e) {
                                System.out.println("Format de date invalide. Assurez-vous d'utiliser le format AAAA-MM-JJ.");
                            }
                           
                            break;
                        case 14:
                            List<String> borrowedBooksDetails = LoanController.getBorrowedBooksDetails();

                            if (borrowedBooksDetails.isEmpty()) {
                                System.out.println("Aucun livre emprunté pour le moment.");
                            } else {
                                System.out.println("Livres empruntés avec les informations des emprunteurs :");
                                for (String details : borrowedBooksDetails) {
                                    System.out.println(details);
                                }
                            }
                            break;
                        case 15:
                            System.out.print("Entrez l'ISBN du livre que vous souhaitez retourner : ");
                            int isbnToReturn = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("Entrez le numéro de membre de l'emprunteur : ");
                            memberNumber = scanner.nextInt();
                            scanner.nextLine();

                            try {
                                boolean success = LoanController.returnBorrowedBook(isbnToReturn, memberNumber);

                                if (success) {
                                    System.out.println("Le livre a été retourné avec succès.");
                                } else {
                                    System.out.println("Échec du retour du livre. Vérifiez l'ISBN ou le numéro de membre.");
                                }
                            } catch (SQLException e) {
                                System.err.println("Erreur lors du retour du livre : " + e.getMessage());
                                e.printStackTrace();
                            }
                            break;
                        case 16:
                            System.out.println("Fin du programme.");
                            break;
                        default:
                            System.out.println("Option invalide. Veuillez choisir une option valide.");
                            break;
                    }
                }  while (choice != 16);

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
