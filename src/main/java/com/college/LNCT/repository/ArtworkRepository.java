package com.college.LNCT.repository;

import com.college.LNCT.entity.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    // ✅ Basic CRUD comes free from JpaRepository:
    // save(), findById(), findAll(), deleteById() etc.

    // ✅ Custom queries if you need them later:
    // List<Artwork> findByTitle(String title);
    // List<Artwork> findByArtistId(Long artistId);
}