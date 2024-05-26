package com.example.analytics_back.controller.offlineAnalytics;

import com.example.analytics_back.service.offlineAnalytics.OfflineSalesByCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/analytics/offline/category")
public class OfflineSalesByCategoryController {
    @Autowired
    private OfflineSalesByCategoryService offlineSalesByCategoryService;
    @GetMapping("/{categoryId}/{year}")
    public ResponseEntity<?> getSalesByCategory(@PathVariable Long categoryId, @PathVariable int year) {
        Map<String, Object> result = offlineSalesByCategoryService.getSalesByCategory(categoryId, year);
        return ResponseEntity.ok(result);
    }
}