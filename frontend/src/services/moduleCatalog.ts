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
      "Gerencie usuários, perfis, permissões, autenticação, controle de acesso e estrutura organizacional.",
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
      "Contas a pagar, contas a receber, fluxo de caixa, tesouraria e gestão contábil em tempo real.",
    image: "/module-images/fi.webp",
  },
  {
    sigla: "MM",
    title: "Materiais e suprimentos",
    description:
      "Compras, estoque, inventário, fornecedores e controle operacional da cadeia de suprimentos.",
    image: "/module-images/mm.webp",
  },
  {
    sigla: "RH",
    title: "Recursos humanos",
    description:
      "Gestão de colaboradores, folha, benefícios, ponto e estrutura de pessoas.",
    image: "/module-images/rh.webp",
  },
  {
    sigla: "PS",
    title: "Projetos e serviços",
    description:
      "Planeje entregas, acompanhe execução e aloque recursos com visibilidade operacional.",
    image: "/module-images/ps.webp",
  },
  {
    sigla: "PP",
    title: "Planejamento e produção",
    description:
      "Coordene produção, capacidade, BOM, apontamentos e eficiencia operacional.",
    image: "/module-images/pp.webp",
  },
  {
    sigla: "QM",
    title: "Qualidade",
    description:
      "Inspeções, não conformidades e melhoria contínua com trilha de controle.",
    image: "/module-images/qm.webp",
  },
  {
    sigla: "GRC",
    title: "Governança, risco e compliance",
    description:
      "Fortaleca controles, auditoria, riscos e governança corporativa.",
    image: "/module-images/grc.webp",
  },
  {
    sigla: "PORTAL",
    title: "Portal e comunicação",
    description:
      "Comunicação corporativa, sessões, dispositivos e compartilhamento de informação.",
    image: "/module-images/portal.webp",
  },
  {
    sigla: "AUDITORIA",
    title: "Auditoria e rastreabilidade",
    description:
      "Trilhas completas de log, eventos e transparência operacional.",
    image: "/module-images/auditoria.webp",
  },
  {
    sigla: "FISCAL",
    title: "Fiscal",
    description:
      "Documentos fiscais, obrigações regulatorias e conformidade tributária.",
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
      "Atendimento, ordens de serviço, SLA e operação de suporte ao cliente.",
    image: "/module-images/sm.webp",
  },
  {
    sigla: "AM",
    title: "Ativos e manutenção",
    description:
      "Patrimônio, manutenção preventiva, corretiva e monitoramento de equipamentos.",
    image: "/module-images/am.webp",
  },
  {
    sigla: "BI",
    title: "Business intelligence",
    description:
      "Dashboards executivos, KPIs, métricas e análises para decisão baseada em dados.",
    image: "/module-images/bi.webp",
  },
];

export const benefits = [
  "Eficiência operacional",
  "Padronização de processos",
  "Visibilidade em tempo real",
  "Compliance e governança",
  "Escalabilidade",
  "Inteligência para decisões",
];

export const testimonials = [
  {
    quote:
      "Conseguimos unificar processos financeiros, operacionais e de governança sem perder controle por unidade.",
    author: "Diretoria de Operações",
    company: "Grupo industrial multiempresa",
  },
  {
    quote:
      "A visibilidade em tempo real elevou a qualidade das decisões e reduziu retrabalho entre equipes.",
    author: "Gerência executiva",
    company: "Empresa de serviços B2B",
  },
  {
    quote:
      "A arquitetura enterprise e a rastreabilidade deram segurança para crescer com governança.",
    author: "Head de tecnologia",
    company: "Operação distribuída nacional",
  },
];
