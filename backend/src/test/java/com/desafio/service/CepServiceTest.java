package com.desafio.service;

import com.desafio.dto.CepDTO;
import com.desafio.dto.CepLaDTO;
import com.desafio.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
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
    void validarCep_deveRetornarDadosViaCepLa() {
        CepLaDTO cepLa = new CepLaDTO();
        cepLa.setCep("01001-000");
        cepLa.setUf("SP");
        cepLa.setCidade("São Paulo");
        cepLa.setBairro("Sé");
        cepLa.setLogradouro("Praça da Sé");

        CepLaDTO[] body = { cepLa };
        ResponseEntity<CepLaDTO[]> response = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://cep.la/01001000"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CepLaDTO[].class)
        )).thenReturn(response);

        CepDTO resultado = cepService.validarCep("01001-000");

        assertNotNull(resultado);
        assertEquals("SP", resultado.getUf());
        assertEquals("São Paulo", resultado.getLocalidade());
        assertFalse(resultado.isErro());
    }

    @Test
    void validarCep_deveFallbackParaViaCepQuandoCepLaFalha() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(CepLaDTO[].class)))
                .thenThrow(new RestClientException("cep.la indisponível"));

        CepDTO viaCepResp = new CepDTO();
        viaCepResp.setCep("01001-000");
        viaCepResp.setUf("SP");
        viaCepResp.setLocalidade("São Paulo");
        viaCepResp.setErro(false);

        when(restTemplate.getForEntity(eq("https://viacep.com.br/ws/01001000/json/"), eq(CepDTO.class)))
                .thenReturn(new ResponseEntity<>(viaCepResp, HttpStatus.OK));

        CepDTO resultado = cepService.validarCep("01001-000");

        assertNotNull(resultado);
        assertEquals("SP", resultado.getUf());
        assertEquals("São Paulo", resultado.getLocalidade());
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
    void validarCep_deveLancarExcecaoQuandoAmbasApisFalham() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(CepLaDTO[].class)))
                .thenThrow(new RestClientException("cep.la indisponível"));

        when(restTemplate.getForEntity(anyString(), eq(CepDTO.class)))
                .thenThrow(new RestClientException("ViaCEP indisponível"));

        assertThrows(BusinessException.class, () -> cepService.validarCep("99999999"));
    }

    @Test
    void validarCep_deveLancarExcecaoQuandoViaCepRetornaErro() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(CepLaDTO[].class)))
                .thenThrow(new RestClientException("cep.la indisponível"));

        CepDTO viaCepResp = new CepDTO();
        viaCepResp.setErro(true);

        when(restTemplate.getForEntity(anyString(), eq(CepDTO.class)))
                .thenReturn(new ResponseEntity<>(viaCepResp, HttpStatus.OK));

        assertThrows(BusinessException.class, () -> cepService.validarCep("99999999"));
    }

    @Test
    void validarCep_deveLimparCaracteresNaoNumericos() {
        CepLaDTO cepLa = new CepLaDTO();
        cepLa.setCep("01001-000");
        cepLa.setUf("SP");
        cepLa.setCidade("São Paulo");

        CepLaDTO[] body = { cepLa };
        ResponseEntity<CepLaDTO[]> response = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://cep.la/01001000"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CepLaDTO[].class)
        )).thenReturn(response);

        CepDTO resultado = cepService.validarCep("01001-000");
        assertNotNull(resultado);
    }
}
