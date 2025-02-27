package com.risha.photoDrive.repository;

import com.risha.photoDrive.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,Object> {
    User findByUsername(String username);
}
