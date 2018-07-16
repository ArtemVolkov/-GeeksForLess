package ua.volkov.electronic_library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.volkov.electronic_library.dao.CommentRepository;
import ua.volkov.electronic_library.model.Book;
import ua.volkov.electronic_library.model.Comment;

import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    public Optional<Comment> findById(int id){
        return commentRepository.findById(id);
    }

    public Comment save(Comment newComment){
        return commentRepository.save(newComment);
    }

    public Iterable<Comment> findAllParentComments(Book book){
        return commentRepository.findAllByBookAndParentIsNull(book);
    }
}
