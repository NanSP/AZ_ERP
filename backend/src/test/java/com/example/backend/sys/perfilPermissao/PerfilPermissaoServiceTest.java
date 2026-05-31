package com.example.backend.sys.perfilPermissao;

import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.perfis.Perfis;
import com.example.backend.sys.perfis.PerfisRepository;
import com.example.backend.sys.permissoes.Permissoes;
import com.example.backend.sys.permissoes.PermissoesRepository;
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
class PerfilPermissaoServiceTest {

    @Mock
    private PerfilPermissaoRepository repository;

    @Mock
    private PerfisRepository perfisRepository;

    @Mock
    private PermissoesRepository permissoesRepository;

    @InjectMocks
    private PerfilPermissaoService service;

    @Test
    void deveCriarVinculoEntrePerfilEPermissao() {
        PerfilPermissaoRequestDTO request = new PerfilPermissaoRequestDTO(1, 2);

        when(perfisRepository.findById(1)).thenReturn(Optional.of(criarPerfil()));
        when(permissoesRepository.findById(2)).thenReturn(Optional.of(criarPermissao()));
        when(repository.save(any(PerfilPermissao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PerfilPermissao saved = service.criar(request);

        assertEquals(1, saved.getPerfil().getId());
        assertEquals(2, saved.getPermissao().getId());

        ArgumentCaptor<PerfilPermissao> captor = ArgumentCaptor.forClass(PerfilPermissao.class);
        verify(repository).save(captor.capture());
        assertEquals(2, captor.getValue().getPermissao().getId());
    }

    @Test
    void deveBloquearAlteracaoDoVinculoAposAtribuicao() {
        PerfilPermissao entity = new PerfilPermissao();
        entity.setId(10);
        entity.setPerfil(criarPerfil());
        entity.setPermissao(criarPermissao());

        PerfilPermissaoRequestDTO request = new PerfilPermissaoRequestDTO(1, 3);

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(10, request));

        assertEquals("Nao e permitido alterar o vinculo perfil-permissao apos a atribuicao", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDaUltimaPermissaoDoPerfil() {
        PerfilPermissao entity = new PerfilPermissao();
        entity.setId(10);
        entity.setPerfil(criarPerfil());
        entity.setPermissao(criarPermissao());

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(repository.existsByPerfilIdAndIdNot(1, 10)).thenReturn(false);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido remover a ultima permissao do perfil", exception.getMessage());
        verify(repository, never()).delete(any(PerfilPermissao.class));
    }

    private Perfis criarPerfil() {
        Perfis perfil = new Perfis();
        perfil.setId(1);
        perfil.setNome("Administrador");
        return perfil;
    }

    private Permissoes criarPermissao() {
        Permissoes permissao = new Permissoes();
        permissao.setId(2);
        permissao.setNome("usuarios.read");
        return permissao;
    }
}
