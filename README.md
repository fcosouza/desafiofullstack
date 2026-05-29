# Fornece - Gestao de Empresas e Fornecedores

Sistema Full-Stack para cadastro de Empresas e Fornecedores com relacionamento ManyToMany.

## Tecnologias

| Back-end | Front-end |
|----------|-----------|
| Java 17 | Angular 19 |
| Spring Boot 2.7 | Angular Material |
| Spring Data JPA | Reactive Forms |
| Bean Validation | RxJS |
| Lombok | TypeScript |
| H2 / PostgreSQL | SCSS |

## Arquitetura

```
backend/                        frontend/
├── controller/  (REST API)     ├── pages/       (telas)
├── service/     (regras)       ├── services/    (HTTP)
├── repository/  (banco)        ├── models/      (interfaces)
├── entity/      (tabelas)      └── shared/      (componentes)
├── dto/         (entrada/saida)
├── exception/   (erros)
└── config/      (CORS, beans)
```

## Pre-requisitos

- **Java 17** - [Download](https://adoptium.net/)
- **Maven 3.8+** - [Download](https://maven.apache.org/)
- **Node.js 18+** - [Download](https://nodejs.org/)
- **Angular CLI** - `npm install -g @angular/cli`

## Como executar

### 1. Clone o repositorio

```bash
git clone <url-do-repositorio>
cd desafiofullstack
```

### 2. Back-end (porta 8080)

```bash
cd backend
mvn spring-boot:run
```

> O banco H2 em memoria ja vem configurado. Nao precisa instalar banco de dados.
> Console H2 disponivel em: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:fornece_db`, user: `sa`, sem senha)

### 3. Front-end (porta 4200)

```bash
cd frontend
npm install
ng serve
```

### 4. Acesse

Abra o navegador em **http://localhost:4200**

## Usando PostgreSQL (opcional)

Se preferir usar PostgreSQL em vez do H2, edite `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fornece_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

E crie o banco:

```bash
createdb fornece_db
```

## Docker Compose (opcional)

```bash
docker-compose up --build
```

Acesse em http://localhost:4200

## Endpoints da API

| Metodo | Rota | Descricao |
|--------|------|-----------|
| GET | `/empresas` | Listar empresas |
| GET | `/empresas/{id}` | Buscar empresa |
| POST | `/empresas` | Criar empresa |
| PUT | `/empresas/{id}` | Atualizar empresa |
| DELETE | `/empresas/{id}` | Excluir empresa |
| GET | `/fornecedores` | Listar fornecedores |
| GET | `/fornecedores/{id}` | Buscar fornecedor |
| GET | `/fornecedores/filtro?nome=&cpfCnpj=` | Filtrar fornecedores |
| POST | `/fornecedores` | Criar fornecedor |
| PUT | `/fornecedores/{id}` | Atualizar fornecedor |
| DELETE | `/fornecedores/{id}` | Excluir fornecedor |
| GET | `/cep/{cep}` | Consultar CEP |

## Regras de Negocio

1. **CPF/CNPJ unicos** - Nao permite duplicatas
2. **Pessoa Fisica** - RG e data de nascimento obrigatorios
3. **Empresa do Parana (PR)** - Nao aceita fornecedor pessoa fisica menor de idade
4. **Validacao de CEP** - Validado no front-end e no back-end via ViaCEP

## Funcionalidades

- CRUD completo de Empresas e Fornecedores
- Vinculacao ManyToMany entre Empresa e Fornecedor
- Busca por nome e CPF/CNPJ
- Filtro por tipo (Pessoa Fisica / Juridica)
- Mascaras para CPF, CNPJ e CEP
- Consulta automatica de CEP com preenchimento de cidade/estado
- Confirmacao antes de excluir
- Notificacoes de sucesso/erro
