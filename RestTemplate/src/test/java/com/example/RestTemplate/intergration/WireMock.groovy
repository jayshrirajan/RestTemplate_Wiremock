package com.example.RestTemplate.intergration

import com.example.RestTemplate.model.Item;
import com.example.RestTemplate.model.VendItemRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 class WireMock {

    private RestTemplate restTemplate;

    private WireMockServer wireMockServer;
    static final String VM_URL = "http://localhost:8081/rest/";

    @Before
    void setUp() {
        this.restTemplate = new RestTemplate();
        this.wireMockServer = new WireMockServer(options().port(8081));
        this.wireMockServer.start();
    }

    @After
    void tearDown() {
        this.wireMockServer.stop();
    }

    @Test
   public void testGetTotalItems() {
        // Set up WireMock stub from JSON mapping file
        wireMockServer.stubFor(get(urlEqualTo("/rest/totalItem"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("response/totalItemResponse.json")));

        ResponseEntity<String> responseEntity = restTemplate.exchange(VM_URL + "totalItem", HttpMethod.GET, null, String.class);

        assertEquals("{\"totalItem\": 4}", responseEntity.getBody().trim());

    }

    @Test
   public void testGetItem() {
        wireMockServer.stubFor(get(urlEqualTo("/rest/getItem"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("response/getItemResponse.json")));

        ResponseEntity<String> responseEntity = restTemplate.exchange(VM_URL + "getItem", HttpMethod.GET, null, String.class);

        assertEquals("{\"itemName\": \"Soda\"}", responseEntity.getBody().trim());
    }

    @Test
    public void testInitialize() {
        wireMockServer.stubFor(post(urlEqualTo("/rest/initialize"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("response/initializeResponse.json")));

        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item(1, "A", "Soda", 1, 1.0));
        HttpEntity<List<Item>> entity = new HttpEntity<>(itemList, new HttpHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(VM_URL + "initialize", HttpMethod.POST, entity, String.class);

        assertEquals("[{\"idItem\":1,\"itemCode\":\"A\",\"itemName\":\"Soda\",\"quantity\":1,\"itemCost\":1.0}]", responseEntity.getBody().trim());

    }

    @Test
    public void testAddChange() {
        wireMockServer.stubFor(post(urlEqualTo("/rest/add-change"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("response/addChangeResponse.json")));

        Map<Double, Integer> denominations = new HashMap<>();
        denominations.put(1.0, 1);
        denominations.put(2.0, 1);
        HttpEntity<Map<Double, Integer>> entity = new HttpEntity<>(denominations, new HttpHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(VM_URL + "add-change", HttpMethod.POST, entity, String.class);

        assertEquals( "{\"message\":\"Change added successfully!!\\nCurrent total change : 3.0\"}" ,responseEntity.getBody().trim());
    }

    @Test
    public void testVendItem() {
        wireMockServer.stubFor(put(urlEqualTo("/rest/vend-items"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("response/vendItemResponse.json")));

        List<VendItemRequest> vendItemRequests = new ArrayList<>();
        vendItemRequests.add(new VendItemRequest(1, 1.0, 1));
        HttpEntity<List<VendItemRequest>> entity = new HttpEntity<>(vendItemRequests, new HttpHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(VM_URL + "vend-items", HttpMethod.PUT, entity, String.class);

//        System.out.println("actual result:" + responseEntity.getBody());
//        System.out.println("expected result:" + responseEntity.getBody());

        assertEquals("{\"message\":\"Enjoy your Soda!!! Your Change: 0.0\"}", responseEntity.getBody().trim());
    }

    @Test
    public void testInsufficientBalance() {
        wireMockServer.stubFor(put(urlEqualTo("/rest/vend-items"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withBodyFile("response/insufficientFundResponse.json")));

        // Send HTTP request to WireMock server
        List<VendItemRequest> vendItemRequests = new ArrayList<>();
        vendItemRequests.add(new VendItemRequest(1, 0.5, 1));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<VendItemRequest>> entity = new HttpEntity<>(vendItemRequests, headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(VM_URL + "vend-items", HttpMethod.PUT, entity, String.class);

        } catch (HttpClientErrorException.BadRequest e) {
            // Handle the exception and assert the error response
            assertEquals("{\"error\":\"Insufficient balance\"}", e.getResponseBodyAsString().trim());
        }
    }

    @Test
    public void testItemNotFound() {
        wireMockServer.stubFor(put(urlEqualTo("/rest/vend-items"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withBodyFile("response/itemNotFoundResponse.json")));

        List<VendItemRequest> vendItemRequests = new ArrayList<>();
        vendItemRequests.add(new VendItemRequest(11, 1.0, 1));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<VendItemRequest>> entity = new HttpEntity<>(vendItemRequests, headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(VM_URL + "vend-items", HttpMethod.PUT, entity, String.class);

        } catch (HttpClientErrorException.NotFound e) {
            assertEquals("{\"error\":\"Item not found\"}", e.getResponseBodyAsString().trim());
        } catch (ResourceAccessException e) {
            System.out.println("ResourceAccessException: " + e.getMessage());
        }

    }


























































    //-------------------------- wiremock with rest and response as direct withBody----------------

//    @Test
//    public void testGetTotalItems() {
//        // Set up WireMock stub
//        this.wireMockServer.stubFor(get(urlEqualTo("/rest/totalItem"))
//                .willReturn(aResponse()
//                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .withBody("4")));
//
//        // Send HTTP request to WireMock server
//        ResponseEntity<String> responseEntity = this.restTemplate.exchange(VM_URL + "totalItem", HttpMethod.GET, null, String.class);
//
//        // Assert that the response body matches the expected value
//        assertEquals("4", responseEntity.getBody());
//    }

//    @Test
//    public void testGetItem() {
//
//        this.wireMockServer.stubFor(get(urlEqualTo("/rest/getItem"))
//                .willReturn(aResponse()
//                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .withBody("{\"item\": \"chips\"}")));
//
//        ResponseEntity<String> responseEntity = this.restTemplate.exchange(VM_URL + "getItem", HttpMethod.GET, null, String.class);
//
//
//        assertEquals("{\"item\": \"chips\"}", responseEntity.getBody());
//    }
//
//    @Test
//    public void testInitialize() {
//
//        this.wireMockServer.stubFor(post(urlEqualTo("/rest/initialize"))
//                .willReturn(aResponse()
//                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .withBody("{\"status\": \"success\"}")));
//
//        List<Item> itemList = new ArrayList<>();
//        itemList.add(new Item(1, "A", "Soda", 1, 1.0));
//        HttpEntity<List<Item>> entity = new HttpEntity<>(itemList, new HttpHeaders());
//        ResponseEntity<String> responseEntity = this.restTemplate.exchange(VM_URL + "initialize", HttpMethod.POST, entity, String.class);
//
//        assertEquals("{\"status\": \"success\"}", responseEntity.getBody());
//    }
//
//    @Test
//    public void testAddChange() {
//
//        this.wireMockServer.stubFor(post(urlEqualTo("/rest/add-change"))
//                .willReturn(aResponse()
//                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .withBody("{\"status\": \"success\"}")));
//
//        Map<Double, Integer> denominations = new HashMap<>();
//        denominations.put(1.0, 1);
//        denominations.put(2.0, 1);
//        HttpEntity<Map<Double, Integer>> entity = new HttpEntity<>(denominations, new HttpHeaders());
//        ResponseEntity<String> responseEntity = this.restTemplate.exchange(VM_URL + "add-change", HttpMethod.POST, entity, String.class);
//
//
//        assertEquals("{\"status\": \"success\"}", responseEntity.getBody());
//    }
//
//    @Test
//    public void testVendItem() {
//        // Set up WireMock stub for success response
//        this.wireMockServer.stubFor(put(urlEqualTo("/rest/vend-items"))
//                .willReturn(aResponse()
//                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .withBody("{\"status\": \"success\"}")));
//
//        List<VendItemRequest> vendItemRequests = new ArrayList<>();
//        vendItemRequests.add(new VendItemRequest(1, 1.0, 1));
//        HttpEntity<List<VendItemRequest>> entity = new HttpEntity<>(vendItemRequests, new HttpHeaders());
//        ResponseEntity<String> responseEntity = this.restTemplate.exchange(VM_URL + "vend-items", HttpMethod.PUT, entity, String.class);
//
//        assertEquals("{\"status\": \"success\"}", responseEntity.getBody());
//    }
//
//    @Test
//    public void testInsufficientBalance() {
//        // Set up WireMock stub for error response
//        this.wireMockServer.stubFor(put(urlEqualTo("/rest/vend-items"))
//                .willReturn(aResponse()
//                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .withStatus(HttpStatus.BAD_REQUEST.value())
//                        .withBody("{\"error\": \"Insufficient balance\"}")));
//
//        // Send HTTP request to WireMock server
//        List<VendItemRequest> vendItemRequests = new ArrayList<>();
//        vendItemRequests.add(new VendItemRequest(1, 0.5, 1));
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<List<VendItemRequest>> entity = new HttpEntity<>(vendItemRequests, headers);
//
//        try {
//            ResponseEntity<String> responseEntity = this.restTemplate.exchange(VM_URL + "vend-items", HttpMethod.PUT, entity, String.class);
//
//        } catch (HttpClientErrorException.BadRequest e) {
//            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
//            assertEquals("{\"error\": \"Insufficient balance\"}", e.getResponseBodyAsString());
//        } catch (ResourceAccessException e) {
//            System.out.println("ResourceAccessException: " + e.getMessage());
//
//        }
//    }
//
//    @Test
//    public void testItemNotFoundException() {
//        String errorMessage = "Item not found";
//        this.wireMockServer.stubFor(put(urlEqualTo("/rest/vend-items"))
//                .willReturn(aResponse()
//                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .withStatus(HttpStatus.NOT_FOUND.value())
//                        .withBody("{\"error\": \"" + errorMessage + "\"}")));
//
//        // Send HTTP request to WireMock server
//        List<VendItemRequest> vendItemRequests = new ArrayList<>();
//        vendItemRequests.add(new VendItemRequest(10, 1.0, 1));
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<List<VendItemRequest>> entity = new HttpEntity<>(vendItemRequests, headers);
//
//        try {
//            ResponseEntity<String> responseEntity = this.restTemplate.exchange(VM_URL + "vend-items", HttpMethod.PUT, entity, String.class);
//
//        } catch (HttpClientErrorException.NotFound e) {
//            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
//            assertEquals("{\"error\": \"" + errorMessage + "\"}", e.getResponseBodyAsString());
//        } catch (ResourceAccessException e) {
//            System.out.println("ResourceAccessException: " + e.getMessage());
//        }
//    }
}
