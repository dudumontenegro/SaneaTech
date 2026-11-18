# Registro de verificação da segunda entrega

**Data:** 18/11/2026  
**Projeto:** SaneaTech — Backend Avançado P4.447

## Verificações efetivamente realizadas

- **Compilação das classes Java:** passou por `javac --release 17`, usando implementações mínimas temporárias apenas para as interfaces/anotações de Jakarta Persistence e JUnit. Essa checagem detecta problemas de sintaxe e de tipos Java, **mas não substitui compilar com bibliotecas reais**.
- **Oito testes de regras do domínio:** aprovados por um executor local de reflexão e afirmações que reproduz as chamadas principais do JUnit. Cobertura: abertura e histórico inicial; fluxo completo de status; bloqueio sem equipe; bloqueio de transições inválidas; edição apenas pendente; associação bidirecional ao reatribuir equipe; validações de entrada/encapsulamento; rejeição de observação vazia.
- **Diagrama UML:** imagens PNG e SVG geradas do arquivo Graphviz `diagrama-classes.dot`; a fonte PlantUML `diagrama-classes.puml` acompanha o mesmo modelo conceitual.
- **Arquivos de configuração:** `pom.xml` e `persistence.xml` incluídos; dependências Maven apontam para versões publicadas.

## Validação que ainda precisa ser feita

Este ambiente de criação não disponibilizava o executável Maven nem acesso direto ao repositório remoto de dependências. Por esse motivo, **não foi possível executar** `mvn clean test`, o teste real de persistência Hibernate/H2 nem o comando `mvn exec:java` aqui. A equipe deve executá-los depois de importar o projeto no computador local com Java 17+ e Maven.

Comandos para conferência final:

```bash
mvn clean test
mvn -q compile exec:java
```

**Importante:** o teste `PersistenciaJpaTest.java` está implementado, mas a sua execução real não foi confirmada neste ambiente. Não declarar que o ciclo de persistência foi aprovado sem executar os comandos acima.
