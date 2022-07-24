package com.mahroz.transactionservice.service;


import com.mahroz.transactionservice.feign.ClientService;
import com.mahroz.transactionservice.producer.TransactionProducer;
import dto.ClientDto;
import dto.TransactionDto;
import enums.BillingInterval;
import enums.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static util.ServiceUtils.distinctByKey;

@Service
@Slf4j
public class DailyBillingCycle extends BillingCycle {

    private TransactionService transactionService;

    private ClientService clientService;

    private TransactionProducer transactionProducer;

    public DailyBillingCycle(TransactionService transactionService, ClientService clientService, TransactionProducer transactionProducer) {
        super(clientService,transactionProducer,transactionService);
        this.transactionService = transactionService;
        this.clientService = clientService;
        this.transactionProducer = transactionProducer;
    }

    @Override
    void collectCustomerDetails() {

        List<String> distinctClientNames = transList
                .stream()
                .filter(distinctByKey(s->s.getClient()))
                .map(s->s.getClient())
                .collect(Collectors.toList());
        log.info("---------TOTAL CLIENTS TO BE BILLED---------"+(null!=distinctClientNames?distinctClientNames.size():0));

         /*
            Fetching client details from client service to determine, which client has monthly plan
          */
        ResponseEntity<List<ClientDto>> clientResponse = clientService.getClients(distinctClientNames, BillingInterval.DAILY);
        if(clientResponse.getStatusCode()== HttpStatus.OK && clientResponse.hasBody()){
            clientDetails=clientResponse.getBody();
        }


    }
}
