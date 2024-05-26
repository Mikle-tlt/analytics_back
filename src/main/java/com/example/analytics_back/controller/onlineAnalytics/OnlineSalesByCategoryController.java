package com.example.analytics_back.controller.onlineAnalytics;

import com.example.analytics_back.service.onlineAnalytics.OnlineSalesByCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/analytics/online/category")
public class OnlineSalesByCategoryController {
    @Autowired
    private OnlineSalesByCategoryService onlineSalesByCategoryService;
    @GetMapping("/{categoryId}/{year}")
    public ResponseEntity<?> getSalesByCategory(@PathVariable Long categoryId, @PathVariable int year) {
        Map<String, Object> result = onlineSalesByCategoryService.getSalesByCategory(categoryId, year);
        return ResponseEntity.ok(result);
    }
}
