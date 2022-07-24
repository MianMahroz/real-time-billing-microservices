package com.mahroz.clientservice.entity;

import enums.BillingInterval;
import enums.ClientStatus;
import lombok.Data;

import javax.persistence.*;

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
    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private BillingInterval billing_interval;

    @Column
    private String email;

    @Column
    private String fees_type;

    @Column
    private String fees;

}
