package com.mahroz.transactionservice.entity;

import enums.CardType;
import enums.TransactionCurrency;
import enums.TransactionStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transaction_details")
@Data
public class TransactionEntity {
    @Id
    @Column
    private String orderId;

    @Column
    private String orderName;

    @Column
    private String dateTime;

    @Column
    private String amount;

    @Column
    private TransactionCurrency currency;

    @Column
    private CardType cardType;

    @Column
    private TransactionStatus status;

    @Column
    private String client;


}
