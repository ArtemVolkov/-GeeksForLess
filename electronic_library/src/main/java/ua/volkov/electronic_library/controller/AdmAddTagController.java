package ua.volkov.electronic_library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.volkov.electronic_library.dao.TagRepository;
import ua.volkov.electronic_library.model.Tag;

import java.util.Map;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")

public class AdmAddTagController {
    @Autowired
    TagRepository tagRepository;

    @GetMapping("/admin/addtag")
    public String addTagPage(){
        return "AddTag.html";
    }

    @PostMapping("/admin/addtag")
    public String addTag(@RequestParam(name = "tagname") String tagName){
        Tag tag= tagRepository.findByNameContains(tagName);
        if(tag!=null)
            return "redirect:/admin/addtag";

        tag = new Tag();
        tag.setName(tagName);
        tagRepository.save(tag);

        return "redirect:/";
    }

    @GetMapping("/admin/tags")
    public String admTagsList(Map<String, Object> model){
        model.put("tags", tagRepository.findAll());
        return "TagsList.html";
    }

}
