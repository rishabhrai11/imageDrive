package com.risha.photoDrive.repository;

import com.risha.photoDrive.entity.Folder;
import com.risha.photoDrive.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends MongoRepository<Folder, ObjectId> {
    List<Folder>  findByOwnerAndParent(User owner, Folder parent);

    boolean existsByNameAndOwnerAndParent(String name, User owner, Folder parent);
}

