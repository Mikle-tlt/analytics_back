package com.example.analytics_back.controller.offlineAnalytics;

import com.example.analytics_back.DTO.analytics.GeneralDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.offlineAnalytics.OfflineGeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("/analytics/offline/general")
public class OfflineGeneralController {
    @Autowired
    private OfflineGeneralService offlineGeneralService;

    @GetMapping
    public ResponseEntity<?> general() {
        try {
            List<GeneralDTO> onlineGeneralDTOList = offlineGeneralService.general();
            return ResponseEntity.ok(onlineGeneralDTOList);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/filter/{withDate}/{byDate}")
    public ResponseEntity<?> generalFiltered(@PathVariable String withDate,
                                             @PathVariable String byDate) {
        try {
            List<GeneralDTO> onlineGeneralDTOList = offlineGeneralService.generalFiltered(withDate, byDate);
            return ResponseEntity.ok(onlineGeneralDTOList);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
