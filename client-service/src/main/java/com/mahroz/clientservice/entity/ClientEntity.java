package com.mahroz.clientservice.entity;

import enums.BillingInterval;
import enums.ClientStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

@Entity
@Table(name = "client_info")
@Data
public class ClientEntity {

    @Id
    @Column
    private String id;

    @Column
    private String client;

    @Column
    private ClientStatus status;

    @Column
    private BillingInterval billing_interval;

    @Column
    private String email;

    @Column
    private String fees_type;

    @Column
    private String fees;

}
