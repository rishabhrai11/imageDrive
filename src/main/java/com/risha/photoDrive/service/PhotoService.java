package com.risha.photoDrive.service;

import com.risha.photoDrive.entity.Photo;
import com.risha.photoDrive.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    public Photo savePhoto(String filename,long size){
        Photo photo = new Photo();
        photo.setFilename(filename);
        photo.setSize(size);
        return photoRepository.save(photo);

    }
}
