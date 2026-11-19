# SaneaTech — Tecnologia para o Saneamento Básico

**Backend Avançado — P4.447 | Ciência da Computação | 3ª entrega: API REST, migrations e banco Neon**

**Equipe:** Eduardo Cabral · Sérgio Henrique · Leonardo Teixeira · Luca Magalhães · Luís Eduardo Montenegro.

## 1. Descrição

O SaneaTech é uma plataforma na qual **cidadãos registram ocorrências de saneamento** (vazamentos, esgoto a céu aberto, falta de abastecimento) e **equipes responsáveis acompanham e atualizam o status** `PENDENTE → EM_ANDAMENTO → CONCLUIDA`. Esta entrega expõe o domínio modelado na 2ª entrega por meio de uma API REST e passa a persistir os dados em PostgreSQL (Neon), com schema versionado por Flyway.

### Features desta entrega

| # | Feature (branch) | Responsável | Endpoints | Classes |
|---|---|---|---|---|
| 1 | `feature/cadastro-cidadaos` | Eduardo Cabral | `POST /cidadaos`, `GET /cidadaos` | `CidadaoController`, `CidadaoService`, `CidadaoRepository` |
| 2 | `feature/consulta-equipes` | Sérgio Henrique | `GET /equipes`, `GET /equipes/{id}` | `EquipeController`, `EquipeService`, `EquipeResponsavelRepository` |
| 3 | `feature/consulta-ocorrencias` | Leonardo Teixeira | `GET /ocorrencias` (`?status=` opcional), `GET /ocorrencias/{id}` | `OcorrenciaController`, `OcorrenciaService`, `OcorrenciaRepository` |
| 4 | `feature/cadastro-equipes` | Luca Magalhães | `POST /equipes` | `CadastroEquipeController`, `CadastroEquipeService` |
| 5 | `feature/atualizacao-status` | Luís Eduardo Montenegro | `PATCH /ocorrencias/{id}/status` | `AtualizacaoStatusController`, `AtualizacaoStatusService` |
| — | registro de ocorrência (entrega anterior) | — | `POST /ocorrencias` | `OcorrenciaController`, `OcorrenciaService` |

**Códigos de resposta:** `200/201` sucesso (o `201` traz o header `Location`); `400` corpo ou parâmetro inválido; `404` recurso inexistente; `409` e-mail de cidadão ou nome de equipe já cadastrado; `422` regra de negócio violada (transição de status inválida, iniciar sem equipe). Os erros seguem `ProblemDetail` (RFC 9457) e são tratados no `GlobalExceptionHandler`. Erros de validação trazem o campo `erros` com a mensagem de cada campo.

**Atualização de status:** só é permitido avançar uma etapa (`PENDENTE → EM_ANDAMENTO → CONCLUIDA`). Pular, retroceder e repetir o status são rejeitados. A `observacao` é obrigatória (até 300 caracteres). Cada mudança acrescenta um registro em `historicos_status` com a equipe responsável.

## 2. Tecnologias e execução

- **Java 17** e **Spring Boot 4.1.1** (Spring MVC, Data JPA, Validation)
- **Maven** via Maven Wrapper (`./mvnw`)
- **Lombok**
- **Flyway** (migrations em `src/main/resources/db/migration`)
- **PostgreSQL / Neon** em produção e **H2** (modo PostgreSQL) no perfil `local` e nos testes

### Perfis

| Perfil | Arquivo | Banco |
|---|---|---|
| `local` (padrão) | `application-local.properties` | H2 em memória, console em `/h2-console` |
| `prod` | `application-prod.properties` | Neon (PostgreSQL) |

O perfil ativo é definido em `application.properties` (`spring.profiles.active`). Nos dois perfis o Flyway cria o schema e o Hibernate apenas valida (`ddl-auto=validate`).

### Rodar localmente (H2)

```bash
./mvnw spring-boot:run
./mvnw test              # testes de domínio, serviço e controllers (MockMvc)
```

### Rodar com o Neon

1. No console do Neon, abra *Connection Details* e copie host, banco, usuário e senha.
2. Copie `src/main/resources/application-prod.properties.example` para `application-prod.properties` (esse arquivo está no `.gitignore` e não vai para o GitHub) e preencha:

   ```properties
   spring.datasource.url=jdbc:postgresql://ep-xxxx-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require
   spring.datasource.username=neondb_owner
   spring.datasource.password=<senha>
   ```

3. Em `application.properties`, troque para `spring.profiles.active=prod`.
4. Rode `./mvnw spring-boot:run`. Na primeira execução o Flyway aplica as migrations V1 a V5 no Neon e registra o histórico na tabela `flyway_schema_history`.

Alternativa local ao Neon: `docker compose -f docker/docker-compose.yml up -d` e `spring.datasource.url=jdbc:postgresql://localhost:5432/saneatech` (usuário e senha `postgres`).

### Migrations

| Versão | Arquivo | Conteúdo |
|---|---|---|
| V1 | `V1__create_table_cidadaos.sql` | `cidadaos` (e-mail único) |
| V2 | `V2__create_table_equipes_responsaveis.sql` | `equipes_responsaveis` (nome único) |
| V3 | `V3__create_table_ocorrencias.sql` | `ocorrencias` (FKs para cidadão e equipe, CHECK de tipo e status, índices) |
| V4 | `V4__create_table_historicos_status.sql` | `historicos_status` (FK para ocorrência e equipe) |
| V5 | `V5__insert_dados_iniciais.sql` | 1 cidadão, 2 equipes, 4 ocorrências `SAN-SEED-0001..0004` e o histórico delas |

Uma migration já aplicada **nunca deve ser editada**. Qualquer alteração de schema entra como um novo arquivo `V6__...sql`.

### Testar as rotas no Insomnia

Importe `docs/insomnia/saneatech-insomnia.json` (*Import → From File*). A collection tem uma pasta por feature, com os casos de sucesso (✅) e de erro (❌) e o status esperado no nome. O ambiente `Local` define `base_url` e os IDs do seed. Na pasta de atualização de status, execute as requisições na ordem: os PATCH de sucesso alteram o banco.

## 3. Entidades, atributos e relacionamentos

| Entidade | Atributos de negócio e tipos principais | Responsabilidade |
|---|---|---|
| `Cidadao` | `id: Long`, `nome: String`, `email: String`, `telefone: String`, `ocorrencias: List<Ocorrencia>` | Origem das solicitações; contato e identificação |
| `EquipeResponsavel` | `id: Long`, `nome: String`, `areaAtuacao: String`, `ocorrencias: List<Ocorrencia>`, `alteracoesRegistradas: List<HistoricoStatus>` | Equipe que atende e registra alterações |
| `Ocorrencia` | `id: Long`, `protocolo: String`, `tipo: TipoOcorrencia`, `descricao: String`, `status: StatusOcorrencia`, `criadoEm: Instant`, `atualizadoEm: Instant`, `endereco: EnderecoOcorrencia`, `solicitante: Cidadao`, `equipeResponsavel: EquipeResponsavel`, `historico: List<HistoricoStatus>` | Raiz do agregado; registro, localização, atribuição e transições |
| `HistoricoStatus` | `id: Long`, `statusAnterior: StatusOcorrencia?`, `statusNovo: StatusOcorrencia`, `observacao: String`, `registradoEm: Instant`, `ocorrencia: Ocorrencia`, `equipeResponsavel: EquipeResponsavel?` | Trilha auditável de eventos de status |
| `EnderecoOcorrencia` | `logradouro`, `numero`, `bairro`, `cidade`, `uf`, `referencia`: `String` | **Objeto de valor `@Embeddable`**, não entidade independente |

`TipoOcorrencia`: `VAZAMENTO_AGUA`, `ESGOTO_CEU_ABERTO`, `FALTA_ABASTECIMENTO`, `OUTROS`.

`StatusOcorrencia`: `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`.

### Cardinalidades e JPA

| Relacionamento | Multiplicidade | Mapeamento Java |
|---|---|---|
| `Cidadao` → `Ocorrencia` | 1 cidadão → 0..N ocorrências; cada ocorrência → 1 cidadão | `@OneToMany(mappedBy = "solicitante")` / `@ManyToOne(optional = false)` |
| `EquipeResponsavel` → `Ocorrencia` | 1 equipe → 0..N ocorrências; cada ocorrência → 0..1 equipe | `@OneToMany(mappedBy = "equipeResponsavel")` / `@ManyToOne` |
| `Ocorrencia` → `HistoricoStatus` | 1 ocorrência → 1..N registros no modelo de domínio; cada registro → 1 ocorrência | `@OneToMany(mappedBy = "ocorrencia", cascade = ALL, orphanRemoval = true)` / `@ManyToOne(optional = false)` |
| `EquipeResponsavel` → `HistoricoStatus` | 1 equipe → 0..N alterações; cada alteração → 0..1 equipe | `@OneToMany(mappedBy = "equipeResponsavel")` / `@ManyToOne` |
| `Ocorrencia` → `EnderecoOcorrencia` | 1 → 1 | `@Embedded` / `@Embeddable` e `@AttributeOverrides` |

**Justificativas dos relacionamentos:** não há `@ManyToMany` porque cada ocorrência pertence a um único cidadão e tem no máximo uma equipe responsável em cada instante. Não há `@OneToOne` porque o endereço é um **objeto de valor embutido**, sem identidade ou ciclo de vida independente. O `HistoricoStatus` usa entidade própria porque o registro de mudanças precisa ser preservado e consultável. O `@ManyToOne` é o **lado proprietário** das relações bidirecionais; o outro lado usa `mappedBy`. A coleção do histórico usa *cascade* porque seus registros têm o mesmo ciclo de persistência da ocorrência.

## 4. Modificadores de estado, encapsulamento e regras

| Classe | Operações do domínio | Regras e propósito |
|---|---|---|
| `Cidadao` | `corrigirNome`, `atualizarContato` | Não aceita nome/e-mail vazio nem e-mail inválido |
| `EquipeResponsavel` | `renomear`, `alterarAreaAtuacao` | Exige informações textuais válidas |
| `Ocorrencia` | `abrir`, `atribuirEquipe`, `atualizarDescricao`, `corrigirLocalizacao`, `iniciarAtendimento`, `concluirAtendimento` | Só permite atualizações de conteúdo e atribuição enquanto pendente; exige equipe para iniciar; exige `EM_ANDAMENTO` para concluir |
| `HistoricoStatus` | Nenhum modificador público | Eventos são criados pela ocorrência e não alterados após sua criação |
| `EnderecoOcorrencia` | Nenhum modificador público | Valor substituído integralmente por `corrigirLocalizacao` |

As classes possuem construtor sem argumentos `protected` exigido pelo JPA. Não existem `setStatus`, `setEquipe` nem setters genéricos para mutações significativas. Campos mutáveis são privados; coleções expostas por getters são **não modificáveis**. Um evento inicial `PENDENTE` é criado ao abrir a ocorrência, e cada transição de estado acrescenta um evento ao histórico. Alterações de descrição e endereço não são alterações de status e **não** geram esse tipo de evento.

**Estados permitidos:**

```text
ABERTURA -> PENDENTE -> EM_ANDAMENTO -> CONCLUIDA
                         (equipe exigida)
```

As datas usam `Instant` para representar instantes absolutos, independentes do fuso do servidor. O protocolo é gerado automaticamente como `SAN-<UUID>` e possui índice de unicidade na tabela. O banco também garante unicidade do e-mail do cidadão; a regra de e-mail duplicado entre registros diferentes depende dessa restrição de persistência nesta etapa (o `CidadaoService` também verifica o e-mail antes de salvar, retornando 409).

### Exemplo de uso sem API

```java
Cidadao cidadao = new Cidadao("Ana", "ana@exemplo.com", "83999999999");
EquipeResponsavel equipe = new EquipeResponsavel("Rede de Água", "Manutenção");
EnderecoOcorrencia endereco = new EnderecoOcorrencia(
    "Rua A", "123", "Centro", "João Pessoa", "PB", "Perto da praça");
Ocorrencia o = Ocorrencia.abrir(cidadao, TipoOcorrencia.VAZAMENTO_AGUA,
    "Vazamento em via pública", endereco);
o.atribuirEquipe(equipe);
o.iniciarAtendimento("Equipe em deslocamento");
o.concluirAtendimento("Reparo finalizado");
```

> Na API, as entidades seguem o padrão `XEntity` (`CidadaoEntity`, `EquipeResponsavelEntity`, `OcorrenciaEntity`, `HistoricoStatusEntity`), mas mantêm os mesmos atributos, relacionamentos e regras descritos acima.

## 5. Arquitetura: monólito modular organizado por feature

O projeto continua sendo um **monólito**, agora organizado por **feature** (`internal/<feature>`) com camadas internas (`controller`, `dto`, `entity`, `mapper`, `repository`, `service`), no mesmo padrão do projeto de referência `assistencia-uniesp`. As regras de negócio continuam nas entidades (domínio rico). Os services orquestram os casos de uso, os mappers convertem entre entidade e DTO, e o tratamento de erros fica centralizado em `config/exception/GlobalExceptionHandler`.

Quando uma feature precisa de dados de outra, ela usa o service da outra feature, e não o repositório dela. Exemplo: `OcorrenciaService` chama `CidadaoService.buscarEntidadePorId`.

## 6. Organização do código

```text
SaneaTech/
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/wrapper/
├── docker/docker-compose.yml          # Postgres local opcional
├── docs/
│   ├── insomnia/saneatech-insomnia.json
│   └── diagrama-classes.*, ENTREGA.md, DECISOES_MODELO.md, VALIDACAO.md
└── src/
    ├── main/
    │   ├── java/br/edu/p447/saneatech/
    │   │   ├── SaneaTechApplication.java
    │   │   ├── config/exception/GlobalExceptionHandler.java
    │   │   ├── internal/
    │   │   │   ├── cidadao/      controller · dto · entity · mapper · repository · service
    │   │   │   ├── equipe/       controller · dto · entity · mapper · repository · service
    │   │   │   └── ocorrencia/   controller · dto · entity · mapper · repository · service
    │   │   └── shared/
    │   │       ├── exception/    RecursoNaoEncontrado, RegraNegocio, RecursoDuplicado
    │   │       └── util/         Validacao
    │   └── resources/
    │       ├── application.properties, application-local.properties, application-prod.properties
    │       └── db/migration/     V1..V5
    └── test/java/br/edu/p447/saneatech/
        ├── SaneaTechApplicationTests.java     # sobe o contexto, aplica e valida as migrations
        └── internal/{cidadao,equipe,ocorrencia}/
```

## 7. Diagrama de classes UML

Veja [`docs/diagrama-classes.png`](docs/diagrama-classes.png), [`docs/diagrama-classes.svg`](docs/diagrama-classes.svg) e a fonte editável [`docs/diagrama-classes.puml`](docs/diagrama-classes.puml). O diagrama identifica entidades, atributos/tipos, métodos de domínio e multiplicidades. O `EnderecoOcorrencia` está indicado como `«Embeddable»` e os enums são declarados; as mesmas relações estão implementadas por JPA.

![Diagrama UML do SaneaTech](docs/diagrama-classes.png)

Para editar o diagrama, abra `docs/diagrama-classes.puml` em uma ferramenta compatível com PlantUML. Também há `docs/diagrama-classes.dot` para regenerar as imagens usando Graphviz (`dot -Tpng ...` e `dot -Tsvg ...`).

---

**Trabalho elaborado por:** Eduardo Cabral; Sérgio Henrique; Leonardo Teixeira; Luca Magalhães; Luís Eduardo Montenegro.  
**Disciplina:** Backend Avançado — P4.447 — Ciência da Computação.
