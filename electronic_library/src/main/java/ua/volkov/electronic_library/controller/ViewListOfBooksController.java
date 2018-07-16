package ua.volkov.electronic_library.controller;

import org.hibernate.boot.jaxb.SourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.volkov.electronic_library.dao.BookRepository;
import ua.volkov.electronic_library.dao.TagRepository;
import ua.volkov.electronic_library.model.Book;
import ua.volkov.electronic_library.model.Tag;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Controller
@RequestMapping("/books")
public class ViewListOfBooksController {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    public String firstPage(Map<String, Object> model){
        return "redirect:/books/page1";
    }

    @GetMapping("/page{id}")
    public String listOfBooks(@PathVariable int id, Map<String, Object> model){
        long entitiesCount = bookRepository.count();
        if(!this.isPageIdValid(id,entitiesCount)) //if pageId is invalid than redirect to a valid page
            return validatePageId(id,entitiesCount);

        if(id-1 > 0) //if previous page exists
            model.put("prevPage", id-1);

        model.put("currentPage",id);

        if(id+1 <= entitiesCount/20+1) //if next page exists
            model.put("nextPage",id+1);

        model.put("books", bookRepository.findAll(new PageRequest(id-1,20)));

        return "listOfBooks.html";
    }

    @GetMapping("/search")
    public String searchByTag(@RequestParam("name") String name,
                              @RequestParam String description,
                              @RequestParam(name = "tags") String tags,
                              @RequestParam(name = "sort") String sort,
                              Model model){
        if(isAllfieldsNotFilled(name,description,tags)){
            if(sort.equals("Название"))
                model.addAttribute("books", bookRepository.findAll(Sort.by("name")));
            else model.addAttribute("books" , bookRepository.findAll(Sort.by("addDate")));
            return "listOfBooks.html";
        }

        List<Tag> tagsResultList= findTagsByName(tags.trim().split(", "));
        List<Book> books = findBooksByTags(tagsResultList);

        if(!name.trim().equals(""))
            books= books.stream().filter((book)-> book.getName().contains(name.trim())).collect(Collectors.toList());

        if(description.trim().length()>0)
            books= books.stream().filter((book)-> book.getDescription().contains(description.trim())).collect(Collectors.toList());


        if(sort.equals("Название"))
            model.addAttribute("books", books.stream()
                    .sorted((b1,b2)-> b1.getName().compareToIgnoreCase(b2.getName()))
                    .collect(Collectors.toList()));
        else
            model.addAttribute("books",books.stream()
                    .sorted(Comparator.comparing(Book::getAddDate))
                    .collect(Collectors.toList()));
        return "listOfBooks.html";

    }

    private boolean isPageIdValid(int pageId, long entitiesCount){
        if(pageId<1) return false;
        if(pageId-1 > entitiesCount/20) return false;
        return true;
    }
    private String validatePageId(int pageId, long entitiesCount){
        if(pageId<1)
            return "redirect:/books/page1";

        return "redirect:/books/page"+(entitiesCount/20+1);
    }

    private List<Book> findBooksByTags(List<Tag> tags) {
        if(tags.size()==0) return (List<Book>) bookRepository.findAll();
        List<Book> books = (List<Book>) bookRepository.findAll();
        List<Book> resultBooks = books.stream()
                .filter(book -> book.getTags().containsAll(tags))
                .collect(Collectors.toList());
        return resultBooks;
    }

    private List<Tag> findTagsByName(String[] tagNames){
        List<Tag> tag = (List)tagRepository.findAll();

        List<Tag> tagsResultList = new LinkedList<Tag>();

        for(String s: tagNames){
            if(!s.equals(""))
                tagsResultList.addAll(tag.stream()
                    .filter((tag1 -> tag1.getName().contains(s)))
                    .collect(Collectors.toList())
            );
        }

        return tagsResultList;
    }

    private boolean isAllfieldsNotFilled(String... fields){
        boolean flag = true;
        for(String s: fields)
            if (!s.trim().equals("")){
                flag = false;
                break;
            }
        return flag;
    }

}
