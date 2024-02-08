package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.OfflineBuysDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.OfflineBuys;
import com.example.analytics_back.service.OfflineBuysService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/offline/points/buys")
public class OfflineBuysController {
    @Autowired
    private OfflineBuysService offlineBuysService;

    @GetMapping("/{offlinePointId}")
    public ResponseEntity<?> offlineBuys(@PathVariable Long offlinePointId) {
        try {
            List<OfflineBuys> offlineBuys = offlineBuysService.offlineBuys(offlinePointId);
            return ResponseEntity.ok(offlineBuys);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/single/{offlineBuyId}")
    public ResponseEntity<?> getOfflineBuy(@PathVariable Long offlineBuyId) {
        try {
            OfflineBuysDTO offlineBuysDTO = offlineBuysService.getOfflineBuy(offlineBuyId);
            return ResponseEntity.ok(offlineBuysDTO);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{offlinePointId}")
    public ResponseEntity<?> offlineBuysAdd(@RequestBody OfflineBuys offlineBuys, @PathVariable Long offlinePointId) {
        try {
            OfflineBuys offlineBuy = offlineBuysService.offlineBuysAdd(offlineBuys.getDate(), offlinePointId);
            return ResponseEntity.ok(offlineBuy);
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{offlinePointId}")
    public ResponseEntity<?> offlineBuysEdit(@RequestBody OfflineBuys offlineBuys, @PathVariable Long offlinePointId) {
        try {
            OfflineBuys offlineBuys1 = offlineBuysService.offlineBuysEdit(offlineBuys.getDate(), offlinePointId);
            return ResponseEntity.ok(offlineBuys1);
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }    }

    @DeleteMapping("/{offlineBuyId}")
    public ResponseEntity<?> offlineBuysDelete(@PathVariable Long offlineBuyId) {
        try {
            offlineBuysService.offlineBuysDelete(offlineBuyId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
