# Segunda entrega — Backend Avançado P4.447

**Projeto:** SaneaTech — Tecnologia para o Saneamento Básico  
**Integrantes:** Eduardo Cabral; Sérgio Henrique; Leonardo Teixeira; Luca Magalhães; Luís Eduardo Montenegro.

## Artefatos preparados

- [x] Entidades `Cidadao`, `EquipeResponsavel`, `Ocorrencia`, `HistoricoStatus` anotadas com JPA
- [x] Atributos e tipos definidos e encapsulados
- [x] Relacionamentos `@OneToMany`, `@ManyToOne` e endereço `@Embeddable`
- [x] Métodos de domínio protegendo invariantes e transições
- [x] Diagrama UML com multiplicidades: `docs/diagrama-classes.png`
- [x] Arquivo-fonte UML: `docs/diagrama-classes.puml`
- [x] Justificativa da arquitetura monolítica em camadas na seção 5 do `README.md`
- [x] Configuração Java/Maven/H2 e instruções de execução
- [x] Testes de regras de domínio e persistência incluídos
- [ ] Repositório GitHub criado e código enviado **pela equipe**
- [ ] Os cinco membros convidados ao repositório **pela equipe**
- [ ] Link do repositório enviado no Google Sala de Aula **pela equipe**

## Texto pronto para colar no Google Sala de Aula

**Segunda Entrega — SaneaTech | Backend Avançado — P4.447**

Repositório GitHub: [COLE O LINK REAL AQUI]

Diagrama de classes UML: `docs/diagrama-classes.png` (anexo e disponível no repositório).

Arquitetura: monólito em camadas com domínio rico, justificado na seção **5 — Arquitetura escolhida** do `README.md`.

Desenvolvemos quatro entidades JPA com seus atributos e relacionamentos, objeto de valor `EnderecoOcorrencia`, estados e tipos enumerados, métodos de negócio para cadastro/atribuição/atendimento/conclusão de ocorrências e histórico de alterações de status. Mantivemos o escopo da primeira entrega do SaneaTech, sem implementação de Controllers, Services ou endpoints, conforme o enunciado desta etapa.

**Equipe:** Eduardo Cabral; Sérgio Henrique; Leonardo Teixeira; Luca Magalhães; Luís Eduardo Montenegro.

## Últimos passos

1. Criar repositório no GitHub e fazer upload dos arquivos, **mantendo a estrutura de diretórios**, ou usar `git` conforme o README.
2. Confirmar que `README.md`, `pom.xml`, `src/` e `docs/diagrama-classes.png` aparecem no repositório.
3. Convidar os demais integrantes do grupo para o GitHub, quando necessário.
4. Em computador com JDK 17+ e Maven, rodar `mvn clean test` e `mvn -q compile exec:java`.
5. Colar o link real no Google Sala de Aula e anexar `docs/diagrama-classes.png` se houver campo para isso.
