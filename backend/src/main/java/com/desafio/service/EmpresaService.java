package com.desafio.service;

import com.desafio.dto.EmpresaDTO;
import com.desafio.dto.EmpresaResumoDTO;
import com.desafio.entity.Empresa;
import com.desafio.entity.Fornecedor;
import com.desafio.exception.BusinessException;
import com.desafio.exception.ResourceNotFoundException;
import com.desafio.repository.EmpresaRepository;
import com.desafio.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final CepService cepService;

    @Transactional(readOnly = true)
    public List<EmpresaResumoDTO> listar() {
        return empresaRepository.findAll().stream()
                .map(this::toResumoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpresaDTO buscarPorId(Long id) {
        Empresa empresa = findOrFail(id);
        return toDTO(empresa);
    }

    @Transactional
    public EmpresaDTO criar(EmpresaDTO dto) {
        validarCnpjUnico(dto.getCnpj(), null);
        cepService.validarCep(dto.getCep());

        Empresa empresa = toEntity(dto);
        vincularFornecedores(empresa, dto.getFornecedorIds());
        empresa = empresaRepository.save(empresa);
        return toDTO(empresa);
    }

    @Transactional
    public EmpresaDTO atualizar(Long id, EmpresaDTO dto) {
        Empresa empresa = findOrFail(id);
        validarCnpjUnico(dto.getCnpj(), id);
        cepService.validarCep(dto.getCep());

        empresa.setCnpj(dto.getCnpj());
        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setCep(dto.getCep());
        empresa.setEstado(dto.getEstado());
        empresa.setCidade(dto.getCidade());

        empresa.getFornecedores().clear();
        vincularFornecedores(empresa, dto.getFornecedorIds());

        empresa = empresaRepository.save(empresa);
        return toDTO(empresa);
    }

    @Transactional
    public void excluir(Long id) {
        Empresa empresa = findOrFail(id);
        empresa.getFornecedores().clear();
        empresaRepository.delete(empresa);
    }

    private Empresa findOrFail(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id: " + id));
    }

    private void validarCnpjUnico(String cnpj, Long idAtual) {
        empresaRepository.findByCnpj(cnpj).ifPresent(existing -> {
            if (!existing.getId().equals(idAtual)) {
                throw new BusinessException("CNPJ já cadastrado");
            }
        });
    }

    private void vincularFornecedores(Empresa empresa, Set<Long> fornecedorIds) {
        if (fornecedorIds != null && !fornecedorIds.isEmpty()) {
            Set<Fornecedor> fornecedores = new HashSet<>(fornecedorRepository.findAllById(fornecedorIds));

            // Regra 3: Empresa do PR não pode ter fornecedor PF menor de idade
            if ("PR".equalsIgnoreCase(empresa.getEstado())) {
                for (Fornecedor f : fornecedores) {
                    if (f.getTipoPessoa() == Fornecedor.TipoPessoa.FISICA) {
                        if (f.getDataNascimento() == null) {
                            throw new BusinessException(
                                "Fornecedor pessoa física '" + f.getNome() + "' deve ter data de nascimento informada");
                        }
                        if (f.getDataNascimento().plusYears(18).isAfter(java.time.LocalDate.now())) {
                            throw new BusinessException(
                                "Empresa do Paraná não pode ter fornecedor pessoa física menor de idade: " + f.getNome());
                        }
                    }
                }
            }

            empresa.getFornecedores().addAll(fornecedores);
        }
    }

    private EmpresaResumoDTO toResumoDTO(Empresa empresa) {
        return EmpresaResumoDTO.builder()
                .id(empresa.getId())
                .cnpj(empresa.getCnpj())
                .nomeFantasia(empresa.getNomeFantasia())
                .cep(empresa.getCep())
                .estado(empresa.getEstado())
                .cidade(empresa.getCidade())
                .totalFornecedores(empresa.getFornecedores().size())
                .build();
    }

    private EmpresaDTO toDTO(Empresa empresa) {
        return EmpresaDTO.builder()
                .id(empresa.getId())
                .cnpj(empresa.getCnpj())
                .nomeFantasia(empresa.getNomeFantasia())
                .cep(empresa.getCep())
                .estado(empresa.getEstado())
                .cidade(empresa.getCidade())
                .fornecedorIds(empresa.getFornecedores().stream()
                        .map(Fornecedor::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    private Empresa toEntity(EmpresaDTO dto) {
        return Empresa.builder()
                .cnpj(dto.getCnpj())
                .nomeFantasia(dto.getNomeFantasia())
                .cep(dto.getCep())
                .estado(dto.getEstado())
                .cidade(dto.getCidade())
                .build();
    }
}
