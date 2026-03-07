package com.jeet.photo_store.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class FileHashUtils {

    public String sha256(MultipartFile file){
        try{
            byte[] bytes = file.getBytes();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(bytes);

            StringBuilder hexString = new StringBuilder();
            for(byte b : hashBytes){
                hexString.append(String.format("%02X", b));
            }
            return hexString.toString();
        }
        catch (IOException e){
            throw new RuntimeException("Failed to read file for hashing", e);
        }
        catch (NoSuchAlgorithmException ex){
            throw new RuntimeException("SHA-256 algorithm not available", ex);
        }
    }
}
