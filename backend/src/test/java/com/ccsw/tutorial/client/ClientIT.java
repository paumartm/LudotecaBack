package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.ClientDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClientIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/client";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<List<ClientDto>> responseType = new ParameterizedTypeReference<List<ClientDto>>() {
    };


    @Test
    public void findAllShouldReturnClients() {
        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(4, response.getBody().size());
    }


    @Test
    public void saveWithoutIdShouldCreateNewClient() {
        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);

        int initialSize = response.getBody().size();

        ClientDto dto = new ClientDto();
        dto.setName("Juan");

        restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET,
                null,
                responseType);
        assertEquals(initialSize + 1, response.getBody().size());
    }

    @Test
    public void modifyWithExistIdShouldModifyClient() {

        ClientDto dto = new ClientDto();
        dto.setName("NuevoNombre");

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/1",
                HttpMethod.PUT,
                new HttpEntity<>(dto),
                Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }



    @Test
    public void deleteWithNotExistsIdShouldThrowException() {
        Long notExistsClientId = 999L;

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + notExistsClientId, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }

    @Test
    public void saveWithDuplicatedNameShouldReturnError() {

        ClientDto dto = new ClientDto();
        dto.setName("Pepe");

        ResponseEntity<?> response = restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.PUT,
                new HttpEntity<>(dto),
                Void.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}