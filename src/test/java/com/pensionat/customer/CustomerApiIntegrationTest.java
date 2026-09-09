package com.pensionat.customer;

import com.pensionat.customer.dto.CreateCustomerRequest;
import com.pensionat.customer.dto.CustomerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")

public class CustomerApiIntegrationTest {
    @LocalServerPort
    private int port;

    private String url;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    void setUp(){
        url = "http://localhost:" + port + "/api/customers";
    }

    @Test
    void shouldCreateCustomerAndReturn201(){
        CreateCustomerRequest request = new CreateCustomerRequest(

                "Anna",
                "Andersson",
                "Test@Test.com",
                "Testar",
                "0761111111"
        );

        ResponseEntity<CustomerResponse> response =
                testRestTemplate.postForEntity(
                        url,
                        request,
                        CustomerResponse.class
                );


        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        assertNotNull(response.getBody());


    }


}
