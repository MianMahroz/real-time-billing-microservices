package exception_handler;

public class BaseException extends Exception{

    private String code;
    private String msg;


    public BaseException(String code,String msg) {
        this.code = code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
