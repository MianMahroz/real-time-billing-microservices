package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception_handler.BillingServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

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
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


}
