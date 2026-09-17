# CampusGigs API

Plataforma de servicos freelance entre estudantes universitarios. API REST construida com Java 17 e Spring Boot.

## Tecnologias

- Java 17
- Spring Boot 3.3 (Web, Data JPA, Validation, Security)
- Flyway (migrations)
- PostgreSQL (banco da aplicacao)
- H2 (uso em testes)
- Lombok
- Maven / Maven Wrapper
- Docker e Docker Compose

## Status do desenvolvimento

O projeto e desenvolvido em checkpoints incrementais. Esta versao corresponde ao **CP1 — Docker e schema inicial**:

- Estrutura Maven do projeto.
- Dockerfile (multi-stage) e Docker Compose (aplicacao + PostgreSQL).
- Persistencia do banco em volume Docker.
- Healthcheck do PostgreSQL, com a aplicacao aguardando o banco ficar saudavel antes de iniciar.
- Migration inicial (Flyway) com as tabelas `usuarios`, `servicos` e `contratacoes`.
- Entidades JPA mapeadas para o schema (`ddl-auto=validate`, Flyway e o responsavel pelo schema).

Cadastro, login, emissao de JWT e regras de autorizacao **ainda nao foram implementados** — serao adicionados nos proximos checkpoints.

## Pre-requisitos

- Docker e Docker Compose.
- (Opcional, para build local fora do Docker) JDK 17.

## Como subir o ambiente

1. Copie o arquivo de variaveis de ambiente de exemplo:

   ```
   cp .env.example .env
   ```

2. Suba os containers:

   ```
   docker compose up --build
   ```

3. A API sobe em `http://localhost:8080` (porta configuravel via `SERVER_PORT`). O banco PostgreSQL fica disponivel em `localhost:5432` (configuravel via `DB_PORT`), com os dados persistidos no volume `campusgigs_db_data`.

4. Para parar os containers preservando os dados:

   ```
   docker compose down
   ```

   Para remover tambem o volume de dados:

   ```
   docker compose down -v
   ```

## Variaveis de ambiente

| Variavel      | Descricao                              | Padrao       |
|---------------|-----------------------------------------|--------------|
| `DB_NAME`     | Nome do banco de dados                  | `campusgigs` |
| `DB_USER`     | Usuario do banco                        | `campusgigs` |
| `DB_PASSWORD` | Senha do banco                          | `campusgigs` |
| `DB_PORT`     | Porta exposta do PostgreSQL no host     | `5432`       |
| `SERVER_PORT` | Porta exposta da API no host            | `8080`       |

## Estrutura do schema (V1)

- `usuarios`: nome, e-mail (unico), hash de senha, papel (`USER`/`ADMIN`), CEP/cidade/UF.
- `servicos`: vinculado ao prestador (`usuarios`), titulo, descricao, categoria, preco, situacao (`ATIVO`/`PAUSADO`/`ENCERRADO`).
- `contratacoes`: vinculada a um servico e a um contratante (`usuarios`), situacao (`SOLICITADA`/`ACEITA`/`CONCLUIDA`/`CANCELADA`).

Novas evolucoes de schema devem ser feitas por meio de novas migrations (`V2__...sql`, etc.), nunca alterando migrations ja aplicadas.
