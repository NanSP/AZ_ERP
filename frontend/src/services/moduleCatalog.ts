import { moduleImageAsset } from "./publicAssets";

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
    image: moduleImageAsset("sys"),
  },
  {
    sigla: "CORE",
    title: "Dados mestres",
    description:
      "Centralize clientes, fornecedores, produtos, filiais e estruturas corporativas em uma base coerente.",
    image: moduleImageAsset("core"),
  },
  {
    sigla: "FI",
    title: "Financeiro",
    description:
      "Contas a pagar, contas a receber, fluxo de caixa, tesouraria e gestão contábil em tempo real.",
    image: moduleImageAsset("fi"),
  },
  {
    sigla: "MM",
    title: "Materiais e suprimentos",
    description:
      "Compras, estoque, inventário, fornecedores e controle operacional da cadeia de suprimentos.",
    image: moduleImageAsset("mm"),
  },
  {
    sigla: "RH",
    title: "Recursos humanos",
    description:
      "Gestão de colaboradores, folha, benefícios, ponto e estrutura de pessoas.",
    image: moduleImageAsset("rh"),
  },
  {
    sigla: "PS",
    title: "Projetos e serviços",
    description:
      "Planeje entregas, acompanhe execução e aloque recursos com visibilidade operacional.",
    image: moduleImageAsset("ps"),
  },
  {
    sigla: "PP",
    title: "Planejamento e produção",
    description:
      "Coordene produção, capacidade, BOM, apontamentos e eficiencia operacional.",
    image: moduleImageAsset("pp"),
  },
  {
    sigla: "QM",
    title: "Qualidade",
    description:
      "Inspeções, não conformidades e melhoria contínua com trilha de controle.",
    image: moduleImageAsset("qm"),
  },
  {
    sigla: "GRC",
    title: "Governança, risco e compliance",
    description:
      "Fortalecimento dos controles, auditoria, riscos e governança corporativa.",
    image: moduleImageAsset("grc"),
  },
  {
    sigla: "PORTAL",
    title: "Portal e comunicação",
    description:
      "Comunicação corporativa, sessões, dispositivos e compartilhamento de informação.",
    image: moduleImageAsset("portal"),
  },
  {
    sigla: "AUDITORIA",
    title: "Auditoria e rastreabilidade",
    description:
      "Trilhas completas de log, eventos e transparência operacional.",
    image: moduleImageAsset("auditoria"),
  },
  {
    sigla: "FISCAL",
    title: "Fiscal",
    description:
      "Documentos fiscais, obrigações regulatorias e conformidade tributária.",
    image: moduleImageAsset("fiscal"),
  },
  {
    sigla: "SD",
    title: "Comercial e vendas",
    description:
      "Clientes, oportunidades, pedidos, contratos, faturamento e desempenho comercial.",
    image: moduleImageAsset("sd"),
  },
  {
    sigla: "SM",
    title: "Service management",
    description:
      "Atendimento, ordens de serviço, SLA e operação de suporte ao cliente.",
    image: moduleImageAsset("sm"),
  },
  {
    sigla: "AM",
    title: "Ativos e manutenção",
    description:
      "Patrimônio, manutenção preventiva, corretiva e monitoramento de equipamentos.",
    image: moduleImageAsset("am"),
  },
  {
    sigla: "BI",
    title: "Business intelligence",
    description:
      "Dashboards executivos, KPIs, métricas e análises para decisão baseada em dados.",
    image: moduleImageAsset("bi"),
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
      "A arquitetura enterprise e a rastreabilidade deram seguranÃ§a para crescer com governança.",
    author: "Head de tecnologia",
    company: "Operação distribuída nacional",
  },
];
