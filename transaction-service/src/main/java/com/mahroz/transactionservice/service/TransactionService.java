package com.mahroz.transactionservice.service;


import dto.TransactionDto;
import exception_handler.BillingServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {
    public List<TransactionDto> fetchAllTransactions();

    public TransactionDto saveTransaction(TransactionDto transactionDto);

    public TransactionDto fetchTransactionById(String orderId) throws BillingServiceException;

    public void removeTransactionById(String orderId) throws BillingServiceException;

    public TransactionDto updateTransaction(TransactionDto transactionDto);

}
