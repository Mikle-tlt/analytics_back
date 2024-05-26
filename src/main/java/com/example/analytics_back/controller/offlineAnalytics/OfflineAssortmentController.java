package com.example.analytics_back.controller.offlineAnalytics;

import com.example.analytics_back.service.offlineAnalytics.OfflineAssortmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/analytics/offline/assortment")
public class OfflineAssortmentController {
    @Autowired
    private OfflineAssortmentService offlineAssortmentService;
    @GetMapping
    public ResponseEntity<?> getSalesByRegions() {
        try {
            Map<String, Object> result = offlineAssortmentService.getAssortment();
            return ResponseEntity.ok(result);
        }  catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}