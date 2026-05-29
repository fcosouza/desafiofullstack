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
class FornecedorServiceTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private CepService cepService;

    @InjectMocks
    private FornecedorService fornecedorService;

    private FornecedorDTO dtoPJ;
    private FornecedorDTO dtoPF;
    private Fornecedor fornecedorPJ;

    @BeforeEach
    void setUp() {
        dtoPJ = FornecedorDTO.builder()
                .tipoPessoa(TipoPessoa.JURIDICA)
                .nome("Fornecedor PJ")
                .cpfCnpj("98.765.432/0001-10")
                .email("pj@email.com")
                .cep("01001-000")
                .build();

        dtoPF = FornecedorDTO.builder()
                .tipoPessoa(TipoPessoa.FISICA)
                .nome("Fornecedor PF")
                .cpfCnpj("123.456.789-00")
                .email("pf@email.com")
                .cep("01001-000")
                .rg("12.345.678-9")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .build();

        fornecedorPJ = Fornecedor.builder()
                .id(1L)
                .tipoPessoa(TipoPessoa.JURIDICA)
                .nome("Fornecedor PJ")
                .cpfCnpj("98.765.432/0001-10")
                .email("pj@email.com")
                .cep("01001-000")
                .build();
    }

    @Test
    void listar_deveRetornarTodosFornecedores() {
        when(fornecedorRepository.findAll()).thenReturn(List.of(fornecedorPJ));

        List<FornecedorResumoDTO> resultado = fornecedorService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Fornecedor PJ", resultado.get(0).getNome());
    }

    @Test
    void buscarPorId_deveRetornarFornecedor() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedorPJ));

        FornecedorDTO resultado = fornecedorService.buscarPorId(1L);

        assertEquals("Fornecedor PJ", resultado.getNome());
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrar() {
        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fornecedorService.buscarPorId(99L));
    }

    @Test
    void criar_deveSalvarFornecedorPJ() {
        when(fornecedorRepository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
        when(cepService.validarCep(anyString())).thenReturn(new CepService.CepResponse());
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedorPJ);

        FornecedorDTO resultado = fornecedorService.criar(dtoPJ);

        assertNotNull(resultado);
        assertEquals("Fornecedor PJ", resultado.getNome());
        verify(fornecedorRepository).save(any(Fornecedor.class));
    }

    @Test
    void criar_deveSalvarFornecedorPFComRGEDataNascimento() {
        Fornecedor fornecedorPF = Fornecedor.builder()
                .id(2L)
                .tipoPessoa(TipoPessoa.FISICA)
                .nome("Fornecedor PF")
                .cpfCnpj("123.456.789-00")
                .email("pf@email.com")
                .cep("01001-000")
                .rg("12.345.678-9")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .build();

        when(fornecedorRepository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
        when(cepService.validarCep(anyString())).thenReturn(new CepService.CepResponse());
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedorPF);

        FornecedorDTO resultado = fornecedorService.criar(dtoPF);

        assertNotNull(resultado);
        assertEquals("12.345.678-9", resultado.getRg());
        assertNotNull(resultado.getDataNascimento());
    }

    @Test
    void criar_deveLancarExcecaoParaCpfCnpjDuplicado() {
        Fornecedor existente = Fornecedor.builder().id(2L).cpfCnpj("98.765.432/0001-10").build();
        when(fornecedorRepository.findByCpfCnpj("98.765.432/0001-10")).thenReturn(Optional.of(existente));

        assertThrows(BusinessException.class, () -> fornecedorService.criar(dtoPJ));
        verify(fornecedorRepository, never()).save(any());
    }

    @Test
    void criar_PF_semRG_deveLancarExcecao() {
        dtoPF.setRg(null);

        when(fornecedorRepository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> fornecedorService.criar(dtoPF));
    }

    @Test
    void criar_PF_semDataNascimento_deveLancarExcecao() {
        dtoPF.setDataNascimento(null);

        when(fornecedorRepository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> fornecedorService.criar(dtoPF));
    }

    @Test
    void criar_PFMenor_empresaPR_deveLancarExcecao() {
        dtoPF.setDataNascimento(LocalDate.now().minusYears(16));
        dtoPF.setEmpresaIds(Set.of(1L));

        Fornecedor fornecedorPF = Fornecedor.builder()
                .id(2L)
                .tipoPessoa(TipoPessoa.FISICA)
                .nome("Menor")
                .cpfCnpj("123.456.789-00")
                .dataNascimento(LocalDate.now().minusYears(16))
                .build();

        Empresa empresaPR = Empresa.builder()
                .id(1L)
                .nomeFantasia("Empresa PR")
                .estado("PR")
                .build();

        when(fornecedorRepository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
        when(cepService.validarCep(anyString())).thenReturn(new CepService.CepResponse());
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedorPF);
        when(empresaRepository.findAllById(anyCollection())).thenReturn(List.of(empresaPR));

        assertThrows(BusinessException.class, () -> fornecedorService.criar(dtoPF));
    }

    @Test
    void filtrar_deveRetornarFornecedoresFiltrados() {
        when(fornecedorRepository.filtrar("Fornecedor", null)).thenReturn(List.of(fornecedorPJ));

        List<FornecedorResumoDTO> resultado = fornecedorService.filtrar("Fornecedor", null);

        assertEquals(1, resultado.size());
        verify(fornecedorRepository).filtrar("Fornecedor", null);
    }

    @Test
    void excluir_deveRemoverFornecedor() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedorPJ));

        fornecedorService.excluir(1L);

        verify(fornecedorRepository).delete(fornecedorPJ);
    }
}
