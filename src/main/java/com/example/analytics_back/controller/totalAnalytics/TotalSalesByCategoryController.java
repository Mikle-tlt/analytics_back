package com.example.analytics_back.controller.totalAnalytics;

import com.example.analytics_back.service.onlineAnalytics.OnlineSalesByCategoryService;
import com.example.analytics_back.service.totalAnalytics.TotalSalesByCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/analytics/total/category")
public class TotalSalesByCategoryController {
    @Autowired
    private TotalSalesByCategoryService totalSalesByCategoryService;
    @GetMapping("/{categoryId}/{year}")
    public ResponseEntity<?> getSalesByCategory(@PathVariable Long categoryId, @PathVariable int year) {
        Map<String, Object> result = totalSalesByCategoryService.getSalesByCategory(categoryId, year);
        return ResponseEntity.ok(result);
    }
}
