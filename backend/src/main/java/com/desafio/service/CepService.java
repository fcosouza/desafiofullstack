package com.desafio.service;

import com.desafio.dto.CepDTO;
import com.desafio.dto.CepLaDTO;
import com.desafio.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CepService {

    private final RestTemplate restTemplate;

    public CepDTO validarCep(String cep) {
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new BusinessException("CEP inválido");
        }

        // Tenta cep.la primeiro (conforme requisito do desafio)
        try {
            return consultarCepLa(cepLimpo);
        } catch (Exception e) {
            // Fallback para ViaCEP caso cep.la esteja indisponível
            try {
                return consultarViaCep(cepLimpo);
            } catch (BusinessException be) {
                throw be;
            } catch (Exception ex) {
                throw new BusinessException("Erro ao consultar CEP: " + ex.getMessage());
            }
        }
    }

    private CepDTO consultarCepLa(String cepLimpo) {
        String url = "http://cep.la/" + cepLimpo;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CepLaDTO[]> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, CepLaDTO[].class);

        CepLaDTO[] body = response.getBody();
        if (body == null || body.length == 0) {
            throw new BusinessException("CEP não encontrado");
        }

        CepLaDTO cepLa = body[0];
        return CepDTO.builder()
                .cep(cepLa.getCep())
                .logradouro(cepLa.getLogradouro())
                .complemento(cepLa.getComplemento())
                .bairro(cepLa.getBairro())
                .localidade(cepLa.getCidade())
                .uf(cepLa.getUf())
                .erro(false)
                .build();
    }

    private CepDTO consultarViaCep(String cepLimpo) {
        String url = "https://viacep.com.br/ws/" + cepLimpo + "/json/";
        ResponseEntity<CepDTO> response = restTemplate.getForEntity(url, CepDTO.class);

        if (response.getBody() == null || response.getBody().isErro()) {
            throw new BusinessException("CEP não encontrado");
        }

        return response.getBody();
    }
}
