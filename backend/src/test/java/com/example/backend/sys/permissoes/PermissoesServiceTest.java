package com.example.backend.sys.permissoes;

import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.perfilPermissao.PerfilPermissaoRepository;
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
class PermissoesServiceTest {

    @Mock
    private PermissoesRepository repository;
    @Mock
    private PerfilPermissaoRepository perfilPermissaoRepository;

    @InjectMocks
    private PermissoesService service;

    @Test
    void deveCriarPermissaoComNormalizacao() {
        PermissoesRequestDTO request = new PermissoesRequestDTO(
                "USUARIOS_LISTAR",
                " Permite listar usuarios ",
                " SYS ",
                " USUARIOS ",
                " READ "
        );

        when(repository.existsByNome("USUARIOS_LISTAR")).thenReturn(false);
        when(repository.save(any(Permissoes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Permissoes saved = service.criar(request);

        assertEquals("USUARIOS_LISTAR", saved.getNome());
        assertEquals("Permite listar usuarios", saved.getDescricao());
        assertEquals("sys", saved.getModulo());
        assertEquals("usuarios", saved.getRecurso());
        assertEquals("read", saved.getAcao());

        ArgumentCaptor<Permissoes> captor = ArgumentCaptor.forClass(Permissoes.class);
        verify(repository).save(captor.capture());
        assertEquals("sys", captor.getValue().getModulo());
    }

    @Test
    void deveBloquearAlteracaoDeModuloQuandoPermissaoJaEstaVinculada() {
        Permissoes entity = new Permissoes();
        entity.setId(10);
        entity.setNome("USUARIOS_LISTAR");
        entity.setDescricao("Permite listar usuarios");
        entity.setModulo("sys");
        entity.setRecurso("usuarios");
        entity.setAcao("read");

        PermissoesRequestDTO request = new PermissoesRequestDTO(
                "USUARIOS_LISTAR",
                "Permite listar usuarios",
                "core",
                "usuarios",
                "read"
        );

        when(repository.existsByNomeAndIdNot("USUARIOS_LISTAR", 10)).thenReturn(false);
        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(perfilPermissaoRepository.existsByPermissaoId(10)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(10, request));

        assertEquals("Nao e permitido alterar o modulo de permissao ja vinculada", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDePermissaoComVinculosAtivos() {
        Permissoes entity = new Permissoes();
        entity.setId(10);

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(perfilPermissaoRepository.existsByPermissaoId(10)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir permissao com vinculos ativos", exception.getMessage());
        verify(repository, never()).delete(any(Permissoes.class));
    }
}
