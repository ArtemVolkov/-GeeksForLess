package ua.volkov.electronic_library.dao;

import org.springframework.data.repository.CrudRepository;
import ua.volkov.electronic_library.model.Book;
import ua.volkov.electronic_library.model.Comment;

public interface CommentRepository extends CrudRepository<Comment, Integer> {


    Iterable<Comment> findAllByBook(Book book);

    Iterable<Comment> findAllByBookAndParentIsNull(Book book);
}
