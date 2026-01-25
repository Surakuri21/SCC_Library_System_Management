package model;

public class Books {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private int quantity;
    private String category; // New Field

    public Books(int id, String title, String author, String isbn, int quantity, String category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.quantity = quantity;
        this.category = (category == null || category.isEmpty()) ? "General" : category;
    }

    // Constructor for backward compatibility (Kung kinakailangan)
    public Books(int id, String title, String author, String isbn, int quantity) {
        this(id, title, author, isbn, quantity, "General");
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // Computed Property for UI
    public String getStatus() {
        return (quantity > 0) ? "Available" : "Out of Stock";
    }
}