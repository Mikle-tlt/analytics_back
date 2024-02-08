package com.example.analytics_back.controller.onlineAnalytics;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.onlineAnalytics.OnlineProfitabilityService;
import com.itextpdf.text.DocumentException;
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
@RequestMapping("/analytics/online/profitability")
public class OnlineProfitabilityController {

    @Autowired
    private OnlineProfitabilityService onlineProfitabilityService;

    @GetMapping("{userId}/filter/{dateWithF}/{dateByF}/{dateWithS}/{dateByS}")
    public ResponseEntity<?> xyzFiltered(@PathVariable Long userId, @PathVariable String dateWithF,
                                         @PathVariable String dateByF, @PathVariable String dateWithS,
                                         @PathVariable String dateByS) {
        try {
            Map<String, Object> result = onlineProfitabilityService.profitabilityData(userId,
                    dateWithF, dateByF, dateWithS, dateByS);
            return ResponseEntity.ok(result);
        } catch (CustomException | ParseException | DocumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
