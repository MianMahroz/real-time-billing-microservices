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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static util.ServiceUtils.distinctByKey;

@Slf4j
abstract class BillingCycle {

    List<ClientDto> clientDetails=null;
    List<TransactionDto> transList=null;

    private ClientService clientService;
    private TransactionProducer transactionProducer;
    private TransactionService transactionService;

    public BillingCycle(ClientService clientService, TransactionProducer transactionProducer, TransactionService transactionService) {
        this.clientService = clientService;
        this.transactionProducer = transactionProducer;
        this.transactionService = transactionService;
    }


    private void getAllNonBillableTransactions(){
        transList = transactionService.fetchTransactionsByStatus(TransactionStatus.APPROVED);
        log.info("---------TOTAL NON-BILLABLE TRANSACTIONS---------"+(null!=transList?transList.size():0));

    }
    abstract void collectCustomerDetails();


    public void executeBillingCycle(){
        getAllNonBillableTransactions();
        collectCustomerDetails();
        sendTransactionToProcessor();
    }

    /**
     * Forwarding Transaction to Transaction Processor
     */
    private void sendTransactionToProcessor(){

        transList.forEach(trans->{
            if(clientDetails.stream().filter(s->s.getClient().equals(trans.getClient())).count()>0){
                transactionProducer.sendTransaction(trans);  // sending payload to transaction processor
            }else{
                log.info("---------ELIMINATED FROM MONTHLY BILLING CYCLE---------");
                log.info("---------PAYLOAD---------"+trans);
            }
        });
    }
}
