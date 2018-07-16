package ua.volkov.electronic_library.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import ua.volkov.electronic_library.model.Book;
import ua.volkov.electronic_library.model.Tag;

import java.util.Optional;

public interface BookRepository extends PagingAndSortingRepository<Book, Integer> {

    Iterable<Book>findAllByTagsContainingOrderByName(Tag tag);

    Iterable<Book>findAllByTagsContainingOrderByAddDate(Tag tag);


    Optional<Book> findBookByName(String name);
}
