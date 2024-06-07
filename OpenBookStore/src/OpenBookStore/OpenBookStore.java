package OpenBookStore;

import java.util.*;
import java.sql.*;
import java.sql.*;

class Author { private String name; private String email;

public Author(String name, String email) {
    this.name = name;
    this.email = email;
}

public String getName() {
    return name;
}

public String getEmail() {
    return email;
}
}

class Book 
{ private String id; private String title; private Author author; private int numberOfCopies;

public Book(String id, String title, Author author, int numberOfCopies) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.numberOfCopies = numberOfCopies;
}

public String getId() {
    return id;
}

public String getTitle() {
    return title;
}

public Author getAuthor() {
    return author;
}

public int getNumberOfCopies() {
    return numberOfCopies;
}

public void setNumberOfCopies(int numberOfCopies) {
    this.numberOfCopies = numberOfCopies;
}
}

class Bookstore { private Map<String, Book> books;

public Bookstore() {
    books = new HashMap<>();
}

public void addBook(Book book) {
    books.put(book.getId(), book);
}

public void removeBook(String bookId) {
    books.remove(bookId);
}

public Book getBook(String bookId) {
    return books.get(bookId);
}

public List<Book> getAllBooks() {
    return new ArrayList<>(books.values());
}
}

class AuthorManager { private Map<String, Author> authors;

public AuthorManager() {
    authors = new HashMap<>();
}

public void addAuthor(Author author) {
    authors.put(author.getName(), author);
}

public void removeAuthor(String authorName) {
    authors.remove(authorName);
}

public Author getAuthor(String authorName) {
    return authors.get(authorName);
}

public List<Author> getAllAuthors() {
    return new ArrayList<>(authors.values());
}
}

public class OpenBookStore { private Bookstore bookstore; private AuthorManager authorManager;
private static final String JDBC_URL = "jdbc:mysql://localhost:3306/openbook";
private static final String JDBC_USER = "root";
private static final String JDBC_PASSWORD = "barleen";

private Connection connection;

public OpenBookStore() {
    bookstore = new Bookstore();
    authorManager = new AuthorManager();
    try {
        connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        System.out.println("Connected to the database.");
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
}

public void controlPanel() {
    System.out.println("\t\t\t    OOP Project");
    System.out.println("\n\t\t\t OPEN BOOK STORE");
    System.out.println("\n\n\t\t\t\tCONTROL PANEL");
    System.out.println("1. ADD BOOK");
    System.out.println("2. DISPLAY BOOKS");
    System.out.println("3. CHECK PARTICULAR BOOK");
    System.out.println("4. UPDATE BOOK");
    System.out.println("5. DELETE BOOK");
    System.out.println("6. ADD AUTHOR");
    System.out.println("7. DISPLAY AUTHORS");
    System.out.println("8. EXIT");
}


public void addBook() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("\n\n\t\t\t\tADD BOOKS");
    System.out.print("Book ID: ");
    String bookId = scanner.nextLine();
    System.out.print("Book Title: ");
    String bookTitle = scanner.nextLine();
    System.out.print("Author Name: ");
    String authorName = scanner.nextLine();
    System.out.print("Author Email: ");
    String authorEmail = scanner.nextLine();
    System.out.print("No. of Books: ");
    int numberOfCopies = scanner.nextInt();
    scanner.nextLine();

    try {
        // Check if author exists, otherwise insert the author
        PreparedStatement authorQuery = connection.prepareStatement("SELECT id FROM authors WHERE name = ?");
        authorQuery.setString(1, authorName);
        ResultSet authorResult = authorQuery.executeQuery();
        int authorId;
        if (authorResult.next()) {
            authorId = authorResult.getInt("id");
        } else {
            PreparedStatement insertAuthor = connection.prepareStatement("INSERT INTO authors (name, email) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            insertAuthor.setString(1, authorName);
            insertAuthor.setString(2, authorEmail);
            insertAuthor.executeUpdate();
            ResultSet generatedKeys = insertAuthor.getGeneratedKeys();
            generatedKeys.next();
            authorId = generatedKeys.getInt(1);
        }

        // Insert the book
        PreparedStatement insertBook = connection.prepareStatement("INSERT INTO books (id, title, author_id, number_of_copies) VALUES (?, ?, ?, ?)");
        insertBook.setString(1, bookId);
        insertBook.setString(2, bookTitle);
        insertBook.setInt(3, authorId);
        insertBook.setInt(4, numberOfCopies);
        insertBook.executeUpdate();

        System.out.println("Book added successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


public void showBooks() {
    System.out.println("\n\n\t\t\t\tAll BOOKS");
    try {
        if (connection != null && !connection.isClosed()) {
            System.out.println("Connected to the database.");
        } else {
            System.out.println("Connection to the database is not established.");
            return;
        }

        PreparedStatement getBooks = connection.prepareStatement("SELECT b.id AS book_id, b.title AS book_title, a.name AS author_name, a.email AS author_email, b.number_of_copies AS num_copies FROM books b JOIN authors a ON b.author_id = a.id");
        ResultSet resultSet = getBooks.executeQuery();
        if (resultSet.next()) {
            do {
                System.out.println("Book ID: " + resultSet.getString("book_id"));
                System.out.println("Book Title: " + resultSet.getString("book_title"));
                System.out.println("Author Name: " + resultSet.getString("author_name"));
                System.out.println("Author Email: " + resultSet.getString("author_email"));
                System.out.println("No. of Books: " + resultSet.getInt("num_copies"));
                System.out.println();
            } while (resultSet.next());
        } else {
            System.out.println("No books found.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}




public void checkBook() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("\n\n\t\t\t\tCheck Particular Book");
    System.out.print("Book ID: ");
    String bookId = scanner.nextLine();

    try {
        PreparedStatement checkBook = connection.prepareStatement(
            "SELECT b.id AS book_id, b.title AS book_title, a.name AS author_name, a.email AS author_email, b.number_of_copies AS num_copies " +
            "FROM books b " +
            "JOIN authors a ON b.author_id = a.id " +
            "WHERE b.id = ?"
        );
        checkBook.setString(1, bookId);
        ResultSet resultSet = checkBook.executeQuery();
        if (resultSet.next()) {
            System.out.println("Book ID: " + resultSet.getString("book_id"));
            System.out.println("Book Title: " + resultSet.getString("book_title"));
            System.out.println("Author Name: " + resultSet.getString("author_name"));
            System.out.println("Author Email: " + resultSet.getString("author_email"));
            System.out.println("No. of Books: " + resultSet.getInt("num_copies"));
        } else {
            System.out.println("Book ID Not Found...");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}





public void updateBook() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("\n\n\t\t\t\tUpdate Book Record");
    System.out.print("Book ID: ");
    String bookId = scanner.nextLine();

    try {
        // Check if the book exists
        PreparedStatement checkBook = connection.prepareStatement("SELECT * FROM books WHERE id = ?");
        checkBook.setString(1, bookId);
        ResultSet resultSet = checkBook.executeQuery();
        if (resultSet.next()) {
            // Prompt user for new book details
            System.out.print("New Book Title: ");
            String newTitle = scanner.nextLine();
            System.out.print("New Author Name: ");
            String newAuthorName = scanner.nextLine();
            System.out.print("New Author Email: ");
            String newAuthorEmail = scanner.nextLine();
            System.out.print("New No. of Books: ");
            int newNumberOfCopies = scanner.nextInt();
            scanner.nextLine();

            // Retrieve the author's ID based on the provided author name
            PreparedStatement getAuthorId = connection.prepareStatement("SELECT id FROM authors WHERE name = ?");
            getAuthorId.setString(1, newAuthorName);
            ResultSet authorResultSet = getAuthorId.executeQuery();
            int newAuthorId;
            if (authorResultSet.next()) {
                newAuthorId = authorResultSet.getInt("id");
            } else {
                // If the author does not exist, insert a new author and retrieve the generated ID
                PreparedStatement insertAuthor = connection.prepareStatement("INSERT INTO authors (name, email) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                insertAuthor.setString(1, newAuthorName);
                insertAuthor.setString(2, newAuthorEmail);
                insertAuthor.executeUpdate();
                ResultSet generatedKeys = insertAuthor.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newAuthorId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to insert author.");
                }
            }

            // Update the book details in the database
            PreparedStatement updateBook = connection.prepareStatement(
                "UPDATE books " +
                "SET title = ?, author_id = ?, number_of_copies = ? " +
                "WHERE id = ?"
            );
            updateBook.setString(1, newTitle);
            updateBook.setInt(2, newAuthorId);
            updateBook.setInt(3, newNumberOfCopies);
            updateBook.setString(4, bookId);
            int rowsAffected = updateBook.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Book updated successfully.");
            } else {
                System.out.println("Failed to update book.");
            }
        } else {
            System.out.println("Book ID Not Found...");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}




public void deleteBook() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("\n\n\t\t\t\tDelete a Book");
    System.out.print("Book ID: ");
    String bookId = scanner.nextLine();

    try {
        // Check if the book exists
        PreparedStatement checkBook = connection.prepareStatement("SELECT * FROM books WHERE id = ?");
        checkBook.setString(1, bookId);
        ResultSet resultSet = checkBook.executeQuery();
        if (resultSet.next()) {
            // Delete the book from the database
            PreparedStatement deleteBook = connection.prepareStatement("DELETE FROM books WHERE id = ?");
            deleteBook.setString(1, bookId);
            int rowsAffected = deleteBook.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book is Deleted Successfully...");
            } else {
                System.out.println("Failed to delete book.");
            }
        } else {
            System.out.println("Book ID Not Found...");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


public void addAuthor() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("\n\n\t\t\t\tADD AUTHORS");
    System.out.print("Author ID: ");
    int authorId = scanner.nextInt();
    scanner.nextLine(); // Consume the newline character
    System.out.print("Author Name: ");
    String authorName = scanner.nextLine();
    System.out.print("Author Email: ");
    String authorEmail = scanner.nextLine();

    try {
        // Check if the author already exists in the database
        PreparedStatement checkAuthor = connection.prepareStatement("SELECT * FROM authors WHERE id = ?");
        checkAuthor.setInt(1, authorId);
        ResultSet resultSet = checkAuthor.executeQuery();
        if (!resultSet.next()) {
            // If the author does not exist, insert the author into the database
            PreparedStatement insertAuthor = connection.prepareStatement("INSERT INTO authors (id, name, email) VALUES (?, ?, ?)");
            insertAuthor.setInt(1, authorId);
            insertAuthor.setString(2, authorName);
            insertAuthor.setString(3, authorEmail);
            int rowsAffected = insertAuthor.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Author added successfully.");
            } else {
                System.out.println("Failed to add author.");
            }
        } else {
            System.out.println("Author ID already exists in the database.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


public void showAuthors() {
    try {
        System.out.println("\n\n\t\t\t\tAll AUTHORS");
        // Retrieve all authors from the database
        PreparedStatement selectAuthors = connection.prepareStatement("SELECT * FROM authors");
        ResultSet resultSet = selectAuthors.executeQuery();
        boolean found = false;
        while (resultSet.next()) {
            found = true;
            System.out.println("Author ID: " + resultSet.getInt("id"));
            System.out.println("Author Name: " + resultSet.getString("name"));
            System.out.println("Author Email: " + resultSet.getString("email"));
            System.out.println();
        }
        if (!found) {
            System.out.println("No authors found.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


public void run() {
    Scanner scanner = new Scanner(System.in);
    int choice;

    do {
        controlPanel();
        System.out.print("Enter your choice (1-8): ");
        choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                addBook();
                break;
            case 2:
                showBooks();
                break;
            case 3:
                checkBook();
                break;
            case 4:
                updateBook();
                break;
            case 5:
                deleteBook();
                break;
            case 6:
                addAuthor();
                break;
            case 7:
                showAuthors();
                break;
            case 8:
                System.out.println("\nThank you for using the Open Book Store");
                break;
            default:
                System.out.println("\nInvalid choice. Please try again.");
        }
    } while (choice != 8);
}

public static void main(String[] args) {
	OpenBookStore openbookstore = new OpenBookStore();
  openbookstore.run();
   
}
}
