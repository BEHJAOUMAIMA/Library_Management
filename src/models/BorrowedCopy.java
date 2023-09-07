package models;

public class BorrowedCopy {
    private int borrowedCopy_id;
    private String borrowedCopy_status;
    private Book book;
    private Loan loan;
    public BorrowedCopy(int borrowedCopyId, String status, Book book, Loan loan) {
        this.borrowedCopy_id = borrowedCopyId;
        this.borrowedCopy_status = status;
        this.book = book;
        this.loan = loan;
    }
    public int getBorrowedCopyId() {
        return borrowedCopy_id;
    }

    public void setBorrowedCopyId(int borrowedCopyId) {
        this.borrowedCopy_id = borrowedCopyId;
    }

    public String getBorrowedCopyStatus() {
        return borrowedCopy_status;
    }

    public void setBorrowedCopyStatus(String status) {
        this.borrowedCopy_status = status;
    }

    public Book getBookCopy() {
        return book;
    }

    public void setBookCopy(Book book) {
        this.book = book;
    }

    public Loan getLoan() {
        return loan;
    }
    public void setLoan(Loan loan) {
        this.loan = loan;
    }

}
