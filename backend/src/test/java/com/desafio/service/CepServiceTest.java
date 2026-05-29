package com.desafio.service;

import com.desafio.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CepServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private CepService cepService;

    @BeforeEach
    void setUp() {
        cepService = new CepService(restTemplate);
    }

    @Test
    void validarCep_deveRetornarDadosQuandoCepValido() {
        CepService.CepLaResponse cepLa = new CepService.CepLaResponse();
        cepLa.setCep("01001-000");
        cepLa.setUf("SP");
        cepLa.setCidade("São Paulo");
        cepLa.setBairro("Sé");
        cepLa.setLogradouro("Praça da Sé");

        CepService.CepLaResponse[] body = { cepLa };
        ResponseEntity<CepService.CepLaResponse[]> response = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://cep.la/01001000"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CepService.CepLaResponse[].class)
        )).thenReturn(response);

        CepService.CepResponse resultado = cepService.validarCep("01001-000");

        assertNotNull(resultado);
        assertEquals("SP", resultado.getUf());
        assertEquals("São Paulo", resultado.getLocalidade());
        assertEquals("Sé", resultado.getBairro());
        assertFalse(resultado.isErro());
    }

    @Test
    void validarCep_deveLancarExcecaoParaCepComMenosDe8Digitos() {
        assertThrows(BusinessException.class, () -> cepService.validarCep("1234"));
    }

    @Test
    void validarCep_deveLancarExcecaoParaCepVazio() {
        assertThrows(BusinessException.class, () -> cepService.validarCep(""));
    }

    @Test
    void validarCep_deveLancarExcecaoQuandoApiRetornaVazio() {
        CepService.CepLaResponse[] body = {};
        ResponseEntity<CepService.CepLaResponse[]> response = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(CepService.CepLaResponse[].class)))
                .thenReturn(response);

        assertThrows(BusinessException.class, () -> cepService.validarCep("99999999"));
    }

    @Test
    void validarCep_deveLancarExcecaoQuandoApiRetornaNull() {
        ResponseEntity<CepService.CepLaResponse[]> response = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(CepService.CepLaResponse[].class)))
                .thenReturn(response);

        assertThrows(BusinessException.class, () -> cepService.validarCep("99999999"));
    }

    @Test
    void validarCep_deveLimparCaracteresNaoNumericos() {
        CepService.CepLaResponse cepLa = new CepService.CepLaResponse();
        cepLa.setCep("01001-000");
        cepLa.setUf("SP");
        cepLa.setCidade("São Paulo");

        CepService.CepLaResponse[] body = { cepLa };
        ResponseEntity<CepService.CepLaResponse[]> response = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://cep.la/01001000"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CepService.CepLaResponse[].class)
        )).thenReturn(response);

        CepService.CepResponse resultado = cepService.validarCep("01001-000");

        assertNotNull(resultado);
        assertEquals("SP", resultado.getUf());
    }
}
