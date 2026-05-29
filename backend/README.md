# Fornece API - Back-end

API REST para gestao de Empresas e Fornecedores.

## Pre-requisitos

- Java 17
- Maven 3.8+

## Como executar

```bash
mvn spring-boot:run
```

A API inicia em **http://localhost:8080**

> Banco H2 em memoria - nao precisa instalar nada.

## Estrutura

```
src/main/java/com/desafio/
├── ForneceApplication.java      # Classe principal
├── config/
│   └── WebConfig.java           # CORS e RestTemplate
├── controller/
│   ├── EmpresaController.java   # /empresas
│   ├── FornecedorController.java# /fornecedores
│   └── CepController.java       # /cep/{cep}
├── service/
│   ├── EmpresaService.java      # Regras de empresa
│   ├── FornecedorService.java   # Regras de fornecedor
│   └── CepService.java          # Consulta ViaCEP
├── repository/
│   ├── EmpresaRepository.java
│   └── FornecedorRepository.java
├── entity/
│   ├── Empresa.java
│   └── Fornecedor.java
├── dto/
│   ├── EmpresaDTO.java
│   ├── EmpresaResumoDTO.java
│   ├── FornecedorDTO.java
│   └── FornecedorResumoDTO.java
└── exception/
    ├── BusinessException.java
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java
```

## Banco de dados

**Desenvolvimento (padrao):** H2 em memoria - ja configurado, sem instalacao.

**Producao:** PostgreSQL - edite `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fornece_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

## Testando a API

```bash
# Listar empresas
curl http://localhost:8080/empresas

# Criar empresa
curl -X POST http://localhost:8080/empresas \
  -H "Content-Type: application/json" \
  -d '{
    "cnpj": "12.345.678/0001-90",
    "nomeFantasia": "Empresa Teste",
    "cep": "01001-000",
    "estado": "SP",
    "cidade": "Sao Paulo"
  }'

# Criar fornecedor PJ
curl -X POST http://localhost:8080/fornecedores \
  -H "Content-Type: application/json" \
  -d '{
    "tipoPessoa": "JURIDICA",
    "nome": "Fornecedor Teste",
    "cpfCnpj": "98.765.432/0001-10",
    "email": "contato@fornecedor.com",
    "cep": "01001-000"
  }'
```
