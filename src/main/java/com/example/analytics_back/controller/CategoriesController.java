package com.example.analytics_back.controller;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Categories;
import com.example.analytics_back.service.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> categories(@PathVariable Long userId) {
        try {
            List<Categories> categoriesList = categoriesService.categories(userId);
            return ResponseEntity.ok(categoriesList);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> categoryAdd(@RequestBody Categories category, @PathVariable Long userId) {
        try {
            Categories category1 = categoriesService.categoryAdd(category.getName(), userId);
            return ResponseEntity.ok(category1);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> categoryEdit(@RequestBody Categories category, @PathVariable Long categoryId) {
        try {
            Categories category1 = categoriesService.categoryEdit(category.getName(), categoryId);
            return ResponseEntity.ok(category1);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> categoryDelete(@PathVariable Long categoryId) {
        try {
            categoriesService.categoryDelete(categoryId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
