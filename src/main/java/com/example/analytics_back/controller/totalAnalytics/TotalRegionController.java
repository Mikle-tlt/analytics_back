package com.example.analytics_back.controller.totalAnalytics;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.onlineAnalytics.OnlineRegionService;
import com.example.analytics_back.service.totalAnalytics.TotalRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/analytics/total/region")
public class TotalRegionController {
    @Autowired
    private TotalRegionService totalRegionService;
    @GetMapping
    public ResponseEntity<?> getSalesByRegions() {
        try {
            Map<String, Object> result = totalRegionService.getSalesByRegions();
            return ResponseEntity.ok(result);
        }  catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }  catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}