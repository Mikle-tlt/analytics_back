package com.example.analytics_back.controller.offlineAnalytics;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.offlineAnalytics.OfflineProfitabilityService;
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
@RequestMapping("/analytics/offline/profitability")
public class OfflineProfitabilityController {

    @Autowired
    private OfflineProfitabilityService offlineProfitabilityService;

    @GetMapping("/filter/{dateWithF}/{dateByF}/{dateWithS}/{dateByS}")
    public ResponseEntity<?> profitability(@PathVariable String dateWithF,
                                           @PathVariable String dateByF,
                                           @PathVariable String dateWithS,
                                           @PathVariable String dateByS) {
        try {
            Map<String, Object> result = offlineProfitabilityService.profitabilityData(dateWithF, dateByF,
                    dateWithS, dateByS);
            return ResponseEntity.ok(result);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException | ParseException | DocumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
