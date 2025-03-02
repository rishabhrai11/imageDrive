package com.risha.photoDrive.service;

import com.risha.photoDrive.entity.Folder;
import com.risha.photoDrive.entity.Photo;
import com.risha.photoDrive.entity.User;
import com.risha.photoDrive.repository.FolderRepository;
import com.risha.photoDrive.repository.PhotoRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private FolderRepository folderRepository;

    public Photo savePhoto(String filename, long size, Folder folder){
        Photo photo = new Photo();
        photo.setFilename(filename);
        photo.setSize(size);
        photo.setFolder(folder);
        return photoRepository.save(photo);
    }

    public Photo findPhotoByFilename(User user, String filename){
        return user.getPhotos().stream().filter(p -> p.getFilename().equals(filename)).findFirst().orElse(null);
    }

    public void deletePhoto(User user,Photo photo){
        user.getPhotos().remove(photo);
        userService.saveUser(user);
        photoRepository.delete(photo);
    }

    public List<Photo> getPhotosInFolder(String folderId){
        Folder folder = folderRepository.findById(new ObjectId(folderId)).orElse(null);
        if(folder == null){
            throw new IllegalArgumentException("Folder not found");
        }
        return photoRepository.findByFolder(folder);
    }

    public Photo getPhotoInFolder(String folderId, String filename) throws Exception{
        Folder folder = folderRepository.findById(new ObjectId(folderId)).orElse(null);
        if(folder == null){
            throw new IllegalArgumentException("Folder not found");
        }
        return photoRepository.findByFolderAndFilename(folder, filename);
    }
}
