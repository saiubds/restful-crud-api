package com.sai.app.restfulcrudapi.Controller;

import com.sai.app.restfulcrudapi.Models.File;
import com.sai.app.restfulcrudapi.Models.Ticket;
import com.sai.app.restfulcrudapi.Repo.FileRepo;
import com.sai.app.restfulcrudapi.Repo.TicketRepo;
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
    private TicketRepo ticketRepo;
    private FileRepo fileRepo;

    private final FileService fileStorageService;

    public ApiController(TicketRepo ticketRepo, FileRepo fileRepo, FileService fileStorageService) {
        this.ticketRepo = ticketRepo;
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
    @GetMapping("/tickets")
    public List<Ticket> getTickets(){
        return ticketRepo.findAll();
    }

    @PostMapping(value = "/submitFile/{id}")
    public String saveFile(@PathVariable long id, @RequestParam("file") MultipartFile file)
    {
        File fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/"+id+"/")
                .path(fileName.getFileName())
                .toUriString();

        Ticket ticketFile = ticketRepo.findById(id).get();
        ticketFile.setFileName(fileDownloadUri+fileName.getFileName());
        ticketFile.setFileType(fileName.getFileType());
        ticketFile.setData(fileName.getData());
        fileRepo.save(fileName);
        return "file "+ fileName.getFileName() +" saved";
    }
    @PostMapping(value = "/submit")
    public String saveTicket(@RequestBody Ticket ticket){
        ticketRepo.save(ticket);

        // insert here for route redirection
        return "Ticket saved...";

    }


    @PutMapping(value = "/update/{id}")
    public String updateTicket(@PathVariable long id, @RequestBody Ticket ticket){
        Ticket updatedTicket = ticketRepo.findById(id).get();
        updatedTicket.setFileDescription(ticket.getFileDescription());
        updatedTicket.setDateOfUpload(ticket.getDateOfUpload());
        updatedTicket.setReportDescription(ticket.getReportDescription());
        updatedTicket.setAge(ticket.getAge());
        ticketRepo.save(updatedTicket);

        return "Ticket information updated...";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteTicket(@PathVariable long id)
    {
        Ticket deleteTicket = ticketRepo.findById(id).get();
        ticketRepo.delete(deleteTicket);

        return "Deleted ticket with id " + id;

    }



}
