package com.example.backend.sd.oportunidades;

import com.example.backend.sd.clientes.Clientes;
import com.example.backend.sd.clientes.ClientesRepository;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OportunidadesServiceTest {

    @Mock
    private OportunidadesRepository repository;
    @Mock
    private ClientesRepository clientesRepository;
    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private OportunidadesService service;

    @Test
    void deveCriarOportunidadeComDefaults() {
        OportunidadesRequestDTO request = new OportunidadesRequestDTO(
                1,
                " Nova venda ",
                " oportunidade importante ",
                new BigDecimal("2500.00"),
                null,
                null,
                null,
                null,
                2
        );

        when(clientesRepository.findById(1)).thenReturn(Optional.of(criarCliente()));
        when(usuariosRepository.findById(2)).thenReturn(Optional.of(criarResponsavel()));
        when(repository.save(any(Oportunidades.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Oportunidades saved = service.criar(request);

        assertEquals("Nova venda", saved.getTitulo());
        assertEquals(50, saved.getProbabilidade());
        assertEquals("prospeccao", saved.getEstagio());

        ArgumentCaptor<Oportunidades> captor = ArgumentCaptor.forClass(Oportunidades.class);
        verify(repository).save(captor.capture());
        assertEquals("oportunidade importante", captor.getValue().getDescricao());
    }

    @Test
    void deveBloquearOportunidadePerdidaSemMotivo() {
        OportunidadesRequestDTO request = new OportunidadesRequestDTO(
                1,
                "Nova venda",
                null,
                new BigDecimal("2500.00"),
                70,
                "fechado_perdido",
                LocalDate.of(2026, 6, 10),
                null,
                2
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Motivo da perda e obrigatorio quando a oportunidade estiver perdida", exception.getMessage());
    }

    @Test
    void deveBloquearEstagioQueExigeDataPrevistaSemFechamento() {
        OportunidadesRequestDTO request = new OportunidadesRequestDTO(
                1,
                "Nova venda",
                null,
                new BigDecimal("2500.00"),
                70,
                "negociacao",
                null,
                null,
                2
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data prevista de fechamento e obrigatoria para o estagio informado", exception.getMessage());
    }

    private Clientes criarCliente() {
        Clientes cliente = new Clientes();
        cliente.setId(1);
        return cliente;
    }

    private Usuarios criarResponsavel() {
        Usuarios usuario = new Usuarios();
        usuario.setId(2);
        usuario.setNome("Responsavel");
        return usuario;
    }
}
