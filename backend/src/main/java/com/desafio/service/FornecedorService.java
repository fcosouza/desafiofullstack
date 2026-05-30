package com.desafio.service;

import com.desafio.dto.FornecedorDTO;
import com.desafio.dto.FornecedorResumoDTO;
import com.desafio.entity.Empresa;
import com.desafio.entity.Fornecedor;
import com.desafio.entity.Fornecedor.TipoPessoa;
import com.desafio.exception.BusinessException;
import com.desafio.exception.ResourceNotFoundException;
import com.desafio.repository.EmpresaRepository;
import com.desafio.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final EmpresaRepository empresaRepository;
    private final CepService cepService;

    @Transactional(readOnly = true)
    public List<FornecedorResumoDTO> listar() {
        return fornecedorRepository.findAll().stream()
                .map(this::toResumoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FornecedorDTO buscarPorId(Long id) {
        Fornecedor fornecedor = findOrFail(id);
        return toDTO(fornecedor);
    }

    @Transactional(readOnly = true)
    public List<FornecedorResumoDTO> filtrar(String nome, String cpfCnpj) {
        return fornecedorRepository.filtrar(
                nome != null && nome.isBlank() ? null : nome,
                cpfCnpj != null && cpfCnpj.isBlank() ? null : cpfCnpj
        ).stream().map(this::toResumoDTO).collect(Collectors.toList());
    }

    @Transactional
    public FornecedorDTO criar(FornecedorDTO dto) {
        validarCpfCnpjUnico(dto.getCpfCnpj(), null);
        validarPessoaFisica(dto);
        cepService.validarCep(dto.getCep());

        Fornecedor fornecedor = toEntity(dto);
        fornecedor = fornecedorRepository.save(fornecedor);

        vincularEmpresas(fornecedor, dto.getEmpresaIds());

        return toDTO(fornecedor);
    }

    @Transactional
    public FornecedorDTO atualizar(Long id, FornecedorDTO dto) {
        Fornecedor fornecedor = findOrFail(id);
        validarCpfCnpjUnico(dto.getCpfCnpj(), id);
        validarPessoaFisica(dto);
        cepService.validarCep(dto.getCep());

        fornecedor.setTipoPessoa(dto.getTipoPessoa());
        fornecedor.setNome(dto.getNome());
        fornecedor.setCpfCnpj(dto.getCpfCnpj());
        fornecedor.setEmail(dto.getEmail());
        fornecedor.setCep(dto.getCep());
        fornecedor.setRg(dto.getRg());
        fornecedor.setDataNascimento(dto.getDataNascimento());

        // Remove from all current empresas
        for (Empresa empresa : new HashSet<>(fornecedor.getEmpresas())) {
            empresa.getFornecedores().remove(fornecedor);
        }

        fornecedor = fornecedorRepository.save(fornecedor);
        vincularEmpresas(fornecedor, dto.getEmpresaIds());

        return toDTO(fornecedor);
    }

    @Transactional
    public void excluir(Long id) {
        Fornecedor fornecedor = findOrFail(id);
        for (Empresa empresa : new HashSet<>(fornecedor.getEmpresas())) {
            empresa.getFornecedores().remove(fornecedor);
        }
        fornecedorRepository.delete(fornecedor);
    }

    private Fornecedor findOrFail(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com id: " + id));
    }

    private void validarCpfCnpjUnico(String cpfCnpj, Long idAtual) {
        fornecedorRepository.findByCpfCnpj(cpfCnpj).ifPresent(existing -> {
            if (!existing.getId().equals(idAtual)) {
                throw new BusinessException("CPF/CNPJ já cadastrado");
            }
        });
    }

    private void validarPessoaFisica(FornecedorDTO dto) {
        if (dto.getTipoPessoa() == TipoPessoa.FISICA) {
            if (dto.getRg() == null || dto.getRg().isBlank()) {
                throw new BusinessException("RG é obrigatório para pessoa física");
            }
            if (dto.getDataNascimento() == null) {
                throw new BusinessException("Data de nascimento é obrigatória para pessoa física");
            }
            if (dto.getDataNascimento().isAfter(LocalDate.now())) {
                throw new BusinessException("Data de nascimento não pode ser futura");
            }
        }
    }

    private void vincularEmpresas(Fornecedor fornecedor, Set<Long> empresaIds) {
        if (empresaIds != null && !empresaIds.isEmpty()) {
            List<Empresa> empresas = empresaRepository.findAllById(empresaIds);
            for (Empresa empresa : empresas) {
                // Regra 3: Empresa do PR não pode ter fornecedor PF menor de idade
                if ("PR".equalsIgnoreCase(empresa.getEstado())
                        && fornecedor.getTipoPessoa() == TipoPessoa.FISICA) {
                    if (fornecedor.getDataNascimento() != null
                            && fornecedor.getDataNascimento().plusYears(18).isAfter(LocalDate.now())) {
                        throw new BusinessException(
                            "Empresa do Paraná '" + empresa.getNomeFantasia() + "' não pode ter fornecedor pessoa física menor de idade");
                    }
                }
                empresa.getFornecedores().add(fornecedor);
                empresaRepository.save(empresa);
            }
        }
    }

    private FornecedorResumoDTO toResumoDTO(Fornecedor f) {
        return FornecedorResumoDTO.builder()
                .id(f.getId())
                .tipoPessoa(f.getTipoPessoa())
                .nome(f.getNome())
                .cpfCnpj(f.getCpfCnpj())
                .email(f.getEmail())
                .cep(f.getCep())
                .rg(f.getRg())
                .dataNascimento(f.getDataNascimento())
                .totalEmpresas(f.getEmpresas().size())
                .build();
    }

    private FornecedorDTO toDTO(Fornecedor f) {
        return FornecedorDTO.builder()
                .id(f.getId())
                .tipoPessoa(f.getTipoPessoa())
                .nome(f.getNome())
                .cpfCnpj(f.getCpfCnpj())
                .email(f.getEmail())
                .cep(f.getCep())
                .rg(f.getRg())
                .dataNascimento(f.getDataNascimento())
                .empresaIds(f.getEmpresas().stream()
                        .map(Empresa::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    private Fornecedor toEntity(FornecedorDTO dto) {
        return Fornecedor.builder()
                .tipoPessoa(dto.getTipoPessoa())
                .nome(dto.getNome())
                .cpfCnpj(dto.getCpfCnpj())
                .email(dto.getEmail())
                .cep(dto.getCep())
                .rg(dto.getRg())
                .dataNascimento(dto.getDataNascimento())
                .build();
    }
}
