package com.risha.photoDrive.controller;

import com.risha.photoDrive.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users/files")
public class S3Controller {

    @Autowired
    S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try{
            String fileUrl = s3Service.uploadFile(file);
            return new ResponseEntity<>("File uploaded Successfully: " + fileUrl, HttpStatus.OK);
        }
        catch(IOException e){
            return new ResponseEntity<>("File upload failed: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
