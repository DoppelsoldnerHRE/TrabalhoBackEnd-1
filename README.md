
# Trabalho backend

Tema: Internet of things(IoT), Monitor inteligente IOT (SmartMonitor).

Yuri Cardoso Maciel,Jéssica Larzen Viana.


## 🛰️ Descrição do Projeto

O projeto consiste em uma API para armazenamento e análise de dados coletados por dispositivos IoT, como o ESP32.
Os dispositivos são responsáveis por capturar informações ambientais — por exemplo, temperatura e umidade — e enviá-las para a API, que realiza o armazenamento, catalogação e monitoramento dos dados coletados.
Será realizado uma comparação entre a efetividade os dois micro-controladores mais populares, para determinar qual seria mais eficaz para a analise de dados por uma larga escala de tempo.

Além da coleta em si, o sistema também realiza uma análise de períodos de inatividade dos dispositivos, permitindo avaliar sua confiabilidade e eficiência no contexto de coleta contínua em larga escala.

## 🎯 Objetivo

O principal objetivo do projeto é avaliar a viabilidade do uso de dispositivos IoT de baixo custo, como o ESP32, para coleta massiva de dados.
Embora esses dispositivos sejam acessíveis economicamente, eles apresentam limitações em performance, estabilidade e disponibilidade. Assim, o estudo busca mensurar o impacto dessas limitações e determinar se o uso do ESP32 é adequado para aplicações de coleta de dados em larga escala.

ESP32, arduino UNO, Arduino IDE, Java SpringBoot, H2, Java 17+, Spring Data.

## 🧱 Entidade: Device (Dispositivo)

Representa um dispositivo IoT cadastrado no sistema.

| Campo          | Tipo          | Descrição                                     |
| -------------- | ------------- | --------------------------------------------- |
| `id`           | Long          | Identificador único do dispositivo            |
| `name`         | String        | Nome ou apelido do dispositivo                |
| `location`     | String        | Localização física                            |
| `lastActivity` | LocalDateTime | Data/hora da última comunicação               |
| `status`       | String        | Indica se o dispositivo está ativo ou inativo |
🔗 Relação: Um Device pode ter várias Reading associadas (1:N).

## 🌡️ Entidade: Reading (Leitura de Sensor)

Representa uma leitura de dados enviada por um dispositivo IoT.

| Campo         | Tipo          | Descrição                        |
| ------------- | ------------- | -------------------------------- |
| `id`          | Long          | Identificador da leitura         |
| `device`      | Device        | Dispositivo que enviou a leitura |
| `temperature` | Double        | Temperatura medida               |
| `humidity`    | Double        | Umidade medida                   |
| `timestamp`   | LocalDateTime | Data/hora da coleta              |
🔗 Relação: Cada leitura pertence a um único Device.

## 📊 InactivityLog (Log de Inatividade)

Registra períodos em que um dispositivo ficou sem enviar dados.

| Campo             | Tipo          | Descrição                         |
| ----------------- | ------------- | --------------------------------- |
| `id`              | Long          | Identificador do log              |
| `device`          | Device        | Dispositivo que ficou inativo     |
| `startTime`       | LocalDateTime | Início da inatividade             |
| `endTime`         | LocalDateTime | Fim da inatividade                |
| `durationMinutes` | Long          | Duração da inatividade em minutos |


# 📖 Descrição da Carta-Desafio

A carta "Inatividade" exige que uma das entidades possua um atributo que registre a última atualização, e que exista uma rota capaz de listar todos os registros inativos por mais de uma semana.

No contexto deste projeto IoT, a funcionalidade foi implementada na entidade Device, representando dispositivos IoT que enviam dados periodicamente (como ESP32). Assim, o sistema consegue identificar quais dispositivos estão inativos há mais de 7 dias, o que permite avaliar sua estabilidade e confiabilidade.

## 🧱 Entidade Afetada: Device

A entidade Device foi estendida para incluir o campo `lastActivity`, que indica a data e hora da última leitura recebida.

```java
@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private LocalDateTime lastActivity;
    private String status;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<Reading> readings;
}
```

## 🔁 Lógica de Atualização de Atividade

Sempre que uma nova leitura (Reading) é recebida pela rota `POST /readings`, o sistema automaticamente atualiza o campo `lastActivity` do dispositivo correspondente.

```java
device.setLastActivity(LocalDateTime.now());
device.setStatus("ATIVO");
deviceRepository.save(device);
```

Essa atualização garante que o último contato do dispositivo com a API seja sempre registrado.

## ⏱️ Detecção de Inatividade

Um serviço (DeviceService) foi criado para detectar dispositivos sem atividade há mais de 7 dias. A lógica realiza uma consulta no banco de dados comparando o `lastActivity` com a data atual:

```java
public List<Device> getInactiveDevices() {
    LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
    return deviceRepository.findByLastActivityBefore(sevenDaysAgo);
}
```

## 🌐 Nova Rota: /devices/inativos

Uma nova rota foi criada para permitir que o usuário visualize todos os dispositivos inativos.

### 📍 Endpoint

| Método | Rota                | Descrição                                                    |
|--------|---------------------|--------------------------------------------------------------|
| GET    | /devices/inativos   | Retorna todos os dispositivos que não enviam dados há mais de 7 dias |

### 📤 Exemplo de Requisição

```http
GET /devices/inativos HTTP/1.1
Host: localhost:8080
```

### 📥 Exemplo de Resposta (200 OK)

```json
[
  {
    "id": 1,
    "name": "ESP32-LAB1",
    "location": "Laboratório 1",
    "lastActivity": "2025-10-05T14:23:00",
    "status": "INATIVO"
  },
  {
    "id": 3,
    "name": "ESP32-EXT01",
    "location": "Área Externa",
    "lastActivity": "2025-10-01T08:12:45",
    "status": "INATIVO"
  }
]
```

## ⚙️ Regras e Critérios

- Um dispositivo é considerado inativo se `lastActivity` for anterior a 7 dias da data atual.
- Dispositivos sem leituras registradas também são marcados como inativos.
- A atualização de status é feita automaticamente quando uma nova leitura é registrada.
- O status pode assumir os valores:
  - **"ATIVO"** — dispositivo enviou dados recentemente;
  - **"INATIVO"** — sem comunicação há mais de 7 dias.

## 📊 Impacto na Análise do Projeto

Essa funcionalidade é essencial para o objetivo principal do projeto, que é avaliar a viabilidade de uso de dispositivos IoT de baixo custo (como o ESP32) em ambientes de coleta de dados contínua.

Com o monitoramento de inatividade:

- É possível identificar falhas de conexão ou instabilidade do hardware;
- A API fornece métricas confiáveis sobre tempo médio de atividade e intervalos de falha;
- O sistema se torna uma base sólida para estudos sobre eficiência operacional de redes IoT.

## ✅ Códigos HTTP Utilizados

| Código | Significado              | Uso                                                    |
|--------|--------------------------|--------------------------------------------------------|
| 200    | OK                       | Lista de dispositivos inativos retornada com sucesso   |
| 404    | Not Found                | Nenhum dispositivo inativo encontrado                  |
| 500    | Internal Server Error    | Problemas de conexão ou lógica de negócio              |

## 🧩 Conclusão

A carta-desafio "Inatividade" foi completamente implementada e integrada à entidade Device, permitindo à API:

- Monitorar automaticamente a atividade dos dispositivos IoT;
- Detectar e exibir períodos de inatividade;
- Fornecer informações essenciais para a análise de viabilidade e estabilidade dos dispositivos ESP32 em ambientes reais.


