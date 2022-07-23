package dto;

import enums.BillingInterval;
import enums.ClientStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ClientDto {

    private String id;
    private String client;
    private ClientStatus status;
    private BillingInterval billing_interval;
    private String email;
    private String fees_type;

    @NotNull(message = "fees must not be null")
    private String fees;

}
