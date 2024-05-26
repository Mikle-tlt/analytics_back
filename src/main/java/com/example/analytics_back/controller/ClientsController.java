package com.example.analytics_back.controller;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.Clients;
import com.example.analytics_back.service.ClientsService;
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
@RequestMapping("/clients")
@PreAuthorize("hasRole('MANAGER')")
public class ClientsController {

    @Autowired
    private ClientsService clientsService;
    @Autowired
    private OnlineFileImport onlineFileImport;

    @GetMapping("/{clientId}")
    public ResponseEntity<?> getClient(@PathVariable Long clientId) {
        try {
            Clients client = clientsService.getClient(clientId);
            return ResponseEntity.ok(client);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<?> clients() {
        try {
            List<Clients> clientsList = clientsService.getClients();
            return ResponseEntity.ok(clientsList);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping
    public ResponseEntity<?> clientAdd(@RequestBody Clients client) {
        try {
            Clients addedClient = clientsService.clientAdd(client);
            return ResponseEntity.ok(addedClient);
        } catch (CustomNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PutMapping
    public ResponseEntity<?> clientEdit(@RequestBody Clients clients) {
        try {
            Clients client = clientsService.clientEdit(clients);
            return ResponseEntity.ok(client);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("/{clientId}")
    public ResponseEntity<?> clientDelete(@PathVariable Long clientId) {
        try {
            clientsService.clientDelete(clientId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping("/upload-excel")
    public ResponseEntity<?> handleExcelUpload(@RequestParam("excelFile") MultipartFile file) {
        try {
            onlineFileImport.handleImportExcelFile(file);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException | IOException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
