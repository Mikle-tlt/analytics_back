package com.example.analytics_back.service;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Clients;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.ClientsRepository;
import com.example.analytics_back.repo.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientsService {
    private final UsersRepository usersRepository;
    private final ClientsRepository clientsRepository;

    public List<Clients> clients(Long userId) throws CustomException {
        Users user = usersRepository.getReferenceById(userId);
        if (user == null) {
            throw new CustomException("Невозможно получить данные клиентов!");
        }
        return user.getClients();
    }
    public Clients getClient(Long clientId) throws CustomException {
        Clients client = clientsRepository.findById(clientId).orElseThrow();
        if (client == null) {
            throw new CustomException("Невозможно получить данные клиента!");
        }
        return client;
    }
    public Clients clientAdd(String name, String contact, Long userId) throws CustomException {
        if (!usersRepository.existsById(userId)) {
            throw new CustomException("Ваша учетная запись недоступна для добавления профиля!");
        }
        Users user = usersRepository.getReferenceById(userId);
        if(clientsRepository.existsByNameAndContactAndOwner(name, contact, user)) {
            throw new CustomException("Клиент с соответствующими данными уже существует в системе!");
        }
        Clients client = new Clients();
        client.setName(name);
        client.setContact(contact);
        client.setOwner(user);
        return clientsRepository.save(client);
    }
    public Clients clientEdit(String name, String contact, Long clientId) throws CustomException {
        Clients client = clientsRepository.findById(clientId).orElseThrow();
        if (client == null) {
            throw new CustomException("Клиент не найден в системе!");
        }
        client.setName(name);
        client.setContact(contact);
        clientsRepository.save(client);
        return client;
    }
    public void clientDelete(Long clientId) throws CustomException {
        Clients client = clientsRepository.getReferenceById(clientId);
        if (client == null) {
            throw new CustomException("Клиент не найден в системе!");
        }
        clientsRepository.deleteById(clientId);
    }
}

