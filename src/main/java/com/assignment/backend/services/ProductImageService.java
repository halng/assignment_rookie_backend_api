package com.assignment.backend.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ProductImageService {
    public ResponseEntity<HttpStatus> deleteImage(int id);
}
