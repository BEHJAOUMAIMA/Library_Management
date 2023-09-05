public class LibraryManagementApp {
    public static void main(String[] args) {
        Book book = new Book(1, "La Bible de Jérusalem", "Description du livre", 123456789, 10, true, null);

        Borrower borrower = new Borrower(1, "BEHJA", "Oumaima", "email@example.com", "0691747097", "CIN123", 1001);

        System.out.println("Informations sur le livre : " + book.getBookTitle());
        System.out.println("Informations sur l'emprunteur : " + borrower.getBorrowerLastName() + borrower.getBorrowerFirstName());
    }

}
