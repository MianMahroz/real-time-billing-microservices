package com.mahroz.clientservice.controller;


import com.mahroz.clientservice.service.ClientService;
import dto.ClientDto;
import enums.BillingInterval;
import exception_handler.BillingServiceException;
import exception_handler.ExceptionMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


import static util.ServiceConstants.*;

@RestController
@RequestMapping("/client")
public class ClientRestController {

    private ClientService clientService;

    ClientRestController(ClientService clientService){
        this.clientService=clientService;
    }

    @PostMapping("/save")
    public ResponseEntity<ClientDto> saveClient(@Valid @RequestBody ClientDto clientDto){
        return new ResponseEntity<>(clientService.saveClient(clientDto), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ClientDto>> getClientList(){
        return new ResponseEntity<>(clientService.fetchAllClients(), HttpStatus.OK);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientDto> getClient(@PathVariable(name="clientId")String clientId) throws BillingServiceException {
        return new ResponseEntity<>(clientService.fetchClientById(clientId), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ClientDto> updateClient(@Valid @RequestBody ClientDto clientDto) throws BillingServiceException {
        if(null==clientDto.getId() || clientDto.getId().isEmpty()){
            throw new BillingServiceException(ExceptionMapper.INVALID_CLIENT_ID);
        }
        return new ResponseEntity<>(clientService.updateClient(clientDto), HttpStatus.OK);
    }

    @DeleteMapping("/remove/{clientId}")
    public ResponseEntity<String> removeClient(@PathVariable(name="clientId")String clientId) throws BillingServiceException {

        if(null==clientId || clientId.isEmpty()){
            throw new BillingServiceException(ExceptionMapper.INVALID_CLIENT_ID);
        }
        clientService.removeClientById(clientId);
        return new ResponseEntity<>(RECORD_REMOVED_SUCCESS, HttpStatus.OK);
    }


    @PostMapping("/list/{interval}")
    public ResponseEntity<List<ClientDto>> getClients(@RequestBody List<String> clientNames,@PathVariable(name="interval") BillingInterval billingInterval){
        return ResponseEntity.ok(clientService.fetchClients(clientNames,billingInterval));
    }

}
