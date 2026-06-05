import amImage from "../assets/am.png";
import auditoriaImage from "../assets/auditoria.png";
import biImage from "../assets/bi.png";
import coreImage from "../assets/core.png";
import fiImage from "../assets/fi.png";
import fiscalImage from "../assets/fiscal.png";
import grcImage from "../assets/grc.png";
import mmImage from "../assets/mm.png";
import portalImage from "../assets/portal.png";
import ppImage from "../assets/pp.png";
import psImage from "../assets/ps.png";
import qmImage from "../assets/qm.png";
import rhImage from "../assets/rh.png";
import sdImage from "../assets/sd.png";
import smImage from "../assets/sm.png";
import sysImage from "../assets/sys.png";

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
    image: sysImage,
  },
  {
    sigla: "CORE",
    title: "Dados mestres",
    description:
      "Centralize clientes, fornecedores, produtos, filiais e estruturas corporativas em uma base coerente.",
    image: coreImage,
  },
  {
    sigla: "FI",
    title: "Financeiro",
    description:
      "Contas a pagar, contas a receber, fluxo de caixa, tesouraria e gestão contábil em tempo real.",
    image: fiImage,
  },
  {
    sigla: "MM",
    title: "Materiais e suprimentos",
    description:
      "Compras, estoque, inventário, fornecedores e controle operacional da cadeia de suprimentos.",
    image: mmImage,
  },
  {
    sigla: "RH",
    title: "Recursos humanos",
    description:
      "Gestão de colaboradores, folha, benefícios, ponto e estrutura de pessoas.",
    image: rhImage,
  },
  {
    sigla: "PS",
    title: "Projetos e serviços",
    description:
      "Planeje entregas, acompanhe execução e aloque recursos com visibilidade operacional.",
    image: psImage,
  },
  {
    sigla: "PP",
    title: "Planejamento e produção",
    description:
      "Coordene produção, capacidade, BOM, apontamentos e eficiência operacional.",
    image: ppImage,
  },
  {
    sigla: "QM",
    title: "Qualidade",
    description:
      "Inspeções, não conformidades e melhoria contínua com trilha de controle.",
    image: qmImage,
  },
  {
    sigla: "GRC",
    title: "Governança, risco e compliance",
    description:
      "Fortaleca controles, auditoria, riscos e governança corporativa.",
    image: grcImage,
  },
  {
    sigla: "PORTAL",
    title: "Portal e comunicação",
    description:
      "Comunicação corporativa, sessões, dispositivos e compartilhamento de informação.",
    image: portalImage,
  },
  {
    sigla: "AUDITORIA",
    title: "Auditoria e rastreabilidade",
    description:
      "Trilhas completas de log, eventos e transparência operacional.",
    image: auditoriaImage,
  },
  {
    sigla: "FISCAL",
    title: "Fiscal",
    description:
      "Documentos fiscais, obrigações regulatorias e conformidade tributária.",
    image: fiscalImage,
  },
  {
    sigla: "SD",
    title: "Comercial e vendas",
    description:
      "Clientes, oportunidades, pedidos, contratos, faturamento e desempenho comercial.",
    image: sdImage,
  },
  {
    sigla: "SM",
    title: "Service management",
    description:
      "Atendimento, ordens de serviço, SLA e operação de suporte ao cliente.",
    image: smImage,
  },
  {
    sigla: "AM",
    title: "Ativos e manutenção",
    description:
      "Patrimônio, manutenção preventiva, corretiva e monitoramento de equipamentos.",
    image: amImage,
  },
  {
    sigla: "BI",
    title: "Business intelligence",
    description:
      "Dashboards executivos, KPIs, métricas e análises para decisão baseada em dados.",
    image: biImage,
  },
];

export const benefits = [
  "Eficiência operacional",
  "Padronização de processos",
  "Visibilidade em tempo real",
  "Compliance e governança",
  "Escalabilidade",
  "Inteligência para decisãoes",
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
    company: "Empresa de servicos B2B",
  },
  {
    quote:
      "A arquitetura enterprise e a rastreabilidade deram segurança para crescer com governança.",
    author: "Head de tecnologia",
    company: "Operação distribuída nacional",
  },
];
