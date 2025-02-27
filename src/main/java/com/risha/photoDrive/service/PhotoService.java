package com.risha.photoDrive.service;

import com.risha.photoDrive.entity.Photo;
import com.risha.photoDrive.entity.User;
import com.risha.photoDrive.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserService userService;

    public Photo savePhoto(String filename,long size){
        Photo photo = new Photo();
        photo.setFilename(filename);
        photo.setSize(size);
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

}
