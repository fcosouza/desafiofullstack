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
| PostgreSQL | SCSS |

## Pre-requisitos

- **Docker** e **Docker Compose** - [Download](https://www.docker.com/products/docker-desktop/)

## Executando com Docker (recomendado)

```bash
git clone <url-do-repositorio>
cd desafiofullstack
docker-compose up --build
```

Aguarde os 3 containers subirem (postgres, backend, frontend) e acesse:

- **Aplicacao:** http://localhost:4200
- **API:** http://localhost:8080

Para parar:

```bash
docker-compose down
```

Para parar e limpar os dados do banco:

```bash
docker-compose down -v
```

## Executando sem Docker (desenvolvimento/debug)

### Pre-requisitos adicionais

- **Java 17** - [Download](https://adoptium.net/)
- **Maven 3.8+** - [Download](https://maven.apache.org/)
- **Node.js 18+** - [Download](https://nodejs.org/)
- **Angular CLI** - `npm install -g @angular/cli`
- **PostgreSQL 15** - [Download](https://www.postgresql.org/download/)

### 1. Configurar o banco

Crie o banco de dados no PostgreSQL:

```bash
createdb fornece_db
```

As credenciais padrao estao em `backend/src/main/resources/application.properties`. Se precisar alterar usuario ou senha, use variaveis de ambiente:

```bash
export DB_USER=seu_usuario
export DB_PASS=sua_senha
```

### 2. Iniciar o back-end (porta 8080)

```bash
cd backend
mvn spring-boot:run
```

### 3. Iniciar o front-end (porta 4200)

Em outro terminal:

```bash
cd frontend
npm install
ng serve
```

### 4. Acessar

Abra http://localhost:4200

## Testes

```bash
cd backend
mvn test
```

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
4. **Validacao de CEP** - Consulta via API externa (cep.la com fallback ViaCEP)
