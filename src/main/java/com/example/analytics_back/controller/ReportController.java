package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.ReportDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.service.ReportService;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@PreAuthorize("hasRole('MANAGER')")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @PostMapping
    public ResponseEntity<?> generateReport(@RequestBody ReportDTO reportDTO) {
        try {
            reportService.downloadPdf(reportDTO);
            return ResponseEntity.ok("Отчет успешно создан!");
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DocumentException | IOException | CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
