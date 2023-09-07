package models;

public class Author {
    private int author_id;
    private String author_fullName;
    private String author_bio;

    // Constructor
    public Author(int author_id, String author_fullName, String author_bio) {
        this.author_id = author_id;
        this.author_fullName = author_fullName;
        this.author_bio = author_bio;
    }

    // Getters et setters
    public int getAuthorId() {
        return author_id;
    }
    public void setAuthorId(int id) {
        this.author_id = id;
    }
    public String getAuthorFullName() {
        return author_fullName;
    }
    public void setAuthorFullName(String fullName) {
        this.author_fullName = fullName;
    }
    public String getAuthorBio() {
        return author_bio;
    }
    public void setBio(String bio) {
        this.author_bio = bio;
    }
}
