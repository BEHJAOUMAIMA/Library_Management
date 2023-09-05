
public class LibraryManagementApp {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/library_management";
        String username = "root";
        String password = "";

        DatabaseConnector connector = new DatabaseConnector(url, username, password);

        if (connector.connect()) {
            System.out.println("Connexion à la base de données réussie !");

            // effectuer des opérations sur la base de données

            connector.disconnect();
        } else {
            System.out.println("La connexion à la base de données a échoué.");
        }

        //Book book = new Book(1, "La Bible de Jérusalem", "Description du livre", 123456789, 10, true, null);
        //Borrower borrower = new Borrower(1, "BEHJA", "Oumaima", "email@example.com", "0691747097", "CIN123", 1001);
        //System.out.println("Informations sur le livre : " + book.getBookTitle());
        //System.out.println("Informations sur l'emprunteur : " + borrower.getBorrowerLastName() + borrower.getBorrowerFirstName());

    }

}
