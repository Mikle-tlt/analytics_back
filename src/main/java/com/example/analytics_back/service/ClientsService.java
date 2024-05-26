package com.example.analytics_back.service;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.Clients;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.ClientsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientsService {
    private final ClientsRepository clientsRepository;
    private final UsersService usersService;

    public Clients getClient(Long clientId) {
        return clientsRepository.findById(clientId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить данные клиента!"));
    }

    public List<Clients> getClients()  {
        Users user = usersService.getUserInfo();
        return user.getClients();
    }
    public Clients clientAdd(Clients client) throws CustomException {
        String name = client.getName();
        String contact = client.getContact();
        Users user = usersService.getUserInfo();
        if(clientsRepository.existsByNameAndContactAndOwner(name, contact, user)) {
            throw new CustomException("Клиент с соответствующими данными уже существует в системе!");
        }
        Clients addedClient = new Clients();
        addedClient.setName(name);
        addedClient.setContact(contact);
        addedClient.setOwner(user);
        return clientsRepository.save(addedClient);
    }
    public Clients clientEdit(Clients client) {
        Clients editedClient = getClient(client.getId());
        editedClient.setName(client.getName());
        editedClient.setContact(client.getContact());
        clientsRepository.save(editedClient);
        return editedClient;
    }
    public void clientDelete(Long clientId) {
        Clients client = getClient(clientId);
        clientsRepository.delete(client);
    }
}

