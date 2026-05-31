package com.example.backend.core.contatos;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContatosServiceTest {

    @Mock
    private ContatosRepository repository;
    @Mock
    private EmpresasRepository empresasRepository;
    @Mock
    private ParceirosRepository parceirosRepository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;

    @InjectMocks
    private ContatosService service;

    @Test
    void deveCriarContatoPrincipalComNormalizacaoERebaixarDemais() {
        ContatosRequestDTO request = new ContatosRequestDTO(
                " EMPRESA ",
                1,
                " EMAIL ",
                " contato@empresa.com ",
                true,
                " financeiro "
        );

        when(empresasRepository.existsById(1)).thenReturn(true);
        when(repository.save(any(Contatos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contatos saved = service.criar(request);

        assertEquals("empresa", saved.getEntidadeTipo());
        assertEquals("email", saved.getTipoContato());
        assertEquals("contato@empresa.com", saved.getValor());
        assertEquals("financeiro", saved.getObservacao());

        ArgumentCaptor<Contatos> captor = ArgumentCaptor.forClass(Contatos.class);
        verify(repository).save(captor.capture());
        verify(repository).clearPrincipalByEntidadeAndTipoContato("empresa", 1, "email", null);
        assertEquals(true, captor.getValue().getPrincipal());
    }

    @Test
    void deveBloquearContatoDeTelefoneInvalido() {
        ContatosRequestDTO request = new ContatosRequestDTO(
                "empresa",
                1,
                "telefone",
                "12345",
                false,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Telefone invalido", exception.getMessage());
    }

    @Test
    void deveBloquearAlteracaoDeVinculoDoContato() {
        Contatos entity = new Contatos();
        entity.setId(10);
        entity.setEntidadeTipo("empresa");
        entity.setEntidadeId(1);

        ContatosRequestDTO request = new ContatosRequestDTO(
                "parceiro",
                2,
                "email",
                "contato@empresa.com",
                false,
                null
        );

        when(parceirosRepository.existsById(2)).thenReturn(true);
        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(10, request));

        assertEquals("Nao e permitido alterar o vinculo do contato com a entidade", exception.getMessage());
    }
}
