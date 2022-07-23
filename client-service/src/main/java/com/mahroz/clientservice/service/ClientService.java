package com.mahroz.clientservice.service;

import dto.ClientDto;
import exception_handler.BillingServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {

    public List<ClientDto> fetchAllClients();

    public ClientDto saveClient(ClientDto clientDto);

    public ClientDto fetchClientById(String id) throws BillingServiceException;

    public void removeClientById(String id) throws BillingServiceException;

    public ClientDto updateClient(ClientDto clientDto);

}
