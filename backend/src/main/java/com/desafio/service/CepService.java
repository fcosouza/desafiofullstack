package com.desafio.service;

import com.desafio.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Collections;

@Service
public class CepService {

    private final RestTemplate restTemplate;

    public CepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CepResponse validarCep(String cep) {
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new BusinessException("CEP inválido");
        }

        try {
            String url = "http://cep.la/" + cepLimpo;

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<CepLaResponse[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, CepLaResponse[].class);

            CepLaResponse[] body = response.getBody();
            if (body == null || body.length == 0) {
                throw new BusinessException("CEP não encontrado");
            }

            CepLaResponse cepLa = body[0];
            CepResponse result = new CepResponse();
            result.setCep(cepLa.getCep());
            result.setLogradouro(cepLa.getLogradouro());
            result.setComplemento(cepLa.getComplemento());
            result.setBairro(cepLa.getBairro());
            result.setLocalidade(cepLa.getCidade());
            result.setUf(cepLa.getUf());
            result.setErro(false);

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao consultar CEP: " + e.getMessage());
        }
    }

    @lombok.Getter @lombok.Setter @lombok.NoArgsConstructor
    public static class CepLaResponse {
        private String cep;
        private String uf;
        private String cidade;
        private String bairro;
        private String logradouro;
        private String complemento;
    }

    @lombok.Getter @lombok.Setter @lombok.NoArgsConstructor
    public static class CepResponse {
        private String cep;
        private String logradouro;
        private String complemento;
        private String bairro;
        private String localidade;
        private String uf;
        private boolean erro;
    }
}
