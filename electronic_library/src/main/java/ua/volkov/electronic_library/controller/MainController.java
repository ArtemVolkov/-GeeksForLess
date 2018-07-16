package ua.volkov.electronic_library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller

public class MainController {

    @GetMapping("/")
    public String getMain(){
        return "Main.html";
    }


}
