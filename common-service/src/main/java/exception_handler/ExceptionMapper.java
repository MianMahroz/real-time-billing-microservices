package exception_handler;

import static util.ServiceConstants.*;

public enum ExceptionMapper {

    NO_RECORD_FOUND("7001",NO_RECORD_FOUND_MSG),
    INVALID_CLIENT_ID("7002",INVALID_CLIENT_ID_MSG),

    INVALID_ORDER_ID("7003",INVALID_ORDER_ID_MSG);


    private String code;
    private String msg;

    ExceptionMapper(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
