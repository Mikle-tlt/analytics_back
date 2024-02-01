package com.example.analytics_back.controller;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Clients;
import com.example.analytics_back.service.ClientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientsController {

    @Autowired
    private ClientsService clientsService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> clients(@PathVariable Long userId) {
        try {
            List<Clients> clientsList = clientsService.clients(userId);
            return ResponseEntity.ok(clientsList);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/selected/{clientId}")
    public  ResponseEntity<?> getClient(@PathVariable Long clientId) {
        try {
            Clients client = clientsService.getClient(clientId);
            return ResponseEntity.ok(client);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> clientAdd(@RequestBody Clients clients, @PathVariable Long userId) {
        try {
            Clients client  = clientsService.clientAdd(clients.getName(), clients.getContact(), userId);
            return ResponseEntity.ok(client);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> clientEdit(@RequestBody Clients clients) {
        try {
            Clients client  = clientsService.clientEdit(clients.getName(), clients.getContact(), clients.getId());
            return ResponseEntity.ok(client);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/{clientId}")
    public ResponseEntity<?> clientDelete(@PathVariable Long clientId) {
        try {
            clientsService.clientDelete(clientId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
