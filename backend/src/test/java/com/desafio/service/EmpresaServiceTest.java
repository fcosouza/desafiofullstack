package com.desafio.service;

import com.desafio.dto.EmpresaDTO;
import com.desafio.dto.EmpresaResumoDTO;
import com.desafio.entity.Empresa;
import com.desafio.entity.Fornecedor;
import com.desafio.entity.Fornecedor.TipoPessoa;
import com.desafio.exception.BusinessException;
import com.desafio.exception.ResourceNotFoundException;
import com.desafio.repository.EmpresaRepository;
import com.desafio.repository.FornecedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private CepService cepService;

    @InjectMocks
    private EmpresaService empresaService;

    private EmpresaDTO dto;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        dto = EmpresaDTO.builder()
                .cnpj("12.345.678/0001-90")
                .nomeFantasia("Empresa Teste")
                .cep("01001-000")
                .estado("SP")
                .cidade("São Paulo")
                .build();

        empresa = Empresa.builder()
                .id(1L)
                .cnpj("12.345.678/0001-90")
                .nomeFantasia("Empresa Teste")
                .cep("01001-000")
                .estado("SP")
                .cidade("São Paulo")
                .build();
    }

    @Test
    void listar_deveRetornarListaDeEmpresas() {
        when(empresaRepository.findAll()).thenReturn(List.of(empresa));

        List<EmpresaResumoDTO> resultado = empresaService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Empresa Teste", resultado.get(0).getNomeFantasia());
        verify(empresaRepository).findAll();
    }

    @Test
    void buscarPorId_deveRetornarEmpresa() {
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        EmpresaDTO resultado = empresaService.buscarPorId(1L);

        assertEquals("Empresa Teste", resultado.getNomeFantasia());
        assertEquals("12.345.678/0001-90", resultado.getCnpj());
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrar() {
        when(empresaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> empresaService.buscarPorId(99L));
    }

    @Test
    void criar_deveSalvarEmpresa() {
        when(empresaRepository.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(cepService.validarCep(anyString())).thenReturn(new CepService.CepResponse());
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);

        EmpresaDTO resultado = empresaService.criar(dto);

        assertNotNull(resultado);
        assertEquals("Empresa Teste", resultado.getNomeFantasia());
        verify(empresaRepository).save(any(Empresa.class));
    }

    @Test
    void criar_deveLancarExcecaoParaCnpjDuplicado() {
        Empresa existente = Empresa.builder().id(2L).cnpj("12.345.678/0001-90").build();
        when(empresaRepository.findByCnpj("12.345.678/0001-90")).thenReturn(Optional.of(existente));

        assertThrows(BusinessException.class, () -> empresaService.criar(dto));
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void criar_empresaPR_deveLancarExcecaoParaFornecedorPFMenor() {
        dto.setEstado("PR");
        dto.setFornecedorIds(Set.of(1L));

        Fornecedor fornecedorMenor = Fornecedor.builder()
                .id(1L)
                .tipoPessoa(TipoPessoa.FISICA)
                .nome("Menor")
                .dataNascimento(LocalDate.now().minusYears(16))
                .build();

        when(empresaRepository.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(cepService.validarCep(anyString())).thenReturn(new CepService.CepResponse());
        when(fornecedorRepository.findAllById(anyCollection())).thenReturn(List.of(fornecedorMenor));

        assertThrows(BusinessException.class, () -> empresaService.criar(dto));
    }

    @Test
    void criar_empresaPR_devePermitirFornecedorPFMaiorDeIdade() {
        dto.setEstado("PR");
        dto.setFornecedorIds(Set.of(1L));

        Fornecedor fornecedorMaior = Fornecedor.builder()
                .id(1L)
                .tipoPessoa(TipoPessoa.FISICA)
                .nome("Maior")
                .dataNascimento(LocalDate.now().minusYears(25))
                .build();

        when(empresaRepository.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(cepService.validarCep(anyString())).thenReturn(new CepService.CepResponse());
        when(fornecedorRepository.findAllById(anyCollection())).thenReturn(List.of(fornecedorMaior));
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);

        EmpresaDTO resultado = empresaService.criar(dto);
        assertNotNull(resultado);
    }

    @Test
    void excluir_deveRemoverEmpresa() {
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        empresaService.excluir(1L);

        verify(empresaRepository).delete(empresa);
    }

    @Test
    void excluir_deveLancarExcecaoQuandoNaoEncontrar() {
        when(empresaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> empresaService.excluir(99L));
    }
}
