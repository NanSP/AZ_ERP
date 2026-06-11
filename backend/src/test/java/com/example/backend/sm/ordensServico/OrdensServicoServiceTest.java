package com.example.backend.sm.ordensServico;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sm.atendimentos.AtendimentosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdensServicoServiceTest {

    @Mock
    private OrdensServicoRepository repository;
    @Mock
    private ParceirosRepository parceirosRepository;
    @Mock
    private ProdutosRepository produtosRepository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;
    @Mock
    private AtendimentosRepository atendimentosRepository;

    @InjectMocks
    private OrdensServicoService service;

    @Test
    void deveCriarOsComDefaultsENormalizacao() {
        LocalDate dataAgendamento = LocalDate.now().plusDays(1);

        OrdensServicoRequestDTO request = new OrdensServicoRequestDTO(
                " OS-001 ",
                1,
                2,
                " Corretiva ",
                " Falha no equipamento ",
                null,
                null,
                dataAgendamento,
                null,
                null,
                3,
                null
        );

        when(parceirosRepository.findById(1)).thenReturn(Optional.of(criarClienteAtivo()));
        when(produtosRepository.findById(2)).thenReturn(Optional.of(criarProdutoValido("produto")));
        when(colaboradoresRepository.findById(3)).thenReturn(Optional.of(criarTecnico()));
        when(repository.save(any(OrdensServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdensServico saved = service.criar(request);

        assertEquals("OS-001", saved.getNumeroOs());
        assertEquals("normal", saved.getPrioridade());
        assertEquals("aberta", saved.getStatus());

        ArgumentCaptor<OrdensServico> captor = ArgumentCaptor.forClass(OrdensServico.class);
        verify(repository).save(captor.capture());
        assertEquals("Corretiva", captor.getValue().getTipoServico());
    }

    @Test
    void deveBloquearOsConcluidaSemDataFim() {
        OrdensServicoRequestDTO request = new OrdensServicoRequestDTO(
                "OS-001",
                1,
                2,
                "Corretiva",
                "Falha no equipamento",
                "alta",
                LocalDateTime.of(2026, 6, 1, 8, 0),
                null,
                null,
                null,
                3,
                "concluida"
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data fim e obrigatoria quando a ordem de servico estiver concluida", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeOsComAtendimentos() {
        OrdensServico entity = new OrdensServico();
        entity.setId(10);

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(atendimentosRepository.existsByOsId(10)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir ordem de servico que possui atendimentos", exception.getMessage());
        verify(repository, never()).delete(any(OrdensServico.class));
    }

    private Parceiros criarClienteAtivo() {
        Parceiros parceiro = new Parceiros();
        parceiro.setId(1);
        parceiro.setSituacao("ativo");
        parceiro.setTipoParceiro("cliente");
        return parceiro;
    }

    private Produtos criarProdutoValido(String tipoItem) {
        Produtos produto = new Produtos();
        produto.setId(2);
        produto.setSituacao("ativo");
        produto.setTipoItem(tipoItem);
        return produto;
    }

    private Colaboradores criarTecnico() {
        Colaboradores colaborador = new Colaboradores();
        colaborador.setId(3);
        colaborador.setNome("Tecnico");
        return colaborador;
    }
}
