import java.util.Date;

public class Loan {
    //attributes
    private int loan_id;
    private Date loan_date;
    private Date return_date;
    private Borrower borrower;

    //constructor
    public Loan(int loan_id, Date loan_date, Borrower borrower) {
        this.loan_id = loan_id;
        this.loan_date = loan_date;
        this.return_date = null;
        this.borrower = borrower;
    }

    //setters & getters
    public int getLoanId(){
        return loan_id;
    }
    public void setLoanId(int loan_id){
        this.loan_id = loan_id;
    }
    public Date getLoanDate() {
        return loan_date;
    }

    public void setLoanDate(Date loanDate) {
        this.loan_date = loanDate;
    }

    public Date getReturnDate() {
        return return_date;
    }

    public void setReturnDate(Date returnDate) {
        this.return_date = returnDate;
    }

    public void returnBook(Date returnDate) {
        this.return_date = returnDate;
    }
    public Borrower getBorrower() {
        return borrower;
    }
    public void setBorrower(Borrower borrower) {
        this.borrower = borrower;
    }

}
