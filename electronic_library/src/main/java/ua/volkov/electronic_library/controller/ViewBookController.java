package ua.volkov.electronic_library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.volkov.electronic_library.dao.BookRepository;
import ua.volkov.electronic_library.dao.CommentRepository;
import ua.volkov.electronic_library.dao.TagRepository;
import ua.volkov.electronic_library.dao.UserRepository;
import ua.volkov.electronic_library.model.Book;
import ua.volkov.electronic_library.model.Comment;
import ua.volkov.electronic_library.model.Tag;
import ua.volkov.electronic_library.service.CommentService;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/book")
public class ViewBookController {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String viewBook(@RequestParam(name = "bookId") int id, Map<String, Object> model){
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book bookFromDb= bookOptional.get();
            model.put("book",bookFromDb);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            model.put("authUser", username);
            model.put("islike", bookFromDb.getLikes().contains(userRepository.findByLogin(username)) );
            model.put("isdislike", bookFromDb.getDislikes().contains(userRepository.findByLogin(username)));
            return "ViewBook.html";
        }
        model.put("aaa", "Ошибка нахождения книги!");
        return "error.html";
    }

    @PostMapping
    public String addComment(@RequestParam String comment,
                             @RequestParam String username,
                             @RequestParam( name = "parent") String parentId,
                             @RequestParam(name = "book_id") int bookId){
        Comment newComment = new Comment(userRepository.findByLogin(username) , comment, bookRepository.findById(bookId).get());
        int parsedParentId = this.parseStringToInt(parentId);
        if(parsedParentId != -1){
            Optional<Comment> commentOpt = commentService.findById(parsedParentId);
            if(commentOpt.isPresent() && commentOpt.get().getBook().getBook_id()==bookId)
                newComment.setParent(commentService.findById(parsedParentId).get());
        }

        commentService.save(newComment);
        return "redirect:/book?bookId="+bookId;

    }


    private int parseStringToInt(String text) {
        int res;
        try {
            res = Integer.parseInt(text);
        } catch (Throwable e) {
            res = -1;
        }
        return res;
    }
}
