package com.desafio.dto;

import lombok.*;
import javax.validation.constraints.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaDTO {

    private Long id;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ inválido")
    private String cnpj;

    @NotBlank(message = "Nome Fantasia é obrigatório")
    @Size(max = 150)
    private String nomeFantasia;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP inválido")
    private String cep;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2)
    private String estado;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100)
    private String cidade;

    private Set<Long> fornecedorIds;
}
