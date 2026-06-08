export type ModuleCard = {
  sigla: string;
  title: string;
  description: string;
  image: string;
};

export const moduleCatalog: ModuleCard[] = [
  {
    sigla: "SYS",
    title: "Sistema e acesso",
    description:
      "Gerencie usuarios, perfis, permissoes, autenticacao, controle de acesso e estrutura organizacional.",
    image: "/module-images/sys.webp",
  },
  {
    sigla: "CORE",
    title: "Dados mestres",
    description:
      "Centralize clientes, fornecedores, produtos, filiais e estruturas corporativas em uma base coerente.",
    image: "/module-images/core.webp",
  },
  {
    sigla: "FI",
    title: "Financeiro",
    description:
      "Contas a pagar, contas a receber, fluxo de caixa, tesouraria e gestao contabil em tempo real.",
    image: "/module-images/fi.webp",
  },
  {
    sigla: "MM",
    title: "Materiais e suprimentos",
    description:
      "Compras, estoque, inventario, fornecedores e controle operacional da cadeia de suprimentos.",
    image: "/module-images/mm.webp",
  },
  {
    sigla: "RH",
    title: "Recursos humanos",
    description:
      "Gestao de colaboradores, folha, beneficios, ponto e estrutura de pessoas.",
    image: "/module-images/rh.webp",
  },
  {
    sigla: "PS",
    title: "Projetos e servicos",
    description:
      "Planeje entregas, acompanhe execucao e aloque recursos com visibilidade operacional.",
    image: "/module-images/ps.webp",
  },
  {
    sigla: "PP",
    title: "Planejamento e producao",
    description:
      "Coordene producao, capacidade, BOM, apontamentos e eficiencia operacional.",
    image: "/module-images/pp.webp",
  },
  {
    sigla: "QM",
    title: "Qualidade",
    description:
      "Inspecoes, nao conformidades e melhoria continua com trilha de controle.",
    image: "/module-images/qm.webp",
  },
  {
    sigla: "GRC",
    title: "Governanca, risco e compliance",
    description:
      "Fortaleca controles, auditoria, riscos e governanca corporativa.",
    image: "/module-images/grc.webp",
  },
  {
    sigla: "PORTAL",
    title: "Portal e comunicacao",
    description:
      "Comunicacao corporativa, sessoes, dispositivos e compartilhamento de informacao.",
    image: "/module-images/portal.webp",
  },
  {
    sigla: "AUDITORIA",
    title: "Auditoria e rastreabilidade",
    description:
      "Trilhas completas de log, eventos e transparencia operacional.",
    image: "/module-images/auditoria.webp",
  },
  {
    sigla: "FISCAL",
    title: "Fiscal",
    description:
      "Documentos fiscais, obrigacoes regulatorias e conformidade tributaria.",
    image: "/module-images/fiscal.webp",
  },
  {
    sigla: "SD",
    title: "Comercial e vendas",
    description:
      "Clientes, oportunidades, pedidos, contratos, faturamento e desempenho comercial.",
    image: "/module-images/sd.webp",
  },
  {
    sigla: "SM",
    title: "Service management",
    description:
      "Atendimento, ordens de servico, SLA e operacao de suporte ao cliente.",
    image: "/module-images/sm.webp",
  },
  {
    sigla: "AM",
    title: "Ativos e manutencao",
    description:
      "Patrimonio, manutencao preventiva, corretiva e monitoramento de equipamentos.",
    image: "/module-images/am.webp",
  },
  {
    sigla: "BI",
    title: "Business intelligence",
    description:
      "Dashboards executivos, KPIs, metricas e analises para decisao baseada em dados.",
    image: "/module-images/bi.webp",
  },
];

export const benefits = [
  "Eficiencia operacional",
  "Padronizacao de processos",
  "Visibilidade em tempo real",
  "Compliance e governanca",
  "Escalabilidade",
  "Inteligencia para decisoes",
];

export const testimonials = [
  {
    quote:
      "Conseguimos unificar processos financeiros, operacionais e de governanca sem perder controle por unidade.",
    author: "Diretoria de Operacoes",
    company: "Grupo industrial multiempresa",
  },
  {
    quote:
      "A visibilidade em tempo real elevou a qualidade das decisoes e reduziu retrabalho entre equipes.",
    author: "Gerencia executiva",
    company: "Empresa de servicos B2B",
  },
  {
    quote:
      "A arquitetura enterprise e a rastreabilidade deram seguranca para crescer com governanca.",
    author: "Head de tecnologia",
    company: "Operacao distribuida nacional",
  },
];
