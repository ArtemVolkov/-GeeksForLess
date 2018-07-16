package ua.volkov.electronic_library.dao;

import org.springframework.data.repository.CrudRepository;
import ua.volkov.electronic_library.model.Book;
import ua.volkov.electronic_library.model.Tag;

public interface TagRepository extends CrudRepository<Tag, Integer> {

    Iterable<Tag> findAllByBooksContains(Book book);

    Tag findByNameContains(String name);

}
