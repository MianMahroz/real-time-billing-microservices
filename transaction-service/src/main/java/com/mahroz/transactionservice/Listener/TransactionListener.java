package com.mahroz.transactionservice.Listener;


import com.mahroz.transactionservice.service.TransactionService;
import dto.TransactionDto;
import enums.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Branched;
import org.springframework.stereotype.Service;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Service
public class TransactionListener {



    private TransactionService transactionService;

    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Takes only approved transactions and perform below operations
     * update trans status to "BILLED" and forward the transaction to email service
     * @return
     */
    @Bean
    public Function<KStream<String, TransactionDto>,KStream<String, TransactionDto>> approvedTransProcess(){
        return  kStream -> {

            kStream
                    .mapValues(trans->{

                        log.info("---------APPROVED TRANSACTION PROCESSING START---------");
                        log.info("---------STREAM PAYLOAD---------");
                        log.info(trans.toString());

                        log.info("---------UPDATING STATUS FROM APPROVED -> BILLED---------");
                        trans.setStatus(TransactionStatus.BILLED);
                        transactionService.updateTransaction(trans);

                        /*
                          Forwarding to email service
                         */
                        sendEmail(trans);

                        return trans;
                    });

            return kStream;
        };
    }


    /**
     * Takes BILLED status trans and forward an email to client
     * This could be another microservice
     * @param transactionDto
     */
    public void sendEmail(TransactionDto transactionDto){

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            log.info("---------SENDING EMAIL TO CLIENT---------");

            // Simulate a long-running Job
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            log.info("I'll run in a separate thread than the main thread.");
        });
    }
}
