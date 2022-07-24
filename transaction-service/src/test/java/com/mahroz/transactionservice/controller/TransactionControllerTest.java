package com.mahroz.transactionservice.controller;

import com.mahroz.transactionservice.entity.TransactionEntity;
import com.mahroz.transactionservice.feign.ClientService;
import com.mahroz.transactionservice.producer.TransactionProducer;
import com.mahroz.transactionservice.repo.TransactionRepository;
import com.mahroz.transactionservice.service.DailyBillingCycle;
import com.mahroz.transactionservice.service.MonthlyBillingCycle;
import com.mahroz.transactionservice.service.TransactionService;
import com.mahroz.transactionservice.service.TransactionServiceImpl;
import com.mahroz.transactionservice.util.TransactionUtil;
import dto.TransactionDto;
import enums.BillingInterval;
import enums.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;


import dto.ClientDto;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.ServiceConstants.NO_RECORD_FOUND_MSG;
import static util.ServiceUtils.distinctByKey;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionProducer transactionProducer;
    @Mock
    private TransactionUtil transactionUtil;

    @Mock
    private MonthlyBillingCycle monthlyBillingCycle;

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private DailyBillingCycle dailyBillingCycle;

    @Mock
    private ClientService clientService;

    @Autowired
    private ModelMapper modelMapper;
    EasyRandom generator = new EasyRandom();
    private AutoCloseable closeable;



    @BeforeEach
    public void setup() {
        modelMapper=new ModelMapper();
        transactionUtil=new TransactionUtil(modelMapper);
        transactionService = new TransactionServiceImpl(transactionRepository,transactionUtil,transactionProducer);
        monthlyBillingCycle=new MonthlyBillingCycle(transactionService,clientService,transactionProducer);
        dailyBillingCycle=new DailyBillingCycle(transactionService,clientService,transactionProducer);
        transactionController=new TransactionController(transactionService,dailyBillingCycle,monthlyBillingCycle);
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void executeMonthlyBillingCycle_SUCCESS() throws Exception{

        List<TransactionDto> transactionList = generator.objects(TransactionDto.class, 1).collect(Collectors.toList());
        List<ClientDto> clientList=generator.objects(ClientDto.class, 1).collect(Collectors.toList());

        List<String> distinctClientNames = transactionList
                .stream()
                .filter(distinctByKey(s->s.getClient()))
                .map(s->s.getClient())
                .collect(Collectors.toList());

        when(transactionService.fetchTransactionsByStatus(TransactionStatus.APPROVED)).thenReturn(transactionList);
        when(clientService.getClients(distinctClientNames, BillingInterval.MONTHLY)).thenReturn(ResponseEntity.ok(clientList));

        transactionController.executeBillingCycle(BillingInterval.MONTHLY);

        verify(monthlyBillingCycle, times(1)).executeBillingCycle();
        verify(dailyBillingCycle, times(0)).executeBillingCycle();

    }

    @Test
    public void executeDailyBillingCycle_SUCCESS() throws Exception{

        List<TransactionDto> transactionList = generator.objects(TransactionDto.class, 1).collect(Collectors.toList());
        List<ClientDto> clientList=generator.objects(ClientDto.class, 1).collect(Collectors.toList());

        List<String> distinctClientNames = transactionList
                .stream()
                .filter(distinctByKey(s->s.getClient()))
                .map(s->s.getClient())
                .collect(Collectors.toList());

        when(transactionService.fetchTransactionsByStatus(TransactionStatus.APPROVED)).thenReturn(transactionList);
        when(clientService.getClients(distinctClientNames, BillingInterval.DAILY)).thenReturn(ResponseEntity.ok(clientList));

        transactionController.executeBillingCycle(BillingInterval.DAILY);

        verify(monthlyBillingCycle, times(0)).executeBillingCycle();
        verify(dailyBillingCycle, times(1)).executeBillingCycle();


    }
}
