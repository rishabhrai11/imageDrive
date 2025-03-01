package com.risha.photoDrive.repository;

import com.risha.photoDrive.entity.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends MongoRepository<Photo, Object> {

}
