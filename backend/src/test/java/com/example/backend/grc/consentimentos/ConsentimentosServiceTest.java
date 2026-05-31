package com.example.backend.grc.consentimentos;

import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsentimentosServiceTest {

    @Mock
    private ConsentimentosRepository repository;

    @InjectMocks
    private ConsentimentosService service;

    @Test
    void deveCriarConsentimentoComDataAutomatica() throws Exception {
        ConsentimentosRequestDTO request = new ConsentimentosRequestDTO(
                1,
                " CLIENTE ",
                " marketing ",
                null,
                null,
                InetAddress.getByName("127.0.0.1"),
                " browser "
        );

        when(repository.save(any(Consentimentos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Consentimentos saved = service.criar(request);

        assertEquals("cliente", saved.getTipoTitular());
        assertEquals("marketing", saved.getFinalidade());
        assertNotNull(saved.getDataConsentimento());

        ArgumentCaptor<Consentimentos> captor = ArgumentCaptor.forClass(Consentimentos.class);
        verify(repository).save(captor.capture());
        assertEquals("browser", captor.getValue().getUserAgent());
    }

    @Test
    void deveBloquearConsentimentoDuplicadoAtivo() throws Exception {
        ConsentimentosRequestDTO request = new ConsentimentosRequestDTO(
                1,
                "cliente",
                "marketing",
                LocalDateTime.now(),
                null,
                InetAddress.getByName("127.0.0.1"),
                null
        );

        when(repository.existsByTitularAndTipoTitularAndFinalidadeAndDataRevogacaoIsNull(1, "cliente", "marketing"))
                .thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Ja existe consentimento ativo para este titular e finalidade", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeConsentimentoRevogado() {
        Consentimentos entity = new Consentimentos();
        entity.setId(10);
        entity.setDataRevogacao(LocalDateTime.now());

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir consentimento que ja possui revogacao registrada", exception.getMessage());
        verify(repository, never()).delete(any(Consentimentos.class));
    }
}
