package com.desafio.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "fornecedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "empresas")
@ToString(exclude = "empresas")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false, length = 8)
    private TipoPessoa tipoPessoa;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 18)
    private String cpfCnpj;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(length = 20)
    private String rg;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @ManyToMany(mappedBy = "fornecedores")
    @Builder.Default
    private Set<Empresa> empresas = new HashSet<>();

    public enum TipoPessoa {
        FISICA, JURIDICA
    }
}
