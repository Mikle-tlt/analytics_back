package com.example.analytics_back.controller.offlineAnalytics;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.offlineAnalytics.OfflineGrowthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@Controller
@RequestMapping("/analytics/offline/growth")
public class OfflineGrowthController {
    @Autowired
    private OfflineGrowthService offlineGrowthService;
    @GetMapping("/filter/{productId}/{withDate}/{byDate}")
    public ResponseEntity<?> growth(@PathVariable Long productId,
                                    @PathVariable String withDate,
                                    @PathVariable String byDate) {
        try {
            Map<String, Object> result = offlineGrowthService.growth(productId, withDate, byDate);
            return ResponseEntity.ok(result);
        } catch (CustomException | ParseException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
