package com.example.analytics_back.controller.onlineAnalytics;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.onlineAnalytics.OnlineABCService;
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
@RequestMapping("/analytics/online/abc")
public class OnlineABCController {
    @Autowired
    private OnlineABCService onlineABCService;

    @GetMapping("{userId}")
    public ResponseEntity<?> abc(@PathVariable Long userId) {
        try {
            Map<String, Object> result = onlineABCService.abc(userId);
            return ResponseEntity.ok(result);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("{userId}/filter/{withDate}/{byDate}")
    public ResponseEntity<?> abcFiltered(@PathVariable Long userId,
                                             @PathVariable String withDate, @PathVariable String byDate) {
        try {
            Map<String, Object> result = onlineABCService.abcFiltered(userId, withDate, byDate);
            return ResponseEntity.ok(result);
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
