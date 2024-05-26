package com.example.analytics_back.controller.totalAnalytics;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.onlineAnalytics.OnlineProfitabilityService;
import com.example.analytics_back.service.totalAnalytics.TotalProfitabilityService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@Controller
@RequestMapping("/analytics/total/profitability")
public class TotalProfitabilityController {

    @Autowired
    private TotalProfitabilityService totalProfitabilityService;

    @GetMapping("/filter/{dateWithF}/{dateByF}/{dateWithS}/{dateByS}")
    public ResponseEntity<?> profitability(@PathVariable String dateWithF,
                                           @PathVariable String dateByF,
                                           @PathVariable String dateWithS,
                                           @PathVariable String dateByS) {
        try {
            Map<String, Object> result = totalProfitabilityService.profitabilityData(dateWithF, dateByF,
                    dateWithS, dateByS);
            return ResponseEntity.ok(result);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException | ParseException | DocumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
