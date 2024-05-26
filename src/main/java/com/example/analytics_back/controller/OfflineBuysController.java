package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.OfflineBuysDTO;
import com.example.analytics_back.DTO.OfflineDetailsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.OfflineBuys;
import com.example.analytics_back.service.OfflineBuysService;
import com.example.analytics_back.service.files.OfflineFileImport;
import com.example.analytics_back.service.files.OnlineFileImport;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/offline/points/buys")
@PreAuthorize("hasRole('MANAGER')")
public class OfflineBuysController {
    @Autowired
    private OfflineBuysService offlineBuysService;
    @Autowired
    private OfflineFileImport offlineFileImport;

    @GetMapping("/{offlineBuyId}")
    public ResponseEntity<?> getOfflineBuy(@PathVariable Long offlineBuyId) {
        try {
            OfflineBuysDTO offlineBuysDTO = offlineBuysService.getOfflineBuyDTO(offlineBuyId);
            return ResponseEntity.ok(offlineBuysDTO);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/point/{offlinePointId}")
    public ResponseEntity<?> offlineBuys(@PathVariable Long offlinePointId) {
        try {
            List<OfflineBuys> offlineBuys = offlineBuysService.getOfflineBuys(offlinePointId);
            return ResponseEntity.ok(offlineBuys);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/point/{offlinePointId}")
    public ResponseEntity<?> offlineBuysAdd(@RequestBody OfflineBuys offlineBuys, @PathVariable Long offlinePointId) {
        try {
            OfflineBuys offlineBuy = offlineBuysService.offlineBuyAdd(offlineBuys.getDate(), offlinePointId);
            return ResponseEntity.ok(offlineBuy);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> offlineBuysEdit(@RequestBody OfflineBuys offlineBuys) {
        try {
            OfflineBuys updatedOfflineBuy = offlineBuysService.offlineBuysEdit(offlineBuys);
            return ResponseEntity.ok(updatedOfflineBuy);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{offlineBuyId}")
    public ResponseEntity<?> offlineBuysDelete(@PathVariable Long offlineBuyId) {
        try {
            offlineBuysService.offlineBuysDelete(offlineBuyId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/upload-excel")
    public ResponseEntity<?> handleExcelUpload(@RequestParam("excelFile") MultipartFile file) {
        try {
            offlineFileImport.handleImportExcelFile(file);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException | IOException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}