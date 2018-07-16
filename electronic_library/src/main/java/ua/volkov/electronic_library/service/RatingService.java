package ua.volkov.electronic_library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.volkov.electronic_library.dao.BookRepository;
import ua.volkov.electronic_library.model.Book;
import ua.volkov.electronic_library.model.User;

@Service
public class RatingService {
    @Autowired
    BookRepository bookRepository;

    public void giveLikeToBook(User user , Book book){
        book.deleteDislike(user);
        int size = book.getLikes().size();
        book.addLike(user);
        if(book.getLikes().size() == size)//if user has like ->  user wants to delete this like;
            book.deleteLike(user);

        bookRepository.save(book);
    }

    public void giveDislikeToBook(User user, Book book){
        book.deleteLike(user);
        int size = book.getDislikes().size();
        book.addDislike(user);
        if(book.getDislikes().size() == size) //if user has dislike ->  user wants to delete this dislike;
            book.deleteDislike(user);

        bookRepository.save(book);
    }
}
