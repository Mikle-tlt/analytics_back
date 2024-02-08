package com.example.analytics_back.controller.onlineAnalytics;

import com.example.analytics_back.DTO.onlineAnalytics.OnlineGeneralDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.onlineAnalytics.OnlineGeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("/analytics/online/general")
public class OnlineGeneralController {
    @Autowired
    private OnlineGeneralService onlineGeneralService;

    @GetMapping("{userId}")
    public ResponseEntity<?> general(@PathVariable Long userId) {
        try {
            List<OnlineGeneralDTO> onlineGeneralDTOList = onlineGeneralService.general(userId);
            return ResponseEntity.ok(onlineGeneralDTOList);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("{userId}/filter/{withDate}/{byDate}")
    public ResponseEntity<?> generalFiltered(@PathVariable Long userId,
                                             @PathVariable String withDate, @PathVariable String byDate) {
        try {
            List<OnlineGeneralDTO> onlineGeneralDTOList = onlineGeneralService.generalFiltered(userId, withDate, byDate);
            return ResponseEntity.ok(onlineGeneralDTOList);
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
