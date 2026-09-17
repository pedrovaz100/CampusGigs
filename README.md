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

**CP3 — Emissao e validacao de JWT:**

- `POST /auth/login` agora retorna um token JWT (`token`, `tipo: "Bearer"`) junto aos dados do usuario autenticado.
- `GET /usuarios/me`: endpoint protegido que retorna os dados do usuario autenticado a partir do token (identidade extraida do proprio token, nunca de um ID enviado pelo cliente).
- `JwtAuthenticationFilter` valida assinatura e expiracao do token em cada requisicao e popula o contexto de seguranca do Spring; API 100% stateless (`SessionCreationPolicy.STATELESS`), sem depender de sessao HTTP.
- Chave de assinatura e tempo de expiracao configurados externamente via `JWT_SECRET` e `JWT_EXPIRATION_MS` (sem segredo real versionado; o valor em `.env.example` e apenas um placeholder de desenvolvimento).
- Respostas 401 diferenciadas para token ausente, invalido e expirado, sempre em JSON (inclusive erros vindos do filtro de seguranca, antes de chegar ao controller).
- Cadastro e login continuam publicos; qualquer outro endpoint exige o header `Authorization: Bearer <token>`.

**CP4 — Autorizacao por papel e regras de negocio:**

- `POST /servicos`: publica servico com o prestador obtido do token (nunca de um ID enviado pelo cliente).
- `GET /servicos` e `GET /servicos/{id}`: listagem e detalhe (autenticado).
- `PUT /servicos/{id}`: edicao restrita ao prestador dono do servico (403 para qualquer outro usuario, inclusive ADMIN).
- `POST /servicos/{id}/encerrar`: encerramento permitido ao prestador dono **ou** a um ADMIN.
- `POST /contratacoes`: contratacao de um servico com o contratante obtido do token; proibida para servico proprio e para servico que nao esteja `ATIVO` (409 em ambos os casos).
- `GET /contratacoes/minhas`: lista as contratacoes feitas pelo usuario autenticado.
- Campos como `prestadorId`, `contratanteId` ou `papel` enviados no corpo da requisicao sao ignorados pelos DTOs de entrada — identidade e papel vem sempre do token.
- Usuario ADMIN de teste disponibilizado apenas em ambiente de desenvolvimento (veja a secao "ADMIN para testes locais" abaixo).

## Decisoes de escopo (contratacao)

O enunciado define as situacoes `SOLICITADA`, `ACEITA`, `CONCLUIDA` e `CANCELADA` para contratacoes, mas as funcionalidades obrigatorias listadas cobrem apenas a criacao da contratacao. Por isso, o CP4 implementa unicamente a criacao (sempre iniciando em `SOLICITADA`); endpoints para transicionar a contratacao entre os demais estados nao foram implementados por nao fazerem parte do escopo funcional exigido, evitando inventar um fluxo nao especificado.

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
| `JWT_SECRET`  | Chave de assinatura dos tokens JWT (use um valor proprio e secreto fora do ambiente local) | placeholder de dev |
| `JWT_EXPIRATION_MS` | Tempo de validade do token, em milissegundos | `3600000` (1h) |
| `SPRING_PROFILES_ACTIVE` | Profile ativo do Spring. Defina como `dev` para habilitar o seed do usuario ADMIN de teste | vazio |
| `ADMIN_EMAIL` | E-mail do usuario ADMIN de teste (so tem efeito com `SPRING_PROFILES_ACTIVE=dev`) | vazio |
| `ADMIN_PASSWORD` | Senha do usuario ADMIN de teste (so tem efeito com `SPRING_PROFILES_ACTIVE=dev`) | vazio |

## ADMIN para testes locais

O cadastro publico (`POST /usuarios/cadastro`) sempre cria usuarios com papel `USER` — nao ha como o cliente se promover a `ADMIN` pela API. Para testes locais que exigem um usuario `ADMIN`, defina no seu `.env`:

```
SPRING_PROFILES_ACTIVE=dev
ADMIN_EMAIL=admin@campusgigs.edu
ADMIN_PASSWORD=uma-senha-somente-local
```

Com essas variaveis definidas, o `AdminSeeder` (componente restrito ao profile `dev`) cria o usuario ADMIN automaticamente na inicializacao, caso ele ainda nao exista. Sem `SPRING_PROFILES_ACTIVE=dev`, esse componente nunca e executado e nenhum usuario ADMIN e criado — comportamento padrao para producao. Nenhuma senha real e versionada: o valor fica apenas no `.env` local (fora do controle de versao).

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

Retorna `200` com um token JWT e os dados do usuario autenticado quando as credenciais sao validas, ou `401` com mensagem generica quando invalidas (sem indicar se o problema foi o e-mail ou a senha).

### Endpoint protegido (identidade autenticada)

```
GET /usuarios/me
Authorization: Bearer <token>
```

Retorna `200` com os dados do usuario dono do token. Retorna `401` se o header `Authorization` estiver ausente, malformado, com token invalido ou expirado.

### Publicar servico

```
POST /servicos
Authorization: Bearer <token>
Content-Type: application/json

{
  "titulo": "Aula de Calculo",
  "descricao": "Reforco de calculo 1",
  "categoria": "Aulas",
  "preco": 50.00
}
```

Retorna `201`. O prestador e sempre o usuario autenticado.

### Editar servico

```
PUT /servicos/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "titulo": "Aula de Calculo Avancada",
  "descricao": "Reforco de calculo 1 e 2",
  "categoria": "Aulas",
  "preco": 60.00,
  "situacao": "ATIVO"
}
```

Retorna `200` se o usuario autenticado for o prestador do servico; `403` caso contrario. `situacao` aceita apenas `ATIVO` ou `PAUSADO` (para encerrar, use o endpoint dedicado abaixo).

### Encerrar servico

```
POST /servicos/{id}/encerrar
Authorization: Bearer <token>
```

Retorna `200` se o usuario autenticado for o prestador do servico **ou** tiver papel `ADMIN`; `403` caso contrario.

### Contratar servico

```
POST /contratacoes
Authorization: Bearer <token>
Content-Type: application/json

{
  "servicoId": 1
}
```

Retorna `201`. Retorna `409` se o servico nao estiver `ATIVO` ou se o usuario autenticado for o proprio prestador do servico.
