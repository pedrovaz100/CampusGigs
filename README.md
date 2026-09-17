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

O projeto e desenvolvido em checkpoints incrementais.

**CP1 — Docker e schema inicial:**

- Estrutura Maven do projeto.
- Dockerfile (multi-stage) e Docker Compose (aplicacao + PostgreSQL).
- Persistencia do banco em volume Docker.
- Healthcheck do PostgreSQL, com a aplicacao aguardando o banco ficar saudavel antes de iniciar.
- Migration inicial (Flyway) com as tabelas `usuarios`, `servicos` e `contratacoes`.
- Entidades JPA mapeadas para o schema (`ddl-auto=validate`, Flyway e o responsavel pelo schema).

**CP2 — Cadastro e autenticacao com senha protegida:**

- `POST /usuarios/cadastro`: cria usuario com papel `USER` (fixo pelo backend) e senha com hash BCrypt.
- `POST /auth/login`: valida e-mail e senha via `AuthenticationManager`/`DaoAuthenticationProvider` do Spring Security. Resposta de sucesso traz os dados do usuario autenticado, **sem emissao de token** (isso sera adicionado no CP3).
- Credenciais invalidas (senha errada ou e-mail inexistente) retornam a mesma mensagem generica em 401, sem revelar qual dado estava incorreto.
- E-mail duplicado no cadastro retorna 409.
- Validacao de campos obrigatorios com Bean Validation (400 com detalhamento por campo).
- Tratamento centralizado de erros (`GlobalExceptionHandler`) e respostas JSON tambem para falhas do filtro de seguranca (401/403).
- Senhas nunca aparecem nas respostas da API.

Emissao/validacao de JWT e regras de autorizacao por papel **ainda nao foram implementadas** — serao adicionadas nos proximos checkpoints.

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

## Endpoints disponiveis (CP2)

### Cadastrar usuario

```
POST /usuarios/cadastro
Content-Type: application/json

{
  "nome": "Ana Silva",
  "email": "ana@campus.edu",
  "senha": "senha123"
}
```

Retorna `201` com os dados do usuario criado (papel `USER`, sem a senha). Retorna `409` se o e-mail ja estiver cadastrado e `400` se algum campo for invalido.

### Login

```
POST /auth/login
Content-Type: application/json

{
  "email": "ana@campus.edu",
  "senha": "senha123"
}
```

Retorna `200` com os dados do usuario autenticado quando as credenciais sao validas, ou `401` com mensagem generica quando invalidas (sem indicar se o problema foi o e-mail ou a senha). Esta etapa ainda nao emite token JWT.
