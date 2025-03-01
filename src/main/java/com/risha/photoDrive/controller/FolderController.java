package com.risha.photoDrive.controller;

import com.risha.photoDrive.entity.Folder;
import com.risha.photoDrive.entity.User;
import com.risha.photoDrive.service.FolderService;
import com.risha.photoDrive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createFolder(@RequestParam String folderName, @RequestParam(required = false) String parentId){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(authentication.getName());
            Folder folder = folderService.createFolder(user,folderName, parentId);
            if(folder == null){
                throw new RuntimeException("Folder could not be created");
            }

            return new ResponseEntity<>("Folder created "+folder.getId(), HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("list")
    public ResponseEntity<?> listFolders(@RequestParam(required = false) String parentId){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(authentication.getName());
            List<Folder> folders = folderService.listFolders(user, parentId);
            if(folders == null){
                throw new RuntimeException("Folders could not be found");
            }
            return new ResponseEntity<>(folders, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
