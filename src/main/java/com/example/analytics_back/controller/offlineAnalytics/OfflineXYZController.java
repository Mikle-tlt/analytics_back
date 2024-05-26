package com.example.analytics_back.controller.offlineAnalytics;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.offlineAnalytics.OfflineXYZService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.util.Map;

@Controller
@RequestMapping("/analytics/offline/xyz")
public class OfflineXYZController {

    @Autowired
    private OfflineXYZService offlineXYZService;

    @GetMapping("/filter/{withDate}/{byDate}")
    public ResponseEntity<?> xyzFiltered(@PathVariable String withDate,
                                         @PathVariable String byDate) {
        try {
            Map<String, Object> result = offlineXYZService.xyzFiltered(withDate, byDate);
            return ResponseEntity.ok(result);
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
