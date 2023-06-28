package com.sai.app.restfulcrudapi.Controller;

import com.sai.app.restfulcrudapi.Models.File;
import com.sai.app.restfulcrudapi.Models.User;
import com.sai.app.restfulcrudapi.Repo.FileRepo;
import com.sai.app.restfulcrudapi.Repo.UserRepo;
import com.sai.app.restfulcrudapi.Service.FileService;
import com.sai.app.restfulcrudapi.Payload.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ApiController {
    @Autowired
    private UserRepo userRepo;
    private FileRepo fileRepo;

    private final FileService fileStorageService;

    public ApiController(UserRepo userRepo, FileRepo fileRepo, FileService fileStorageService) {
        this.userRepo = userRepo;
        this.fileRepo = fileRepo;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/")
    public String getPage() {
        return "Welcome";
    }

    @GetMapping("/status")
    @ResponseStatus(code = HttpStatus.OK, reason="OK")
    public String getStatus()
    {
        return HttpStatus.OK.toString();
    }

    @PostMapping("/upload")
    public Response uploadFile(@RequestParam("file")MultipartFile file)
    {
        File fileName = fileStorageService.storeFile(file);

        System.out.println(fileName);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(fileName.getFileName())
                .toUriString();

        System.out.println((fileDownloadUri));

        return new Response(fileName.getFileName(), fileDownloadUri, file.getContentType(), file.getSize());
    }

    @PostMapping("/upload++")
    public List < Response > uploadMultipleFiles(@RequestParam("files") MultipartFile[] files)
    {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName, HttpServletRequest request)
    {
        File databaseFile = fileStorageService.getFile(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(databaseFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + databaseFile.getFileName() + "\"")
                .body(new ByteArrayResource(databaseFile.getData()));
    }
    @GetMapping("/users")
    public List<User> getUsers(){
        return userRepo.findAll();
    }

    @PostMapping(value = "/submitFile/{id}")
    public String saveFile(@PathVariable long id, @RequestParam("file") MultipartFile file)
    {
        File fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(fileName.getFileName())
                .toUriString();

        User userFile = userRepo.findById(id).get();
        userFile.setFileName(fileName.getFileName());
        userFile.setFileType(fileName.getFileType());
        userFile.setData(fileName.getData());
        fileRepo.save(fileName);
        return "file "+ fileName.getFileName() +" saved";
    }
    @PostMapping(value = "/submit")
    public String saveUser(@RequestBody User user){
        userRepo.save(user);
        return "user saved...";

    }


    @PutMapping(value = "/update/{id}")
    public String updateUser(@PathVariable long id, @RequestBody User user){
        User updatedUser = userRepo.findById(id).get();
        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setLastName(user.getLastName());
        updatedUser.setOccupation(user.getOccupation());
        updatedUser.setAge(user.getAge());
        userRepo.save(updatedUser);

        return "user information updated...";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable long id)
    {
        User deleteUser = userRepo.findById(id).get();
        userRepo.delete(deleteUser);

        return "deleted user with id " + id;

    }



}
