package com.mahroz.clientservice.service;

import com.mahroz.clientservice.repo.ClientRepository;
import com.mahroz.clientservice.util.ClientUtil;
import dto.ClientDto;
import enums.BillingInterval;
import exception_handler.BillingServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import static exception_handler.ExceptionMapper.NO_RECORD_FOUND;

@Service
public class ClientServiceImpl implements ClientService{
    private ClientRepository clientRepository;
    private ClientUtil clientUtil;

    public ClientServiceImpl(
            ClientRepository clientRepository,
            ClientUtil clientUtil){

        this.clientRepository=clientRepository;
        this.clientUtil=clientUtil;
    }

    @Override
    public List<ClientDto> fetchAllClients() {
        return clientRepository
                .findAll()
                .stream()
                .map(clientUtil::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public ClientDto saveClient(ClientDto clientDto) {
      return clientUtil.toDto(clientRepository.save(clientUtil.toEntity(clientDto)));
    }

    @Override
    public ClientDto fetchClientById(String id) throws BillingServiceException {
        return clientRepository
                .findById(id)
                .map(clientUtil::toDto)
                .orElseThrow(()->new BillingServiceException(NO_RECORD_FOUND));
    }

    @Override
    public void removeClientById(String clientId) throws BillingServiceException {
        clientRepository
                .findById(clientId)
                .map(s->{
                    clientRepository.delete(s);
                    return s;
                })
                .orElseThrow(()->new BillingServiceException(NO_RECORD_FOUND));
    }

    @Override
    public ClientDto updateClient(ClientDto clientDto) {
        return clientUtil.toDto(clientRepository.save(clientUtil.toEntity(clientDto)));
    }


    @Override
    public List<ClientDto> fetchClients(List<String> clientNames, BillingInterval billingInterval){
        return clientRepository
                .getClients(clientNames,billingInterval)
                .stream()
                .map(clientUtil::toDto)
                .collect(Collectors.toList());
    }
}
