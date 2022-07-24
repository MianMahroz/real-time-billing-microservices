package com.mahroz.transactionservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahroz.transactionservice.entity.TransactionEntity;
import dto.TransactionDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class TransactionUtil {
    private ModelMapper modelMapper;

    public TransactionUtil(ModelMapper modelMapper){
        this.modelMapper=modelMapper;
    }

    public TransactionEntity toEntity(TransactionDto transactionDto){
        return modelMapper.map(transactionDto,TransactionEntity.class);
    }

    public TransactionDto toDto(TransactionEntity transactionEntity){
        return modelMapper.map(transactionEntity,TransactionDto.class);
    }



}
