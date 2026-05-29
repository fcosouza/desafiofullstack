package com.desafio.repository;

import com.desafio.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Optional<Fornecedor> findByCpfCnpj(String cpfCnpj);
    boolean existsByCpfCnpj(String cpfCnpj);

    @Query("SELECT f FROM Fornecedor f WHERE " +
           "(:nome IS NULL OR LOWER(f.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:cpfCnpj IS NULL OR f.cpfCnpj LIKE CONCAT('%', :cpfCnpj, '%'))")
    List<Fornecedor> filtrar(@Param("nome") String nome, @Param("cpfCnpj") String cpfCnpj);
}
