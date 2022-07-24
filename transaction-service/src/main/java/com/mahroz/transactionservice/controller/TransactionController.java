package com.mahroz.transactionservice.controller;

import com.mahroz.transactionservice.service.DailyBillingCycle;
import com.mahroz.transactionservice.service.MonthlyBillingCycle;
import com.mahroz.transactionservice.service.TransactionService;
import dto.TransactionDto;
import enums.BillingInterval;
import exception_handler.BillingServiceException;
import exception_handler.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import static util.ServiceConstants.RECORD_REMOVED_SUCCESS;

@Slf4j
@RestController
@RequestMapping("trans")
public class TransactionController {

    private TransactionService transactionService;
    private DailyBillingCycle dailyBillingCycle;
    private MonthlyBillingCycle monthlyBillingCycle;


    public TransactionController(TransactionService transactionService, DailyBillingCycle dailyBillingCycle, MonthlyBillingCycle monthlyBillingCycle) {
        this.transactionService = transactionService;
        this.dailyBillingCycle = dailyBillingCycle;
        this.monthlyBillingCycle = monthlyBillingCycle;
    }

    @PostMapping("/save")
    public ResponseEntity<TransactionDto> saveTransaction(@Valid @RequestBody TransactionDto transactionDto){
        log.info("---------SAVING TRANSACTION---------");
        log.info("---------REQUEST---------");
        log.info(transactionDto.toString());
        return new ResponseEntity<>(transactionService.saveTransaction(transactionDto), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TransactionDto>> getTransactionList(){
        return new ResponseEntity<>(transactionService.fetchAllTransactions(), HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable(name="orderId")String orderId) throws BillingServiceException {
        return new ResponseEntity<>(transactionService.fetchTransactionById(orderId), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<TransactionDto> updateTransaction(@Valid @RequestBody TransactionDto transactionDto) throws BillingServiceException {
        log.info("---------UPDATING TRANSACTION---------");
        log.info("---------REQUEST---------");
        log.info(transactionDto.toString());

        if(null==transactionDto.getOrderId() || transactionDto.getOrderId().isEmpty()){
            throw new BillingServiceException(ExceptionMapper.INVALID_ORDER_ID);
        }
        return new ResponseEntity<>(transactionService.updateTransaction(transactionDto), HttpStatus.OK);
    }

    @DeleteMapping("/remove/{orderId}")
    public ResponseEntity<String> removeTransaction(@PathVariable(name="orderId")String orderId) throws BillingServiceException {
        log.info("---------REMOVING TRANSACTION---------");
        log.info("---------ORDER ID---------",orderId);

        if(null==orderId || orderId.isEmpty()){
            throw new BillingServiceException(ExceptionMapper.INVALID_ORDER_ID);
        }
        transactionService.removeTransactionById(orderId);
        return new ResponseEntity<>(RECORD_REMOVED_SUCCESS, HttpStatus.OK);
    }

    @GetMapping("/executeBillingCycle/{interval}")
    public ResponseEntity executeBillingCycle(@PathVariable(name="interval")BillingInterval billingInterval){
        if(billingInterval==BillingInterval.DAILY){
            log.info("---------DAILY BILLING CYCLE EXECUTED---------");
            dailyBillingCycle.executeBillingCycle();
        } else if (billingInterval==BillingInterval.MONTHLY) {
            log.info("---------MONTHLY BILLING CYCLE EXECUTED---------");
            monthlyBillingCycle.executeBillingCycle();
        }else{
            return ResponseEntity.ok("INVALID BILLING INTERVAL");
        }

        return ResponseEntity.ok("BILLING CYCLE COMPLETED");
    }
}
