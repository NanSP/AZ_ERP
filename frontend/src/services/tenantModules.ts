export type TenantResource = {
  schema: string;
  entity: string;
  label: string;
  description: string;
};

export type TenantModule = {
  sigla: string;
  title: string;
  description: string;
  resources: TenantResource[];
};

export const tenantModules: TenantModule[] = [
  {
    sigla: "SYS",
    title: "Sistema",
    description: "Usuarios, perfis, permissoes e estrutura de acesso.",
    resources: [
      {
        schema: "sys",
        entity: "usuarios",
        label: "Usuarios",
        description: "Gestao de usuarios do tenant.",
      },
      {
        schema: "sys",
        entity: "perfis",
        label: "Perfis",
        description: "Perfis de acesso e responsabilidades.",
      },
      {
        schema: "sys",
        entity: "permissoes",
        label: "Permissoes",
        description: "Permissoes disponiveis no sistema.",
      },
      {
        schema: "sys",
        entity: "usuarioPerfil",
        label: "Usuario x Perfil",
        description: "Vinculos entre usuarios e perfis.",
      },
      {
        schema: "sys",
        entity: "perfilPermissao",
        label: "Perfil x Permissao",
        description: "Permissoes atribuidas aos perfis.",
      },
    ],
  },
  {
    sigla: "CORE",
    title: "Cadastros Centrais",
    description: "Base de dados corporativa do ERP.",
    resources: [
      {
        schema: "core",
        entity: "empresas",
        label: "Empresas",
        description: "Cadastro das empresas da operacao.",
      },
      {
        schema: "core",
        entity: "parceiros",
        label: "Parceiros",
        description: "Clientes, fornecedores e parceiros de negocio.",
      },
      {
        schema: "core",
        entity: "produtos",
        label: "Produtos",
        description: "Catalogo de produtos e itens.",
      },
      {
        schema: "core",
        entity: "enderecos",
        label: "Enderecos",
        description: "Enderecos vinculados aos cadastros.",
      },
      {
        schema: "core",
        entity: "contatos",
        label: "Contatos",
        description: "Contatos corporativos e operacionais.",
      },
    ],
  },
  {
    sigla: "FI",
    title: "Financeiro",
    description: "Fluxo financeiro, tesouraria e contabilizacao.",
    resources: [
      {
        schema: "fi",
        entity: "contasPagar",
        label: "Contas a Pagar",
        description: "Obrigacoes financeiras a pagar.",
      },
      {
        schema: "fi",
        entity: "contasReceber",
        label: "Contas a Receber",
        description: "Recebimentos e titulos de clientes.",
      },
      {
        schema: "fi",
        entity: "planoContas",
        label: "Plano de Contas",
        description: "Estrutura contabil e classificacoes.",
      },
      {
        schema: "fi",
        entity: "centrosCusto",
        label: "Centros de Custo",
        description: "Centros de custo e rateios.",
      },
      {
        schema: "fi",
        entity: "fluxoCaixa",
        label: "Fluxo de Caixa",
        description: "Movimentacoes e previsoes de caixa.",
      },
      {
        schema: "fi",
        entity: "movimentacoesBancarias",
        label: "Movimentacoes Bancarias",
        description: "Controle bancario e conciliacao.",
      },
    ],
  },
  {
    sigla: "MM",
    title: "Materiais e Suprimentos",
    description: "Compras, estoque e abastecimento.",
    resources: [
      {
        schema: "mm",
        entity: "compras",
        label: "Compras",
        description: "Processos de compra e aquisicao.",
      },
      {
        schema: "mm",
        entity: "compraItens",
        label: "Itens de Compra",
        description: "Itens vinculados as compras.",
      },
      {
        schema: "mm",
        entity: "materiais",
        label: "Materiais",
        description: "Cadastro de materiais e insumos.",
      },
      {
        schema: "mm",
        entity: "estoques",
        label: "Estoques",
        description: "Posicoes e controle de estoque.",
      },
      {
        schema: "mm",
        entity: "movimentacoes",
        label: "Movimentacoes",
        description: "Entradas, saidas e transferencias.",
      },
      {
        schema: "mm",
        entity: "inventarios",
        label: "Inventarios",
        description: "Inventarios fisicos e ajustes.",
      },
    ],
  },
  {
    sigla: "RH",
    title: "Recursos Humanos",
    description: "Pessoas, beneficios e administracao da forca de trabalho.",
    resources: [
      {
        schema: "rh",
        entity: "colaboradores",
        label: "Colaboradores",
        description: "Cadastro e gestao de colaboradores.",
      },
      {
        schema: "rh",
        entity: "dependentes",
        label: "Dependentes",
        description: "Dependentes vinculados aos colaboradores.",
      },
      {
        schema: "rh",
        entity: "beneficios",
        label: "Beneficios",
        description: "Beneficios corporativos e regras.",
      },
      {
        schema: "rh",
        entity: "controleDePonto",
        label: "Controle de Ponto",
        description: "Jornadas, marcacoes e apontamentos.",
      },
      {
        schema: "rh",
        entity: "folhaDePagamento",
        label: "Folha de Pagamento",
        description: "Informacoes de folha e processamento.",
      },
    ],
  },
  {
    sigla: "PS",
    title: "Projetos e Servicos",
    description: "Planejamento, execucao e alocacao.",
    resources: [
      {
        schema: "ps",
        entity: "projetos",
        label: "Projetos",
        description: "Gestao do portifolio de projetos.",
      },
      {
        schema: "ps",
        entity: "tarefas",
        label: "Tarefas",
        description: "Tarefas, entregas e acompanhamento.",
      },
      {
        schema: "ps",
        entity: "recursosAlocados",
        label: "Recursos Alocados",
        description: "Alocacao de recursos e capacidade.",
      },
    ],
  },
  {
    sigla: "PP",
    title: "Planejamento e Producao",
    description: "Planejamento fabril e execucao operacional.",
    resources: [
      {
        schema: "pp",
        entity: "ordemProducao",
        label: "Ordens de Producao",
        description: "Ordens e execucao de producao.",
      },
      {
        schema: "pp",
        entity: "bom",
        label: "BOM",
        description: "Estruturas de materiais e composicao.",
      },
      {
        schema: "pp",
        entity: "apontamentos",
        label: "Apontamentos",
        description: "Apontamentos de producao e operacao.",
      },
      {
        schema: "pp",
        entity: "mrp",
        label: "MRP",
        description: "Planejamento de necessidades de materiais.",
      },
    ],
  },
  {
    sigla: "QM",
    title: "Qualidade",
    description: "Inspecoes, conformidade e melhoria continua.",
    resources: [
      {
        schema: "qm",
        entity: "inspecoes",
        label: "Inspecoes",
        description: "Inspecoes e verificacoes de qualidade.",
      },
      {
        schema: "qm",
        entity: "naoConformidade",
        label: "Nao Conformidades",
        description: "Registros e tratativas de nao conformidade.",
      },
    ],
  },
  {
    sigla: "GRC",
    title: "Governanca, Riscos e Compliance",
    description: "Controles, riscos e conformidade corporativa.",
    resources: [
      {
        schema: "grc",
        entity: "riscos",
        label: "Riscos",
        description: "Mapeamento e avaliacao de riscos.",
      },
      {
        schema: "grc",
        entity: "auditorias",
        label: "Auditorias",
        description: "Planos e execucao de auditorias.",
      },
      {
        schema: "grc",
        entity: "consentimentos",
        label: "Consentimentos",
        description: "Consentimentos e bases legais.",
      },
      {
        schema: "grc",
        entity: "controles",
        label: "Controles",
        description: "Controles internos e governanca.",
      },
    ],
  },
  {
    sigla: "PORTAL",
    title: "Portal",
    description: "Comunicacao, colaboracao e sessoes.",
    resources: [
      {
        schema: "portal",
        entity: "notificacoes",
        label: "Notificacoes",
        description: "Mensagens e notificacoes internas.",
      },
      {
        schema: "portal",
        entity: "dispositivos",
        label: "Dispositivos",
        description: "Dispositivos e acessos cadastrados.",
      },
      {
        schema: "portal",
        entity: "sessoes",
        label: "Sessoes",
        description: "Controle de sessoes e atividade.",
      },
    ],
  },
  {
    sigla: "AUDITORIA",
    title: "Auditoria",
    description: "Rastreabilidade e transparencia operacional.",
    resources: [
      {
        schema: "auditoria",
        entity: "logAcoes",
        label: "Log de Acoes",
        description: "Historico de acoes executadas.",
      },
      {
        schema: "auditoria",
        entity: "logErros",
        label: "Log de Erros",
        description: "Erros registrados pela aplicacao.",
      },
    ],
  },
  {
    sigla: "FISCAL",
    title: "Fiscal",
    description: "Obrigacoes fiscais e documentos digitais.",
    resources: [
      {
        schema: "fiscal",
        entity: "documentos",
        label: "Documentos",
        description: "Documentos fiscais e registros.",
      },
      {
        schema: "fiscal",
        entity: "esocialEventos",
        label: "eSocial",
        description: "Eventos eSocial.",
      },
      {
        schema: "fiscal",
        entity: "ecdRegistros",
        label: "ECD",
        description: "Registros contabeis digitais.",
      },
      {
        schema: "fiscal",
        entity: "efdRegistros",
        label: "EFD",
        description: "Registros fiscais digitais.",
      },
    ],
  },
  {
    sigla: "SD",
    title: "Sales and Distribution",
    description: "Operacao comercial, pedidos e faturamento.",
    resources: [
      {
        schema: "sd",
        entity: "clientes",
        label: "Clientes",
        description: "Base comercial de clientes.",
      },
      {
        schema: "sd",
        entity: "oportunidades",
        label: "Oportunidades",
        description: "Pipeline comercial e oportunidades.",
      },
      {
        schema: "sd",
        entity: "contratos",
        label: "Contratos",
        description: "Contratos e acordos comerciais.",
      },
      {
        schema: "sd",
        entity: "pedidos",
        label: "Pedidos",
        description: "Pedidos comerciais e operacionais.",
      },
      {
        schema: "sd",
        entity: "pedidoItens",
        label: "Itens do Pedido",
        description: "Itens vinculados aos pedidos.",
      },
      {
        schema: "sd",
        entity: "faturas",
        label: "Faturas",
        description: "Faturamento e cobranca.",
      },
    ],
  },
  {
    sigla: "SM",
    title: "Service Management",
    description: "Atendimento, operacao de servicos e SLA.",
    resources: [
      {
        schema: "sm",
        entity: "atendimentos",
        label: "Atendimentos",
        description: "Chamados e atendimentos.",
      },
      {
        schema: "sm",
        entity: "ordensServico",
        label: "Ordens de Servico",
        description: "Ordens e execucao de servicos.",
      },
      {
        schema: "sm",
        entity: "slaConfig",
        label: "SLA",
        description: "Configuracao de niveis de servico.",
      },
    ],
  },
  {
    sigla: "AM",
    title: "Asset Management",
    description: "Ativos, manutencao e ciclo de vida.",
    resources: [
      {
        schema: "am",
        entity: "bensPatrimoniais",
        label: "Bens Patrimoniais",
        description: "Cadastro e controle de ativos.",
      },
      {
        schema: "am",
        entity: "manutencoes",
        label: "Manutencoes",
        description: "Manutencoes preventivas e corretivas.",
      },
    ],
  },
  {
    sigla: "BI",
    title: "Business Intelligence",
    description: "Analise, dashboards e inteligencia executiva.",
    resources: [
      {
        schema: "bi",
        entity: "dashboards",
        label: "Dashboards",
        description: "Dashboards e paineis executivos.",
      },
      {
        schema: "bi",
        entity: "metricas",
        label: "Metricas",
        description: "Metricas e indicadores.",
      },
      {
        schema: "bi",
        entity: "historicoMetricas",
        label: "Historico de Metricas",
        description: "Evolucao historica dos indicadores.",
      },
      {
        schema: "bi",
        entity: "relatorios",
        label: "Relatorios",
        description: "Relatorios analiticos e gerenciais.",
      },
    ],
  },
];
