package com.mahroz.clientservice.service;

import com.mahroz.clientservice.entity.ClientEntity;
import enums.BillingInterval;
import enums.ClientStatus;
import com.mahroz.clientservice.repo.ClientRepository;
import com.mahroz.clientservice.util.ClientUtil;
import dto.ClientDto;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


//@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;
    @Mock
    private ClientRepository clientRepository;

    @Autowired
    private ClientUtil clientUtil;

    @Autowired
    private ModelMapper modelMapper;
    EasyRandom generator = new EasyRandom();
    private AutoCloseable closeable;

    @Captor
    ArgumentCaptor<ClientEntity> clientEntityArgumentCaptor;


    @BeforeEach
    public void setup() {
        modelMapper=new ModelMapper();
        clientUtil=new ClientUtil(modelMapper);
        clientService = new ClientServiceImpl(clientRepository,clientUtil);
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void saveClient_SUCCESS() throws Exception{
        ClientDto clientDto = generator.nextObject(ClientDto.class);
        ClientEntity clientEntity = generator.nextObject(ClientEntity.class);

        when(clientRepository.save(any())).thenReturn(clientEntity);

        clientService.saveClient(clientDto);

        verify(clientRepository, times(1)).save(clientEntityArgumentCaptor.capture());

        assertEquals(clientDto.getId(), clientEntityArgumentCaptor.getValue().getId());

    }
}
