package com.risha.photoDrive.service;

import com.risha.photoDrive.entity.Folder;
import com.risha.photoDrive.entity.User;
import com.risha.photoDrive.repository.FolderRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    public Folder createFolder(User user, String folderName, String parentId) {
        Folder parent = null;
        try {
            if (parentId != null && !parentId.isEmpty()) {
                parent = folderRepository.findById(new ObjectId(parentId)).orElse(null);
                if (parent == null) {
                    throw new RuntimeException("Parent not found");
                }
            }

            if(folderRepository.existsByNameAndOwnerAndParent(folderName, user, parent)){
                throw new RuntimeException("Folder already exists");
            }
            Folder folder = Folder.builder().name(folderName)
                    .owner(user)
                    .parent(parent)
                    .build();
            return folderRepository.save(folder);
        }
        catch(Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    public List<Folder> listFolders(User user, String parentId) {
        try {
            Folder parent = null;
            if (parentId != null && !parentId.isEmpty()) {
                parent = folderRepository.findById(new ObjectId(parentId)).orElse(null);
                if (parent == null) {
                    throw new RuntimeException("Parent not found");
                }
            }
            return folderRepository.findByOwnerAndParent(user, parent);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
}
