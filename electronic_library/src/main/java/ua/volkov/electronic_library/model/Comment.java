package ua.volkov.electronic_library.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supercomment_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent" , fetch = FetchType.EAGER)
    private Set<Comment> subComments;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usr_id")
    private User author;

    @Column(name="text")
    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name= "book_id")
    private Book book;


    public Comment(){

    }

    public Comment(User author,String text, Book book){
        this.author = author;
        this.text = text;
        this.book = book;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public Set<Comment> getSubComments() {
        return subComments;
    }

    public void setSubComments(Set<Comment> subComments) {
        this.subComments = subComments;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
