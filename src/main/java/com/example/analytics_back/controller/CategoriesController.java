package com.example.analytics_back.controller;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.Categories;
import com.example.analytics_back.service.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@PreAuthorize("hasRole('MANAGER')")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    @GetMapping
    public ResponseEntity<?> getCategories() {
        try {
            List<Categories> categoriesList = categoriesService.getCategories();
            return ResponseEntity.ok(categoriesList);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> categoryAdd(@RequestBody Categories category) {
        try {
            Categories addedCategory = categoriesService.categoryAdd(category.getName());
            return ResponseEntity.ok(addedCategory);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> categoryEdit(@RequestBody Categories category) {
        try {
            Categories updatedCategory = categoriesService.categoryEdit(category);
            return ResponseEntity.ok(updatedCategory);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> categoryDelete(@PathVariable Long categoryId) {
        try {
            categoriesService.categoryDelete(categoryId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
