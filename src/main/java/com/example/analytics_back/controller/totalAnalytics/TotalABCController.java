package com.example.analytics_back.controller.totalAnalytics;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.totalAnalytics.TotalABCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.util.Map;

@Controller
@RequestMapping("/analytics/total/abc")
public class TotalABCController {
    @Autowired
    private TotalABCService totalABCService;

    @GetMapping
    public ResponseEntity<?> abc() {
        try {
            Map<String, Object> result = totalABCService.abc();
            return ResponseEntity.ok(result);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/filter/{withDate}/{byDate}")
    public ResponseEntity<?> abcFiltered(@PathVariable String withDate,
                                         @PathVariable String byDate) {
        try {
            Map<String, Object> result = totalABCService.abcFiltered(withDate, byDate);
            return ResponseEntity.ok(result);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}