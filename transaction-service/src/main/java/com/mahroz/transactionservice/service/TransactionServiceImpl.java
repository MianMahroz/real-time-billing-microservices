package com.mahroz.transactionservice.service;


import com.mahroz.transactionservice.entity.TransactionEntity;
import com.mahroz.transactionservice.producer.TransactionProducer;
import com.mahroz.transactionservice.repo.TransactionRepository;
import com.mahroz.transactionservice.util.TransactionUtil;
import dto.TransactionDto;
import exception_handler.BillingServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static exception_handler.ExceptionMapper.NO_RECORD_FOUND;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService{
    private TransactionRepository transactionRepository;
    private TransactionUtil transactionUtil;

    private TransactionProducer transactionProducer;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            TransactionUtil transactionUtil,
            TransactionProducer transactionProducer){

        this.transactionRepository=transactionRepository;
        this.transactionUtil=transactionUtil;
        this.transactionProducer=transactionProducer;
    }

    /**
     *  Fetching all transaction details from db
     *  It could be pageable if load exceeds
     * @return
     */
    @Override
    public List<TransactionDto> fetchAllTransactions() {
        return transactionRepository
                .findAll()
                .stream()
                .map(transactionUtil::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Takes transaction as input and perform below operations
     * - forward payload to kafka topic for further processing
     * - create new record in db
     * @param transactionDto
     * @return
     */
    @Override
    public TransactionDto saveTransaction(TransactionDto transactionDto) {
        transactionProducer.sendTransaction(transactionDto);
        return transactionUtil.toDto(transactionRepository.save(transactionUtil.toEntity(transactionDto)));
    }

    /**
     * Takes orderId as param and return transaction object if found else throws exception
     * @param id
     * @return
     * @throws BillingServiceException
     */
    @Override
    public TransactionDto fetchTransactionById(String id) throws BillingServiceException {
        return transactionRepository
                .findById(id)
                .map(transactionUtil::toDto)
                .orElseThrow(()->new BillingServiceException(NO_RECORD_FOUND));
    }

    /**
     * Takes orderId as param and delete related record from db if found or else throws exception
     * @param orderId
     * @throws BillingServiceException
     */
    @Override
    public void removeTransactionById(String orderId) throws BillingServiceException {
        transactionRepository
                .findById(orderId)
                .map(s->{
                    transactionRepository.delete(s);
                    return s;
                })
                .orElseThrow(()->new BillingServiceException(NO_RECORD_FOUND));
    }

    /**
     * Takes transaction payload and update related record in db
     * @param transactionDto
     * @return
     */
    @Override
    public TransactionDto updateTransaction(TransactionDto transactionDto) {
       TransactionEntity eny= transactionUtil.toEntity(transactionDto);
       log.info("------UPDATE TRANS"+eny.toString());
        return transactionUtil.toDto(transactionRepository.save(eny));
    }
}
