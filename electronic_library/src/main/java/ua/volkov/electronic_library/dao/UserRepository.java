package ua.volkov.electronic_library.dao;

import org.springframework.data.repository.CrudRepository;
import ua.volkov.electronic_library.model.User;

public interface UserRepository extends CrudRepository<User,Integer> {

    User findByLogin(String login);
}
