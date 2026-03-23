# AZ_ERP Frontend

Interface web React/TypeScript para o sistema ERP AZ_ERP, construída com Vite, React Router e Axios.

## 🚀 Funcionalidades

- **CRUD Completo**: Interface genérica para todas as entidades do backend
- **Navegação Modular**: Acesso rápido a todos os módulos do ERP
- **Editor JSON**: Interface intuitiva para criação e edição de registros
- **Design Responsivo**: Interface moderna e adaptável a diferentes telas
- **Integração REST**: Comunicação direta com o backend Spring Boot

## 📋 Módulos Disponíveis

O frontend oferece acesso completo aos seguintes módulos:

### Core (Núcleo)

- 🏢 Empresas
- 🤝 Parceiros
- 📞 Contatos
- 📍 Endereços
- 📦 Produtos

### Financeiro (FI)

- 💰 Contas a Pagar
- 💸 Contas a Receber
- 📊 Plano de Contas
- 💳 Movimentações Bancárias
- 💵 Fluxo de Caixa
- 🏢 Centros de Custo

### Gestão de Materiais (MM)

- 🛒 Compras
- 📦 Itens de Compra
- 📦 Estoques
- 🔄 Movimentações
- 📋 Inventários
- 🏭 Materiais

### Recursos Humanos (RH)

- 👥 Colaboradores
- 💼 Benefícios
- ⏰ Controle de Ponto
- 💰 Folha de Pagamento

### Gestão de Projetos (PS)

- 📋 Projetos
- ✅ Tarefas
- 👥 Recursos Alocados

### Produção (PP)

- 🏭 Ordem de Produção
- 📋 BOM (Lista de Materiais)
- 📊 MRP (Planejamento de Requisitos)
- ⏰ Apontamentos

### Qualidade (QM)

- 🔍 Inspeções
- ❌ Não Conformidades

### Governança (GRC)

- 📋 Auditorias
- ✅ Controles
- ⚠️ Riscos
- 📝 Consentimentos

### Portal

- 🔔 Notificações
- 📱 Dispositivos
- 🔐 Sessões

### Sistema (SYS)

- 📝 Log de Ações
- ⚠️ Log de Erros

### BI (Business Intelligence)

- 📊 Dashboards
- 📈 Métricas
- 📈 Histórico de Métricas
- 📋 Relatórios

### Fiscal

- 📄 Documentos Fiscais
- 📋 Registros EFD
- 📋 Registros EDC
- 📝 Eventos eSocial

## 🛠️ Tecnologias Utilizadas

- **React 19.2.0** - Framework JavaScript
- **TypeScript 5.9.3** - Tipagem estática
- **Vite 7.3.1** - Build tool e dev server
- **React Router DOM 6.18.1** - Roteamento
- **Axios 1.6.0** - Cliente HTTP
- **ESLint** - Linting e formatação de código

## 🚀 Como Executar

### Pré-requisitos

- Node.js 18+
- Backend AZ_ERP rodando (Spring Boot)

### Instalação

1. Instale as dependências:

```bash
npm install
```

2. Configure o backend (opcional):
   - Crie um arquivo `.env` na raiz do frontend
   - Defina a URL do backend:

   ```
   VITE_API_BASE_URL=http://localhost:8080/api
   ```

3. Execute o servidor de desenvolvimento:

```bash
npm run dev
```

4. Acesse `http://localhost:5173` no navegador

### Build para Produção

```bash
npm run build
```

Os arquivos otimizados serão gerados na pasta `dist/`.

## 📁 Estrutura do Projeto

```
frontend/
├── public/              # Arquivos estáticos
├── src/
│   ├── components/      # Componentes React
│   │   └── ModuleCrud.tsx
│   ├── pages/           # Páginas da aplicação
│   │   ├── Home.tsx
│   │   └── pages.css
│   ├── services/        # Serviços e configurações
│   │   ├── api.ts
│   │   └── resourceService.ts
│   ├── App.tsx          # Componente principal
│   ├── App.css          # Estilos do App
│   ├── index.css        # Estilos globais
│   └── main.tsx         # Ponto de entrada
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

## 🔧 Configuração

### API Base URL

Por padrão, a aplicação tenta conectar ao backend em `http://localhost:8080/api`. Para alterar:

1. Crie um arquivo `.env` na raiz do frontend
2. Adicione:

```
VITE_API_BASE_URL=http://seu-backend-url:porta/api
```

### Desenvolvimento

- `npm run dev` - Inicia o servidor de desenvolvimento
- `npm run build` - Build para produção
- `npm run preview` - Preview do build de produção
- `npm run lint` - Executa o ESLint

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto é parte do sistema AZ_ERP e segue a mesma licença do backend.
import reactDom from 'eslint-plugin-react-dom'

export default defineConfig([
globalIgnores(['dist']),
{
files: ['**/*.{ts,tsx}'],
extends: [
// Other configs...
// Enable lint rules for React
reactX.configs['recommended-typescript'],
// Enable lint rules for React DOM
reactDom.configs.recommended,
],
languageOptions: {
parserOptions: {
project: ['./tsconfig.node.json', './tsconfig.app.json'],
tsconfigRootDir: import.meta.dirname,
},
// other options...
},
},
])

```

```
