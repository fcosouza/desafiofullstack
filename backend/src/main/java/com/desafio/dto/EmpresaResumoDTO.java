package com.desafio.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaResumoDTO {
    private Long id;
    private String cnpj;
    private String nomeFantasia;
    private String cep;
    private String estado;
    private String cidade;
    private int totalFornecedores;
}
