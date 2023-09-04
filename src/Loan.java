import java.util.Date;

public class Loan {
    //attributes
    private Date loan_date;
    private Date return_date;

    //constructor
    public Loan(Date loan_date) {
        this.loan_date = loan_date;
        this.return_date = null;
    }

    //setters & getters
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

}
