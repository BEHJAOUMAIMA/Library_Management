public class Book {
    //attributes
    private int book_id;
    private String book_title;
    private String book_description;
    private int book_ISBN;
    private int book_quantity;
    private boolean book_state;
    private Author author;

    //construct
    public Book(int book_id, String book_title, String book_description, int book_ISBN, int book_quantity, boolean book_state, Author author) {
        this.book_id = book_id;
        this.book_title = book_title;
        this.book_description = book_description;
        this.book_ISBN = book_ISBN;
        this.book_quantity = book_quantity;
        this.book_state = book_state;
        this.author = author;
    }

    // getters & setters
    public int getId() {
        return book_id;
    }
    public void setId(int id) {
        this.book_id = id;
    }
    public String getBookTitle() {
        return book_title;
    }
    public void setBookTitle(String title) {
        this.book_title = title;
    }
    public String getBookDescription() {
        return book_description;
    }
    public void setBookDescription(String description) {
        this.book_description = description;
    }
    public int getBookISBN() {
        return book_ISBN;
    }
    public void setBookISBN(int ISBN_number) {
        this.book_ISBN = ISBN_number;
    }
    public int getBookQuantity() {
        return book_quantity;
    }
    public void setBookQuantity(int quantity) {
        this.book_quantity = quantity;
    }
    public boolean getBookState() {
        return book_state;
    }
   public void setBookState(boolean state){
        this.book_state = state;
   }
    public Author getBookAuthor() {
        return author;
    }
    public void setBookAuthor(Author author) {
        this.author = author;
    }
}
