package com.risha.photoDrive.service;

import com.risha.photoDrive.entity.Photo;
import com.risha.photoDrive.entity.User;
import com.risha.photoDrive.repository.PhotoRepository;
import com.risha.photoDrive.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private FileService fileService;
    @Autowired
    private PhotoRepository photoRepository;

    public void saveUser(User user) {
        try{
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        }
        catch(Exception e){
            log.error(e.getMessage());
        }
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(User user) {
        List<Photo> photos = user.getPhotos();
        for(Photo photo : photos){
            fileService.deleteFile(photo.getFilename());
            photoRepository.delete(photo);
        }
        userRepository.delete(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void addPhoto(User user,Photo photo) {
        user.getPhotos().add(photo);
        userRepository.save(user);
    }

}
