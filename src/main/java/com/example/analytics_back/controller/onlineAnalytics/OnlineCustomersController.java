package com.example.analytics_back.controller.onlineAnalytics;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.onlineAnalytics.OnlineCustomersService;
import com.example.analytics_back.service.onlineAnalytics.OnlineRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/analytics/online/customers")
public class OnlineCustomersController {
    @Autowired
    private OnlineCustomersService onlineCustomersService;

    @GetMapping
    public ResponseEntity<?> getCustomersAnalytics(@RequestParam String year) {
        try {
            Map<String, Object> result = onlineCustomersService.getCustomersAnalytics(Integer.parseInt(year));
            return ResponseEntity.ok(result);
        }  catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
