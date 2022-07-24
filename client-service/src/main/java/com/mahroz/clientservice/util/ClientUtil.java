package com.mahroz.clientservice.util;

import com.mahroz.clientservice.entity.ClientEntity;
import dto.ClientDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ClientUtil {
    private ModelMapper modelMapper;

    public ClientUtil(ModelMapper modelMapper){
        this.modelMapper=modelMapper;
    }

    public ClientEntity toEntity(ClientDto clientDto){
        return modelMapper.map(clientDto,ClientEntity.class);
    }

    public ClientDto toDto(ClientEntity clientEntity){
        return modelMapper.map(clientEntity,ClientDto.class);
    }


}
