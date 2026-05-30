package com.desafio.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CepLaDTO {

    private String cep;
    private String uf;
    private String cidade;
    private String bairro;
    private String logradouro;
    private String complemento;
}
