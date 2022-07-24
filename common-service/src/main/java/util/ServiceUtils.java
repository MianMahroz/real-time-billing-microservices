package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception_handler.BillingServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ServiceUtils {

    @Bean
    public ModelMapper modalMapper(){
        return new ModelMapper();
    }

    public static Map<String,String> toMap(BillingServiceException ex){
        Map<String,String> map=new HashMap<>();
        map.put("code",ex.getCode());
        map.put("msg",ex.getMsg());
        return map;
    }


}
