package com.example.backend.grc.riscos;

import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
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
class RiscosServiceTest {

    @Mock
    private RiscosRepository repository;
    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private RiscosService service;

    @Test
    void deveCriarRiscoComNivelCalculado() {
        RiscosRequestDTO request = new RiscosRequestDTO(
                " RSK-01 ",
                " Risco operacional ",
                " descricao ",
                "processo",
                4,
                4,
                1,
                " mitigar "
        );

        when(usuariosRepository.findById(1)).thenReturn(Optional.of(criarUsuario()));
        when(repository.save(any(Riscos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Riscos saved = service.criar(request);

        assertEquals("RSK-01", saved.getCodigo());
        assertEquals("alto", saved.getNivelRisco());
        assertEquals("mitigar", saved.getPlanoMitigacao());

        ArgumentCaptor<Riscos> captor = ArgumentCaptor.forClass(Riscos.class);
        verify(repository).save(captor.capture());
        assertEquals("Risco operacional", captor.getValue().getTitulo());
    }

    @Test
    void deveBloquearRiscoAltoSemResponsavel() {
        RiscosRequestDTO request = new RiscosRequestDTO(
                null,
                "Risco operacional",
                null,
                null,
                5,
                4,
                null,
                "mitigar"
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Risco alto deve possuir responsavel", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeRiscoJaAvaliado() {
        Riscos entity = new Riscos();
        entity.setId(10);
        entity.setNivelRisco("medio");
        entity.setProbabilidade(3);
        entity.setImpacto(3);

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir risco ja avaliado ou com plano de mitigacao", exception.getMessage());
        verify(repository, never()).delete(any(Riscos.class));
    }

    private Usuarios criarUsuario() {
        Usuarios usuario = new Usuarios();
        usuario.setId(1);
        usuario.setNome("Responsavel");
        return usuario;
    }
}
