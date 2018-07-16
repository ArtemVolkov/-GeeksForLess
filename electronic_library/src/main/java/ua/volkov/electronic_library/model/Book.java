package ua.volkov.electronic_library.model;

import javax.persistence.*;
import java.sql.Blob;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int book_id;

    @Column(name="name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "author")
    private String author;

    @Column(name = "publisher")
    private String publisher;

    @Column(name="file")
    private String file;

    @Column(name="preview")
    private String preview;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<Comment>();

    @ManyToMany
    @JoinTable(name = "books_tags",
            joinColumns = @JoinColumn(name = "book_id") ,
            inverseJoinColumns = @JoinColumn(name = "tag_id") )
    private Set<Tag> tags = new HashSet<Tag>();

    @ManyToMany
    @JoinTable(name = "book_user_likes",
            joinColumns = @JoinColumn(name = "book_id") ,
            inverseJoinColumns = @JoinColumn(name = "usr_id"))
    private Set<User> likes = new HashSet<User>();

    @ManyToMany
    @JoinTable(name = "book_user_dislikes",
            joinColumns = @JoinColumn(name = "book_id") ,
            inverseJoinColumns = @JoinColumn(name = "usr_id"))
    private Set<User> dislikes = new HashSet<User>();

    @Column(name = "add_date", columnDefinition = "DATETIME")
    private Date addDate;

    public Book(){

    }

    public Book(String name, String author, String publisher, String description, Date addDate){
        this.name=name;
        this.author=author;
        this.publisher=publisher;
        this.description=description;
        this.addDate = addDate;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void addTags(Tag tag){
        this.tags.add(tag);
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }

    public Set<User> getDislikes() {
        return dislikes;
    }

    public void setDislikes(Set<User> dislikes) {
        this.dislikes = dislikes;
    }

    public void addLike(User user){
        this.likes.add(user);
    }

    public void deleteLike(User user){
        this.likes.remove(user);
    }

    public void addDislike(User user){
        this.dislikes.add(user);
    }

    public void deleteDislike(User user){
        this.dislikes.remove(user);
    }

    private Set<Comment> getParrentComments(){
       return this.getComments().stream()
               .filter((comment -> comment.getParent()==null))
               .collect(Collectors.toSet());
    }

    public String findCommentQueue(){
        Map<Comment, Integer> commentsMap = new LinkedHashMap<Comment, Integer>();
        int parent=0;
        for(Comment p: this.getParrentComments()){
            dfsSearchComments(p, commentsMap, parent);
        }

        return generateHtmlComments(commentsMap);
    }

    private String generateHtmlComments(Map<Comment,Integer> map){
        StringBuilder stringBuilder= new StringBuilder();
        stringBuilder.append("<table>");
        for(Comment com: map.keySet()){
            int generation = map.get(com);
            //<td th:text="'Пользователь: ' + ${comment.author.login} + ' Id комментария: ' + ${comment.id}"></td>
            if(generation==0){
                stringBuilder.append("<tr>\n<td>");
                stringBuilder.append("Пользователь: <strong>" + com.getAuthor().getLogin()+ "</strong> Id комментария: " + com.getId());
                stringBuilder.append("</td>\n</tr>");
                stringBuilder.append("<tr><td>" + com.getText() + "</td></tr>" );
            }
            else{
                stringBuilder.append("<table style=\"padding-left:"+ generation*20 + "px\"><tr><td>");
                stringBuilder.append("Пользователь: <strong>" + com.getAuthor().getLogin()+ "</strong> в ответ на комментарий <strong>"
                        + com.getParent().getAuthor().getLogin() +  "</strong> Id комментария: " + com.getId());
                stringBuilder.append("</td>\n</tr>");
                stringBuilder.append("<tr><td>" + com.getText() + "</td></tr></table>" );
            }


            //<tr>                    <td th:utext="${comment.text}"></td>        </tr>
        }
        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

    private void dfsSearchComments(Comment p, Map<Comment,Integer> commentSet, int generationCount){
        commentSet.put(p,generationCount);
        for(Comment child: p.getSubComments()){
            dfsSearchComments(child, commentSet, generationCount+1);
        }

    }
}
