import java.util.Scanner;

public class LibraryManagementApp {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/library_management";
        String username = "root";
        String password = "";

        DatabaseConnector connector = new DatabaseConnector(url, username, password);

        if (connector.connect()) {
            System.out.println("Connexion à la base de données réussie !");



            connector.disconnect();
        } else {
            System.out.println("La connexion à la base de données a échoué.");
        }
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("Menu :");
            System.out.println("1. Ajouter un auteur");
            System.out.println("2. Ajouter un livre");
            System.out.println("3. Quitter");
            System.out.print("Choisissez une option : ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:

                    break;
                case 2:

                    break;
                case 3:
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Option invalide. Veuillez choisir une option valide.");
                    break;
            }

        } while (choice != 3);

        scanner.close();

        //Book book = new Book(1, "La Bible de Jérusalem", "Description du livre", 123456789, 10, true, null);
        //Borrower borrower = new Borrower(1, "BEHJA", "Oumaima", "email@example.com", "0691747097", "CIN123", 1001);
        //System.out.println("Informations sur le livre : " + book.getBookTitle());
        //System.out.println("Informations sur l'emprunteur : " + borrower.getBorrowerLastName() + borrower.getBorrowerFirstName());

    }

}
