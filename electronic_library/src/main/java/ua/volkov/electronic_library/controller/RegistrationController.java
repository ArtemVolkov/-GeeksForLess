package ua.volkov.electronic_library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ua.volkov.electronic_library.dao.UserRepository;
import ua.volkov.electronic_library.model.Role;
import ua.volkov.electronic_library.model.User;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/registration")
    public String registration(){
        return "registration.html";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model){
        User userFromDataBase = userRepository.findByLogin(user.getLogin());

        if(userFromDataBase!=null){
            model.put("message", "User already exists!");
            return "registration.html";
        }
        user.setUserRoles(Collections.singleton(Role.USER));
        user.setActive(true);
        userRepository.save(user);
        return "redirect:/login";

    }
}
