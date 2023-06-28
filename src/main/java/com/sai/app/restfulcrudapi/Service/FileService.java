package com.sai.app.restfulcrudapi.Service;

import java.io.IOException;


import com.sai.app.restfulcrudapi.Exception.FileNotFoundException;
import com.sai.app.restfulcrudapi.Exception.FileStorageException;
import org.springframework.util.StringUtils;
import com.sai.app.restfulcrudapi.Models.File;
import com.sai.app.restfulcrudapi.Repo.FileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import static com.mysql.cj.util.StringUtils.*;
@Service
public class FileService {
    @Autowired

    private FileRepo dbFileRepository;

    public File storeFile(MultipartFile file) {
        System.out.println(file.toString());
        // normalize the file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            //check if the file's name contains any invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path squence " + fileName);
            }
            File dbFile = new File(fileName, file.getContentType(), file.getBytes());

            return dbFileRepository.save(dbFile);
        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + fileName + ".Please try again.", e);

        }
    }

    public File getFile(String fileId) {
        return dbFileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with id " + fileId));
    }
}