
Tema: Internet of things(IoT), Monitor inteligente IOT (SmartMonitor).


Yuri Cardoso Maciel,Jéssica Larzen Viana.


🛰️ Descrição do Projeto

O projeto consiste em uma API para armazenamento e análise de dados coletados por dispositivos IoT, como o ESP32.
Os dispositivos são responsáveis por capturar informações ambientais — por exemplo, temperatura e umidade — e enviá-las para a API, que realiza o armazenamento, catalogação e monitoramento dos dados coletados.
Será realizado uma comparação entre a efetividade os dois micro-controladores mais populares, para determinar qual seria mais eficaz para a analise de dados por uma larga escala de tempo.

Além da coleta em si, o sistema também realiza uma análise de períodos de inatividade dos dispositivos, permitindo avaliar sua confiabilidade e eficiência no contexto de coleta contínua em larga escala.

🎯 Objetivo

O principal objetivo do projeto é avaliar a viabilidade do uso de dispositivos IoT de baixo custo, como o ESP32, para coleta massiva de dados.
Embora esses dispositivos sejam acessíveis economicamente, eles apresentam limitações em performance, estabilidade e disponibilidade. Assim, o estudo busca mensurar o impacto dessas limitações e determinar se o uso do ESP32 é adequado para aplicações de coleta de dados em larga escala.

ESP32, arduino UNO, Arduino IDE, Java SpringBoot, H2, Java 17+, Spring Data.

🧱 Entidade: Device (Dispositivo)

Representa um dispositivo IoT cadastrado no sistema.

| Campo          | Tipo          | Descrição                                     |
| -------------- | ------------- | --------------------------------------------- |
| `id`           | Long          | Identificador único do dispositivo            |
| `name`         | String        | Nome ou apelido do dispositivo                |
| `location`     | String        | Localização física                            |
| `lastActivity` | LocalDateTime | Data/hora da última comunicação               |
| `status`       | String        | Indica se o dispositivo está ativo ou inativo |
🔗 Relação: Um Device pode ter várias Reading associadas (1:N).

🌡️ Entidade: Reading (Leitura de Sensor)

Representa uma leitura de dados enviada por um dispositivo IoT.

| Campo         | Tipo          | Descrição                        |
| ------------- | ------------- | -------------------------------- |
| `id`          | Long          | Identificador da leitura         |
| `device`      | Device        | Dispositivo que enviou a leitura |
| `temperature` | Double        | Temperatura medida               |
| `humidity`    | Double        | Umidade medida                   |
| `timestamp`   | LocalDateTime | Data/hora da coleta              |
🔗 Relação: Cada leitura pertence a um único Device.

📊 InactivityLog (Log de Inatividade)

Registra períodos em que um dispositivo ficou sem enviar dados.

| Campo             | Tipo          | Descrição                         |
| ----------------- | ------------- | --------------------------------- |
| `id`              | Long          | Identificador do log              |
| `device`          | Device        | Dispositivo que ficou inativo     |
| `startTime`       | LocalDateTime | Início da inatividade             |
| `endTime`         | LocalDateTime | Fim da inatividade                |
| `durationMinutes` | Long          | Duração da inatividade em minutos |





