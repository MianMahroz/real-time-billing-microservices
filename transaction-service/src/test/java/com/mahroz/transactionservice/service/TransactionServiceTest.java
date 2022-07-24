package com.mahroz.transactionservice.service;

import com.mahroz.transactionservice.entity.TransactionEntity;
import com.mahroz.transactionservice.producer.TransactionProducer;
import com.mahroz.transactionservice.repo.TransactionRepository;
import com.mahroz.transactionservice.util.TransactionUtil;
import dto.ClientDto;
import dto.TransactionDto;
import exception_handler.BillingServiceException;
import exception_handler.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.ServiceConstants.NO_RECORD_FOUND_MSG;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionProducer transactionProducer;
    @Autowired
    private TransactionUtil transactionUtil;

    @Autowired
    private ModelMapper modelMapper;
    EasyRandom generator = new EasyRandom();
    private AutoCloseable closeable;

    @Captor
    ArgumentCaptor<TransactionEntity> transactionEntityArgumentCaptor;

    @Captor
    ArgumentCaptor<String> orderIdCaptor;


    @BeforeEach
    public void setup() {
        modelMapper=new ModelMapper();
        transactionUtil=new TransactionUtil(modelMapper);
        transactionService = new TransactionServiceImpl(transactionRepository,transactionUtil,transactionProducer);
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void saveTransaction_WHEN_REQUEST_IS_VALID() throws Exception{
        TransactionDto transactionDto = generator.nextObject(TransactionDto.class);
        TransactionEntity transactionEntity = generator.nextObject(TransactionEntity.class);

        when(transactionRepository.save(any())).thenReturn(transactionEntity);
        transactionService.saveTransaction(transactionDto);
        verify(transactionRepository, times(1)).save(transactionEntityArgumentCaptor.capture());

        assertEquals(transactionDto.getOrderId(), transactionEntityArgumentCaptor.getValue().getOrderId());

    }

    @Test
    public void fetchAllTransactions_SUCCESS() throws Exception{
        List<TransactionEntity> transactionEntityList = generator.objects(TransactionEntity.class, 3).collect(Collectors.toList());
        List<TransactionDto> expectedResponse = transactionEntityList.stream().map(transactionUtil::toDto).collect(Collectors.toList());

        when(transactionRepository.findAll()).thenReturn(transactionEntityList);
        List<TransactionDto> responseList = transactionService.fetchAllTransactions();

        verify(transactionRepository, times(1)).findAll();

        assertEquals(expectedResponse.size(), responseList.size());
        assertEquals(expectedResponse.get(0).getOrderId(), responseList.get(0).getOrderId());

    }


    @Test
    public void fetchTransactionById_SUCCESS() throws BillingServiceException {
        TransactionEntity transactionEntity = generator.nextObject(TransactionEntity.class);

        when(transactionRepository.findById(any())).thenReturn(Optional.of(transactionEntity));
        transactionService.fetchTransactionById(transactionEntity.getOrderId());

        verify(transactionRepository, times(1)).findById(orderIdCaptor.capture());
        assertEquals(transactionEntity.getOrderId(), orderIdCaptor.getValue());

    }

    @Test
    public void fetchTransactionById_WHEN_NOT_FOUND() throws BillingServiceException {
        TransactionEntity transactionEntity = generator.nextObject(TransactionEntity.class);

        when(transactionRepository.findById(any())).thenReturn(Optional.empty());
        BillingServiceException exception = assertThrows(BillingServiceException.class, () -> {
            transactionService.fetchTransactionById(transactionEntity.getOrderId());
        });

        verify(transactionRepository, times(1)).findById(orderIdCaptor.capture());

        assertEquals(transactionEntity.getOrderId(), orderIdCaptor.getValue());
        assertTrue(ExceptionMapper.NO_RECORD_FOUND.getMsg().contains(exception.getMsg()));
        assertTrue(ExceptionMapper.NO_RECORD_FOUND.getCode().equals(exception.getCode()));

    }



}
