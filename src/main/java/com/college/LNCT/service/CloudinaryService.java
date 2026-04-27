package com.college.LNCT.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    // ✅ Upload image
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap("folder", "finearts-project")
        );
        String url = (String) uploadResult.get("secure_url");
        log.info("Image uploaded to Cloudinary: {}", url);
        return url;
    }

    // ✅ Delete image by Cloudinary URL
    public void deleteImage(String imageUrl) {
        try {
            // Extract public_id from URL
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Image deleted from Cloudinary: {}", publicId);
        } catch (Exception e) {
            log.error("Error deleting image from Cloudinary: {}", imageUrl, e);
        }
    }

    // ✅ Extract public_id from Cloudinary URL
    private String extractPublicId(String imageUrl) {
        // URL format: https://res.cloudinary.com/cloud/image/upload/v123/folder/filename.jpg
        String[] parts = imageUrl.split("/upload/");
        String afterUpload = parts[1]; // v123/folder/filename.jpg
        String withoutVersion = afterUpload.replaceFirst("v[0-9]+/", ""); // folder/filename.jpg
        return withoutVersion.substring(0, withoutVersion.lastIndexOf(".")); // folder/filename
    }
}