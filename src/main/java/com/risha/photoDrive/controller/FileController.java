package com.risha.photoDrive.controller;

import com.risha.photoDrive.entity.Folder;
import com.risha.photoDrive.entity.Photo;
import com.risha.photoDrive.entity.User;
import com.risha.photoDrive.repository.FolderRepository;
import com.risha.photoDrive.repository.PhotoRepository;
import com.risha.photoDrive.repository.UserRepository;
import com.risha.photoDrive.service.FileService;
import com.risha.photoDrive.service.FolderService;
import com.risha.photoDrive.service.PhotoService;
import com.risha.photoDrive.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
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

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(required = false) String folderId) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if(user == null){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            Folder folder = null;
            if(folderId != null && !folderId.isEmpty()){
                folder = folderRepository.findById(new ObjectId(folderId)).orElse(null);
                if(folder == null){
                    throw new FileNotFoundException("Folder not found");
                }
            }
            String filename = fileService.uploadFile(file);
            Photo photo = photoService.savePhoto(filename, file.getSize(), folder);
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

    @DeleteMapping("/delete-photo")
    public ResponseEntity<?> deletePhoto(@RequestParam String filename, @RequestParam(required = false) String folderId){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            Folder folder = null;
            if (folderId != null && !folderId.isEmpty()) {
                folder = folderRepository.findById(new ObjectId(folderId)).orElse(null);
                if (folder == null) {
                    throw new FileNotFoundException("Folder not found");
                }
            }
            Photo photo = photoService.findPhotoByFilename(user, filename);
            photoService.deletePhoto(user, photo);
            fileService.deleteFile(filename);
            return new ResponseEntity<>("File deleted", HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>("File deletion failed: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam String filename){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if(user == null){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            Photo photo = photoService.findPhotoByFilename(user,filename);
            if(photo == null){
                return new ResponseEntity<>("Photo not found", HttpStatus.NOT_FOUND);
            }
            String url = fileService.generatePreSignedUrl(photo.getFilename());
            return new ResponseEntity<>(url, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>("File retrieval failed: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/folder/{folderId}/contents")
    public ResponseEntity<?> getPhotosInFolder(@PathVariable String folderId) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if(user == null){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            List<Photo> photos = photoService.getPhotosInFolder(folderId);
            List<String> urls = photos.stream().map(photo -> fileService.generatePreSignedUrl(photo.getFilename())).collect(Collectors.toList());
            List<Folder> folders = folderService.listFolders(user, folderId);
            urls.addAll(folders.stream().map(Folder::getName).toList());
            return new ResponseEntity<>(urls, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>("File retrieval failed: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/folder/{folderId}/{filename}")
    public ResponseEntity<?> getPhotoInFolder(@PathVariable String folderId, @PathVariable String filename) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if(user == null){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            Photo photo = photoService.findPhotoByFilename(user, filename);
            if(photo == null){
                return new ResponseEntity<>("Photo not found", HttpStatus.NOT_FOUND);
            }
            String url = fileService.generatePreSignedUrl(photo.getFilename());
            return new ResponseEntity<>(url, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>("File retrieval failed: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
