package com.risha.photoDrive.repository;

import com.risha.photoDrive.entity.Folder;
import com.risha.photoDrive.entity.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends MongoRepository<Photo, Object> {
    public List<Photo> findByFolder(Folder folder);

    public Photo findByFolderAndFilename(Folder folder, String filename);
}
