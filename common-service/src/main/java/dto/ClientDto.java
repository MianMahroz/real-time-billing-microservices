package dto;

import enums.BillingInterval;
import enums.ClientStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ClientDto {

    @NotNull(message = "id must not be null")
    private String id;

    @NotNull(message = "client must not be null")
    private String client;

    @NotNull(message = "status must not be null")
    private ClientStatus status;

    @NotNull(message = "billing_interval must not be null")
    private BillingInterval billing_interval;

    @NotNull(message = "email must not be null")
    private String email;

    @NotNull(message = "fees_type must not be null")
    private String fees_type;

    @NotNull(message = "fees must not be null")
    private String fees;

}
