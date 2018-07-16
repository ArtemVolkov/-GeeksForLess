package ua.volkov.electronic_library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.volkov.electronic_library.dao.BookRepository;
import ua.volkov.electronic_library.dao.UserRepository;
import ua.volkov.electronic_library.model.Book;
import ua.volkov.electronic_library.model.User;
import ua.volkov.electronic_library.service.RatingService;


import java.util.Map;


@Controller
public class RatingController {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RatingService ratingService;


    @GetMapping("/book/like")
    public String likeBook(@RequestParam("username") String username,
                           @RequestParam("book_id") int bookId,
                           Map<String,Object> model){
        Book book = bookRepository.findById(bookId).get();
        User user = userRepository.findByLogin(username);
        if(user == null || book == null) {
            model.put("aaa", "Ошибка установки оценки книге!");
            return "error.html";
        }

        ratingService.giveLikeToBook(user,book);

        return "redirect:/book?bookId=" + bookId;


    }

    @GetMapping("/book/dis")
    public String dislikeBook(@RequestParam("username") String username,
                           @RequestParam("book_id") int bookId){
        Book book = bookRepository.findById(bookId).get();
        User user = userRepository.findByLogin(username);
        if(user == null || book == null) throw new IllegalArgumentException("Book or User is invalid!");

        ratingService.giveDislikeToBook(user, book);
        return "redirect:/book?bookId=" + bookId;

    }
}
