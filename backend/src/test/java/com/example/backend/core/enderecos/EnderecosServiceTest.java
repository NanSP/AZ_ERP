package com.example.backend.core.enderecos;

import com.example.backend.core.empresas.EmpresasRepository;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnderecosServiceTest {

    @Mock
    private EnderecosRepository repository;
    @Mock
    private EmpresasRepository empresasRepository;
    @Mock
    private ParceirosRepository parceirosRepository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;

    @InjectMocks
    private EnderecosService service;

    @Test
    void deveCriarEnderecoComDefaultsENormalizacao() {
        EnderecosRequestDTO request = new EnderecosRequestDTO(
                " parceiro ",
                1,
                null,
                " Rua A ",
                " 123 ",
                " sala 2 ",
                " centro ",
                " salvador ",
                " ba ",
                " 40.000-000 ",
                null,
                true
        );

        when(parceirosRepository.existsById(1)).thenReturn(true);
        when(repository.save(any(Enderecos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Enderecos saved = service.criar(request);

        assertEquals("parceiro", saved.getEntidadeTipo());
        assertEquals("comercial", saved.getTipoEndereco());
        assertEquals("BA", saved.getUf());
        assertEquals("40000000", saved.getCep());
        assertEquals("BRASIL", saved.getPais());

        ArgumentCaptor<Enderecos> captor = ArgumentCaptor.forClass(Enderecos.class);
        verify(repository).save(captor.capture());
        verify(repository).clearPrincipalByEntidade("parceiro", 1, null);
        assertEquals("Rua A", captor.getValue().getLogradouro());
    }

    @Test
    void deveBloquearEnderecoComUfInvalida() {
        EnderecosRequestDTO request = new EnderecosRequestDTO(
                "empresa",
                1,
                "comercial",
                null,
                null,
                null,
                null,
                null,
                "bahia",
                null,
                null,
                false
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("UF deve conter 2 letras", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeEnderecoPrincipal() {
        Enderecos entity = new Enderecos();
        entity.setId(10);
        entity.setPrincipal(true);

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir endereco principal", exception.getMessage());
        verify(repository, never()).delete(any(Enderecos.class));
    }
}
