package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.LoanDto;

import com.ccsw.tutorial.loan.model.LoanSearchDto;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/loan";

    private static final int TOTAL_LOANS = 7;
    private static final int PAGE_SIZE = 5;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<List<LoanDto>> responseType = new ParameterizedTypeReference<List<LoanDto>>() {
    };
    ParameterizedTypeReference<ResponsePage<LoanDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<LoanDto>>() {
    };

    @Test
    public void saveWithoutIdShouldCreateNewLoan() {
        ResponseEntity<List<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);

        int initialSize = response.getBody().size();

        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(5L);

        ClientDto client = new ClientDto();
        client.setId(4L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(new Date("2026/12/01"));
        dto.setReturnDate(new Date("2026/12/10"));

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);
        response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertEquals(initialSize + 1, response.getBody().size());

    }

    @Test
    public void modifyWithExistIdShouldModifyLoan() {

        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(2L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(new Date("2026/11/01"));
        dto.setReturnDate(new Date("2026/11/10"));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/1", HttpMethod.PUT, new HttpEntity<>(dto), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void findWithoutFiltersShouldReturnAllLoansInDB() {

        LoanSearchDto searchDto = new LoanSearchDto();

        ResponseEntity<List<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);

        assertNotNull(response);
        assertEquals(7, response.getBody().size());
    }

    @Test
    public void findByGameShouldReturnLoans() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setIdGame(1L);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), Object.class);
        assertNotNull(response);
    }

    @Test
    public void deleteWithExistsIdShouldDeleteLoan() {

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/1", HttpMethod.DELETE, null, Void.class);
        ResponseEntity<List<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(6, response.getBody().size());

    }

    @Test
    public void saveLoanWithReturnDateBeforeLoanDateShouldReturnBadRequest() {

        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(2L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(new Date("2026/12/20"));
        dto.setReturnDate(new Date("2026/12/10"));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveLoanMoreThan14DaysShouldReturnBadRequest() {
        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(5L);

        ClientDto client = new ClientDto();
        client.setId(4L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(new Date("2026/12/01"));
        dto.setReturnDate(new Date("2026/12/20"));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveLoanWithGameAlreadyLoanedShouldReturnBadRequest() {
        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(4L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(new Date("2024/12/25"));
        dto.setReturnDate(new Date("2024/12/29"));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }

    @Test
    public void saveLoanWithClientHavingMoreThanTwoLoansShouldReturnBadRequest() {
        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(5L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(new Date("2025/03/05"));
        dto.setReturnDate(new Date("2025/03/10"));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }

    @Test
    public void deleteWithNotExistsIdShouldThrowException() {
        Long notExistsLoanId = 999L;

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + notExistsLoanId, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }

    @Test
    public void modifyWithNotExistIdShouldInternalError() {
        LoanDto dto = new LoanDto();

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        dto.setGame(game);
        dto.setClient(client);

        dto.setLoanDate(new Date("2027/03/05"));
        dto.setReturnDate(new Date("2027/03/10"));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/999", HttpMethod.PUT, new HttpEntity<>(dto), Void.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }

    @Test
    public void findFirstPageWithFiveSizeShouldReturnFirstFiveResults() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(PAGE_SIZE, response.getBody().getContent().size());
    }

    @Test
    public void findSecondPageWithFiveSizeShouldReturnLastResults() {
        int elementsCount = TOTAL_LOANS - PAGE_SIZE;

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(1, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(elementsCount, response.getBody().getContent().size());
    }

}