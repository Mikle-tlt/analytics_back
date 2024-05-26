package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.BuysDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.service.BuysService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/online/buys")
@PreAuthorize("hasRole('MANAGER')")
public class BuysController {

    @Autowired
    private BuysService buysService;

    @GetMapping("/{buyId}")
    public ResponseEntity<?> getBuy(@PathVariable Long buyId) {
        try {
            BuysDTO buy = buysService.getBuyDTO(buyId);
            return ResponseEntity.ok(buy);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getOnlineBuys(@PathVariable Long clientId) {
        try {
            List<BuysDTO> buys = buysService.getOnlineBuys(clientId);
            return ResponseEntity.ok(buys);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping("/{clientId}")
    public ResponseEntity<?> onlineBuyAdd(@PathVariable Long clientId, @RequestBody BuysDTO buysDTO) {
        try {
            BuysDTO buysDTO1 = buysService.onlineBuyAdd(clientId, buysDTO);
            return ResponseEntity.ok(buysDTO1);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PutMapping
    public ResponseEntity<?> onlineBuyEdit(@RequestBody BuysDTO buysDTO) {
        try {
            BuysDTO updatedBuy = buysService.onlineBuyEdit(buysDTO);
            return ResponseEntity.ok(updatedBuy);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/{buyId}")
    public ResponseEntity<?> onlineBuyDelete(@PathVariable Long buyId) {
        try {
            buysService.onlineBuyDelete(buyId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
