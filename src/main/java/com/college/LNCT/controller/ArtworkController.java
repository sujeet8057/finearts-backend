package com.college.LNCT.controller;

import com.college.LNCT.entity.Artwork;
import com.college.LNCT.repository.ArtworkRepository;
import com.college.LNCT.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ArtworkRepository artworkRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadArtwork(
        @RequestParam("image") MultipartFile image,
        @RequestParam("title") String title
    ) throws IOException {

        String imageUrl = cloudinaryService.uploadImage(image);

        Artwork artwork = new Artwork();
        artwork.setTitle(title);
        artwork.setImageUrl(imageUrl);
        artworkRepository.save(artwork);

        return ResponseEntity.ok(artwork);
    }
}