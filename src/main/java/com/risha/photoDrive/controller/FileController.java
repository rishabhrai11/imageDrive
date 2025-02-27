package com.risha.photoDrive.controller;

import com.risha.photoDrive.entity.Photo;
import com.risha.photoDrive.entity.User;
import com.risha.photoDrive.repository.UserRepository;
import com.risha.photoDrive.service.FileService;
import com.risha.photoDrive.service.PhotoService;
import com.risha.photoDrive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if(user == null){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            String filename = fileService.uploadFile(file);
            Photo photo = photoService.savePhoto(filename, file.getSize());
            userService.addPhoto(user,photo);
            return new ResponseEntity<>("File uploaded Successfully: " + filename, HttpStatus.OK);
        }
        catch(IOException e){
            return new ResponseEntity<>("File upload failed: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listUserFiles(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = userRepository.findByUsername(username);
            if(user == null){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            List<String> preSignedUrls = user.getPhotos().stream()
                    .map(photo -> fileService.generatePreSignedUrl(photo.getFilename()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(preSignedUrls, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>("File list retrieval failed: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
