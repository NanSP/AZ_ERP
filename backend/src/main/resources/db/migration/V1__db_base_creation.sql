--criacao da base do banco e os schemas

DROP DATABASE IF EXISTS az_erp_db;

CREATE DATABASE az_erp_db;

\c az_erp_db;

CREATE SCHEMA IF NOT EXISTS fi
    AUTHORIZATION postgres;

COMMENT ON SCHEMA fi IS 'Schema do departamento financeiro';
--============================================================

CREATE SCHEMA IF NOT EXISTS rh
    AUTHORIZATION postgres;

COMMENT ON SCHEMA rh IS 'Schema do departamento de RH';
--============================================================

CREATE SCHEMA IF NOT EXISTS mm
    AUTHORIZATION postgres;

COMMENT ON SCHEMA mm IS 'Schema do departamento de gestão de materiais';
--============================================================

CREATE SCHEMA IF NOT EXISTS crm
    AUTHORIZATION postgres;

COMMENT ON SCHEMA crm IS 'Schema do departamento de gestão de vendas e clientes';
--============================================================

CREATE SCHEMA IF NOT EXISTS pp
    AUTHORIZATION postgres;

COMMENT ON SCHEMA pp IS 'Schema do departamento de gestão de produção';
--============================================================

CREATE SCHEMA IF NOT EXISTS ps
    AUTHORIZATION postgres;

COMMENT ON SCHEMA ps IS 'Schema do departamento de gestão de projetos';
--============================================================

CREATE SCHEMA IF NOT EXISTS sm
    AUTHORIZATION postgres;

COMMENT ON SCHEMA sm IS 'Schema do departamento de gestão de serviços';
--============================================================

CREATE SCHEMA IF NOT EXISTS bi
    AUTHORIZATION postgres;

COMMENT ON SCHEMA bi IS 'Schema do departamento de business inteligence';
--============================================================

CREATE SCHEMA IF NOT EXISTS grc
    AUTHORIZATION postgres;

COMMENT ON SCHEMA grc IS 'Schema do departamento de governança e compilance';
--============================================================

CREATE SCHEMA IF NOT EXISTS qm
    AUTHORIZATION postgres;

COMMENT ON SCHEMA qm IS 'Schema do departamento de controle de qualidade';
--============================================================

CREATE SCHEMA IF NOT EXISTS am
    AUTHORIZATION postgres;

COMMENT ON SCHEMA am IS 'Schema do departamento de gestão de ativos';
--============================================================

CREATE SCHEMA IF NOT EXISTS contabil
    AUTHORIZATION postgres;

COMMENT ON SCHEMA contabil IS 'Módulo de Contabilidade';
--============================================================

CREATE SCHEMA IF NOT EXISTS fiscal
    AUTHORIZATION postgres;

COMMENT ON SCHEMA fiscal IS 'Módulo Fiscal';
--============================================================

CREATE SCHEMA IF NOT EXISTS sped
    AUTHORIZATION postgres;

COMMENT ON SCHEMA sped IS 'Obrigações Acessórias - SPED, EFD, eSocial';
--============================================================

CREATE SCHEMA IF NOT EXISTS tributos
    AUTHORIZATION postgres;

COMMENT ON SCHEMA tributos IS 'Gestão de Tributos - Cálculos, alíquotas, regimes tributários';
--============================================================

CREATE SCHEMA IF NOT EXISTS usarios
    AUTHORIZATION postgres;

COMMENT ON SCHEMA usuarios IS 'Schema de usuarios';
--============================================================

CREATE SCHEMA IF NOT EXISTS sys
    AUTHORIZATION postgres;

COMMENT ON SCHEMA sys IS 'Schema de administração do sistema';
--============================================================