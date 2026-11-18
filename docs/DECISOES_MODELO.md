# Decisões de modelagem — SaneaTech

## Referência de escopo

A primeira entrega documentou registro de ocorrências com descrição, localização e acompanhamento de três estados. A segunda entrega representa esses conceitos sem expandir o MVP com chat, pagamentos, sensores ou autenticação.

## Entidades

- **Cidadão:** autor da solicitação. É persistido separadamente porque uma pessoa pode cadastrar vários problemas.
- **Equipe Responsável:** unidade encarregada de acompanhar ocorrências e registrar seu andamento.
- **Ocorrência:** entidade central, com protocolo único, categoria, descrição, localização, data, solicitante, equipe opcional e estado.
- **Histórico de Status:** registro de transições. O primeiro evento marca a criação no estado pendente; eventos seguintes guardam o responsável e a observação.
- **Endereço Ocorrência:** `@Embeddable`, não entidade, pois não faz sentido ter identidade ou repositório independente nesse MVP.

## Regras implementadas

1. É obrigatório haver solicitante, categoria, descrição e endereço para abrir uma ocorrência.
2. Toda ocorrência nasce `PENDENTE` e recebe protocolo único.
3. A equipe é opcional na abertura e obrigatória para iniciar atendimento.
4. Atribuição/re-atribuição, descrição e localização são modificáveis somente em `PENDENTE`.
5. O fluxo é `PENDENTE -> EM_ANDAMENTO -> CONCLUIDA`; não é permitido pular, repetir ou retroceder estados.
6. Toda transição requer observação e registra data e equipe responsável.
7. O histórico não pode ser editado diretamente por código consumidor.
8. Coleções de entidades são expostas sem setters e como vistas somente de leitura.

## Notas sobre JPA

- O lado dono da associação é a propriedade `@ManyToOne` em `Ocorrencia` ou `HistoricoStatus`, com `@JoinColumn` explícito.
- As coleções `@OneToMany` são inversas (`mappedBy`). O método `atribuirEquipe` atualiza as duas pontas em memória.
- `HistoricoStatus` é dependente do agregado `Ocorrencia`, portanto `cascade = ALL` e `orphanRemoval = true` são adequados nessa relação. Não se usa *cascade remove* de cidadão/equipe para ocorrência, protegendo registros históricos.
- Como não há `@ManyToMany`, o diagrama não inventa relações desse tipo.
- Instantes são armazenados em `Instant`. `EnumType.STRING` protege o banco contra reordenação das constantes enum.
- `@GeneratedValue(IDENTITY)` identifica registros no banco; o protocolo de negócio é independente do `id`.

## Restrições não cobertas nesta etapa

O domínio não define autorização por usuário autenticado: os métodos podem ser chamados por quem tem referência da entidade, pois a autenticação será responsabilidade de uma camada futura. A atribuição de equipe e a validade do endereço seriam controladas por futuros casos de uso; não há integração externa, Controller ou Service nesta entrega.
