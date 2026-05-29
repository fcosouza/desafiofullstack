package com.desafio.dto;

import com.desafio.entity.Fornecedor.TipoPessoa;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FornecedorResumoDTO {
    private Long id;
    private TipoPessoa tipoPessoa;
    private String nome;
    private String cpfCnpj;
    private String email;
    private String cep;
    private String rg;
    private LocalDate dataNascimento;
    private int totalEmpresas;
}
