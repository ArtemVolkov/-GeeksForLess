package ua.volkov.electronic_library.controller;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ua.volkov.electronic_library.dao.BookRepository;
import ua.volkov.electronic_library.dao.TagRepository;
import ua.volkov.electronic_library.model.Book;
import ua.volkov.electronic_library.model.Tag;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin/addbook")
public class AdmAddBookController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private TagRepository tagRepository;

    private static final String[] possibleFileExtensions={"txt", "rtf", "doc", "docx", "odt", "pdf"};
    private static final String[] possiblePreviewExtensions={"jpeg", "jpg", "png", "gif", "tif", "bmp"};

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping
    public String addBookForm(Map<String,Object> model){
        model.put("tags", tagRepository.findAll());
        return "AddBookForm.html";
    }

    @PostMapping
    public String addNewBook(@RequestParam String name,
                             @RequestParam String author,
                             @RequestParam String publisher,
                             @RequestParam String description,
                             @RequestParam(name = "booktags", required = false) String tags,
                             @RequestParam(name="preview") MultipartFile preview,
                             @RequestParam(name="bookfile") MultipartFile file,
                             Map<String,Object> model) throws IOException {
        //check valid
        if(!isAllFieldsFilled(name,author,publisher,description)) {
            model.put("errFields", "Все поля должны быть заполнены!");
            model.put("tags", tagRepository.findAll());
            return "AddBookForm.html";
        }
        if(!checkExtesion(FilenameUtils.getExtension(file.getOriginalFilename()), "file") ||
                !checkExtesion(FilenameUtils.getExtension(preview.getOriginalFilename()) , "preview") ){

            if(!checkExtesion(FilenameUtils.getExtension(file.getOriginalFilename()), "file"))
                model.put("errorfile", "Файл должен быть текстовым!");

            if(!checkExtesion(FilenameUtils.getExtension(preview.getOriginalFilename()), "preview"))
                model.put("errorpreview", "Превью должно быть изображением");


            model.put("tags", tagRepository.findAll());
            return "AddBookForm.html";
        }

        if(!checkPreviewResolution(preview)){
            model.put("errorpreview", "Разрешение превью слишком большое");
            model.put("tags", tagRepository.findAll());
            return "AddBookForm.html";

        }  //end valid check

        Book newBook = new Book(name , author , publisher , description , new Date((Calendar.getInstance().getTime()).getTime()));
        if(tags!=null && !tags.equals(""))
            newBook.setTags(getTagsByName(tags.split(",")));

        String newFileName = saveFileTo(file, "/books");
        if(!newFileName.equals("not a file"))
            newBook.setFile(newFileName);

        String newPreviewName= saveFileTo(preview , "/previews");
        if(!newPreviewName.equals("not a file"))
            newBook.setPreview(newPreviewName);

        bookRepository.save(newBook);
        return "redirect:/books";
    }

    private String saveFileTo(MultipartFile file, String directory) throws IOException {
        if(file!=null && !file.getOriginalFilename().isEmpty()){
            File uploadDir = new File(uploadPath+ directory);
            if(!uploadDir.exists()) uploadDir.mkdir();

            String uuidFile = UUID.randomUUID().toString();
            String resFileName = uuidFile+ '.'+ file.getOriginalFilename();

            file.transferTo(new File(uploadDir + "/" + resFileName));
            return resFileName;
        }
        return "not a file";
    }

    private Set<Tag> getTagsByName(String[] tags){
        Set<Tag> tagSet = new HashSet<Tag>();

        for(Tag t: tagRepository.findAll()){
            for(String s: tags)
                if(t.getName().equals(s))
                    tagSet.add(t);
        }

        return tagSet;
    }

    private boolean checkExtesion(String s, String type){
        String[]ext = (type.equals("file"))? possibleFileExtensions: possiblePreviewExtensions;
        boolean isTypeValid = false;
        for(String e: ext)
            if(s.equals(e)){
                isTypeValid=true;
                break;
            }

        return isTypeValid;
    }

    private boolean checkPreviewResolution(MultipartFile preview) throws IOException {
        BufferedImage image = ImageIO.read(preview.getInputStream());
        if(image.getWidth()>800) return false;
        if(image.getHeight()>600) return false;
        return true;
    }

    private boolean isAllFieldsFilled( String ... fields){
        boolean flag = true;
        for(String s: fields){
            if(s.trim().equals("")){
                flag = false;
                break;
            }
        }
        return flag;
    }

}
