package com.desafio.dto;

import com.desafio.entity.Fornecedor.TipoPessoa;
import lombok.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FornecedorDTO {

    private Long id;

    @NotNull(message = "Tipo Pessoa é obrigatório")
    private TipoPessoa tipoPessoa;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    private String cpfCnpj;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP inválido")
    private String cep;

    private String rg;

    private LocalDate dataNascimento;

    private Set<Long> empresaIds;
}
