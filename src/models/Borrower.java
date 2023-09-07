package models;

public class Borrower {
    private int borrower_id;
    private String borrower_lastName;
    private String borrower_firstName;
    private String borrower_email;
    private String borrower_telephone;
    private String borrower_CIN;
    private int member_number;

    // Constructor
    public Borrower(int borrower_id, String borrower_lastName, String borrower_firstName, String borrower_email, String borrower_telephone, String borrower_CIN, int member_number) {
        this.borrower_id = borrower_id;
        this.borrower_lastName = borrower_lastName;
        this.borrower_firstName = borrower_firstName;
        this.borrower_email = borrower_email;
        this.borrower_telephone = borrower_telephone;
        this.borrower_CIN = borrower_CIN;
        this.member_number = member_number;
    }

    // Getters et setters
    public int getBorrowerId() {
        return borrower_id;
    }

    public String getBorrowerLastName() {
        return borrower_lastName;
    }

    public void setBorrowerLastName(String lastName) {
        this.borrower_lastName = lastName;
    }

    public String getBorrowerFirstName() {
        return borrower_firstName;
    }

    public void setBorrowerFirstName(String firstName) {
        this.borrower_firstName = firstName;
    }

    public String getBorrowerEmail() {
        return borrower_email;
    }

    public void setBorrowerEmail(String email) {
        this.borrower_email = email;
    }

    public String getBorrowerTelephone() {
        return borrower_telephone;
    }

    public void setBorrowerTelephone(String telephone) {
        this.borrower_telephone = telephone;
    }

    public String getBorrowerCIN() {
        return borrower_CIN;
    }

    public void setBorrowerCIN(String number_CIN) {
        this.borrower_CIN = number_CIN;
    }
    public int getMemberNumber() {
        return member_number;
    }

    public void setMemberNumber(int member_number) {
        this.member_number = member_number;
    }

}
