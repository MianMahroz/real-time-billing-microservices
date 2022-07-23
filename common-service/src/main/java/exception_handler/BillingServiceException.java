package exception_handler;

public class BillingServiceException extends BaseException{
    public BillingServiceException(ExceptionMapper mapper) {
        super(mapper.getCode(), mapper.getMsg());
    }
}
