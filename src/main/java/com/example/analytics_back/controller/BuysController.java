package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.BuysDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Buys;
import com.example.analytics_back.service.BuysService;
import com.example.analytics_back.service.DTOConvectors.BuysDTOConverter;
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
@RequestMapping("/online/buys")
public class BuysController {

    @Autowired
    private BuysService buysService;
    @Autowired
    private BuysDTOConverter buysDTOConverter;

    @GetMapping("/{clientId}")
    public ResponseEntity<?> onlineBuys(@PathVariable Long clientId) {
        try {
            List<Buys> buys = buysService.onlineBuys(clientId);
            List<BuysDTO> buysDTOList = buys.stream()
                    .map(buysDTOConverter::convertToDTO)
                    .toList();
            return ResponseEntity.ok(buysDTOList);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/single/{buyId}")
    public ResponseEntity<?> getBuy(@PathVariable Long buyId) {
        try {
            BuysDTO buysDTO = buysService.getBuy(buyId);
            return ResponseEntity.ok(buysDTO);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{clientId}")
    public ResponseEntity<?> onlineBuyAdd(@PathVariable Long clientId, @RequestBody BuysDTO buysDTO) {
        try {
            BuysDTO buysDTO1 = buysService.onlineBuyAdd(clientId, buysDTO);
            return ResponseEntity.ok(buysDTO1);
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> onlineBuyEdit(@RequestBody BuysDTO buysDTO) {
        try {
            BuysDTO buysDTO1 = buysService.onlineBuyEdit(buysDTO);
            return ResponseEntity.ok(buysDTO1);
        } catch (CustomException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{buyId}")
    public ResponseEntity<?> onlineBuyDelete(@PathVariable Long buyId) {
        try {
            buysService.onlineBuyDelete(buyId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
