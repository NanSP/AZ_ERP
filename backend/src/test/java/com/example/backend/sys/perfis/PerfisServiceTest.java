package com.example.backend.sys.perfis;

import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.perfilPermissao.PerfilPermissaoRepository;
import com.example.backend.sys.usuarioPerfil.UsuarioPerfilRepository;
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
class PerfisServiceTest {

    @Mock
    private PerfisRepository repository;

    @Mock
    private UsuarioPerfilRepository usuarioPerfilRepository;

    @Mock
    private PerfilPermissaoRepository perfilPermissaoRepository;

    @InjectMocks
    private PerfisService service;

    @Test
    void deveCriarPerfilComNivelPadrao() {
        PerfisRequestDTO request = new PerfisRequestDTO(
                " Administrador ",
                " Perfil principal ",
                null
        );

        when(repository.save(any(Perfis.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Perfis saved = service.criar(request);

        assertEquals("Administrador", saved.getNome());
        assertEquals("Perfil principal", saved.getDescricao());
        assertEquals(1, saved.getNivelAcesso());

        ArgumentCaptor<Perfis> captor = ArgumentCaptor.forClass(Perfis.class);
        verify(repository).save(captor.capture());
        assertEquals("Administrador", captor.getValue().getNome());
    }

    @Test
    void deveBloquearAlteracaoDeNomeQuandoPerfilEstiverVinculado() {
        Perfis entity = criarPerfil();

        PerfisRequestDTO request = new PerfisRequestDTO(
                "Novo Nome",
                "Perfil principal",
                1
        );

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(usuarioPerfilRepository.existsByPerfilId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(1, request));

        assertEquals("Nao e permitido alterar o nome de perfil ja vinculado a usuarios", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoQuandoPerfilPossuirVinculosAtivos() {
        Perfis entity = criarPerfil();

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(perfilPermissaoRepository.existsByPerfilId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(1));

        assertEquals("Nao e permitido excluir perfil com vinculos ativos", exception.getMessage());
        verify(repository, never()).delete(any(Perfis.class));
    }

    private Perfis criarPerfil() {
        Perfis perfil = new Perfis();
        perfil.setId(1);
        perfil.setNome("Administrador");
        perfil.setDescricao("Perfil principal");
        perfil.setNivelAcesso(1);
        return perfil;
    }
}
