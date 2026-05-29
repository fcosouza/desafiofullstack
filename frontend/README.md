# Fornece - Front-end

Interface web para gestao de Empresas e Fornecedores.

## Pre-requisitos

- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)

## Como executar

```bash
npm install
ng serve
```

Acesse **http://localhost:4200**

> O back-end precisa estar rodando em http://localhost:8080

## Estrutura

```
src/app/
├── app.component.ts             # Layout principal (sidebar + conteudo)
├── app.routes.ts                # Rotas
├── app.config.ts                # Providers (HTTP, Router, Animations)
├── models/
│   ├── empresa.model.ts         # Interfaces Empresa
│   ├── fornecedor.model.ts      # Interfaces Fornecedor
│   └── cep.model.ts             # Interface CepResponse
├── services/
│   ├── empresa.service.ts       # HTTP para /empresas
│   ├── fornecedor.service.ts    # HTTP para /fornecedores
│   └── cep.service.ts           # HTTP para /cep
├── pages/
│   ├── empresa-list/            # Listagem de empresas
│   ├── empresa-form/            # Cadastro/edicao de empresa
│   ├── fornecedor-list/         # Listagem de fornecedores
│   └── fornecedor-form/         # Cadastro/edicao de fornecedor
└── shared/
    └── components/
        ├── sidebar/             # Menu lateral
        └── confirm-dialog/      # Modal de confirmacao
```

## Telas

| Rota | Tela |
|------|------|
| `/empresas` | Listagem de empresas |
| `/empresas/novo` | Cadastro de empresa |
| `/empresas/editar/:id` | Edicao de empresa |
| `/fornecedores` | Listagem de fornecedores |
| `/fornecedores/novo` | Cadastro de fornecedor |
| `/fornecedores/editar/:id` | Edicao de fornecedor |

## Build para producao

```bash
ng build --configuration production
```

Os arquivos ficam em `dist/frontend/browser/`
