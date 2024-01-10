package com.example.RestTemplate.controller;

import com.example.RestTemplate.exception.InsufficientBalanceException;
import com.example.RestTemplate.exception.ItemNotFoundException;
import com.example.RestTemplate.exception.ItemOutOfStockException;
import com.example.RestTemplate.exception.UnableToReturnBalanceException;
import com.example.RestTemplate.model.Item;
import com.example.RestTemplate.model.VendItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class RestTemplateController {

    @Autowired
   public  RestTemplate restTemplate;

    static final String VM_URL = "http://localhost:8080/vending-machine/";

    @GetMapping("rest/totalItem")
    public String getTotalItems()
    {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(  VM_URL+"total-items", HttpMethod.GET, entity, String.class);
        return responseEntity.getBody();
    }


    @GetMapping("rest/getItem")
    public String getItem()
    {

        HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);

         HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(VM_URL +"get-item", HttpMethod.GET, entity, String.class);
        return responseEntity.getBody();
    }


    @PostMapping("rest/initialize")
    public String initialize(@RequestBody List<Item> itemList) {
        HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);
         HttpEntity<List<Item>> entity = new HttpEntity<>(itemList, headers);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(VM_URL +"initialize", HttpMethod.POST, entity, String.class);

        return responseEntity.getBody();
    }

    @PostMapping("rest/Change")
    public String addChange(@RequestBody Map<Double, Integer> denominations) {
      HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<Double, Integer>> entity = new HttpEntity<>(denominations, headers);

    ResponseEntity<String> responseEntity = restTemplate
            .exchange(VM_URL +"add-change", HttpMethod.POST, entity, String.class);

    return responseEntity.getBody();
}



    @PutMapping("rest/vendItem")
    public ResponseEntity<String> vendItem(@RequestBody List<VendItemRequest> vendItemRequests) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {

            HttpEntity<List<VendItemRequest>> entity = new HttpEntity<>(vendItemRequests, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(VM_URL +"vend-items", HttpMethod.PUT, entity, String.class);

            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());

        } catch (HttpClientErrorException e) {

            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());

        } catch (ItemNotFoundException | ItemOutOfStockException e) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (InsufficientBalanceException | UnableToReturnBalanceException e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }


    }

}













