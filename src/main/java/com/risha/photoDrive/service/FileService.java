package com.risha.photoDrive.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.risha.photoDrive.entity.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    AmazonS3 s3Client;

    @Value("${app.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + " " + file.getOriginalFilename ();
        ObjectMetadata metadata = new ObjectMetadata();

        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
        return fileName;
    }

    public String generatePreSignedUrl(String fileName){
        Date expiration = new Date();
        long expiryTime = expiration.getTime();
        expiryTime += 1000 * 60 * 60;
        expiration.setTime(expiryTime);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName).withMethod(HttpMethod.GET).withExpiration(expiration);
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    public void deleteFile(String fileName){
        s3Client.deleteObject(bucketName, fileName);
    }



}
