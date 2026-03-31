--Base dos schemas para o db

-- Módulos Core
CREATE SCHEMA IF NOT EXISTS core;-- Tabelas base/compartilhadas
CREATE SCHEMA IF NOT EXISTS sys;-- Administração do Sistema

-- Módulos de Negócio
CREATE SCHEMA IF NOT EXISTS financeiro;-- Gestão Financeira (FI)
CREATE SCHEMA IF NOT EXISTS contabil;-- Contabilidade (parte do FI)

CREATE SCHEMA IF NOT EXISTS rh;
CREATE SCHEMA IF NOT EXISTS materiais;-- Gestão de Materiais (MM)
CREATE SCHEMA IF NOT EXISTS vendas;-- Vendas e Clientes (SD)
CREATE SCHEMA IF NOT EXISTS crm;-- CRM (Customer Relationship)
CREATE SCHEMA IF NOT EXISTS producao;-- Gestão de Produção (PP)
CREATE SCHEMA IF NOT EXISTS projetos;-- Gestão de Projetos (PS)
CREATE SCHEMA IF NOT EXISTS servicos;-- Gestão de Serviços (SM)
CREATE SCHEMA IF NOT EXISTS qualidade;-- Controle de Qualidade (QM)
CREATE SCHEMA IF NOT EXISTS ativos;-- Gestão de Ativos (AM)

-- Módulos de Apoio e Integração
CREATE SCHEMA IF NOT EXISTS fiscal;
CREATE SCHEMA IF NOT EXISTS sped;-- Obrigações Acessórias
CREATE SCHEMA IF NOT EXISTS bi;
CREATE SCHEMA IF NOT EXISTS grc;-- Governança e Compliance
CREATE SCHEMA IF NOT EXISTS portal;-- Portal Web
CREATE SCHEMA IF NOT EXISTS mobile;
CREATE SCHEMA IF NOT EXISTS auditoria;-- Logs e Auditoria