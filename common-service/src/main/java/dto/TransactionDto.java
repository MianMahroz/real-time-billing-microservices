package dto;

import enums.CardType;
import enums.TransactionCurrency;
import enums.TransactionStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TransactionDto {

    @NotNull(message = "orderId must not be null")
    private String orderId;

    @NotNull(message = "orderName must not be null")
    private String orderName;

    @NotNull(message = "dateTime must not be null")
    private String dateTime;

    @NotNull(message = "amount must not be null")
    private String amount;

    @NotNull(message = "currency must not be null")
    private TransactionCurrency currency;

    @NotNull(message = "cardType must not be null")
    private CardType cardType;

    @NotNull(message = "status must not be null")
    private TransactionStatus status;

    @NotNull(message = "client must not be null")
    private String client;

}
