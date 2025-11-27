# 🛰️ Sistema de Gerenciamento de Dispositivos IoT - SmartMonitor

## 👥 Integrantes do Grupo
- **Yuri Cardoso Maciel**
- **Jéssica Larzen Viana**

---

## 📋 Descrição do Projeto

O **SmartMonitor** é uma API REST completa para armazenamento, catalogação e análise de dados coletados por dispositivos IoT, como o ESP32 e Arduino UNO. O sistema foi desenvolvido para monitorar ambientes inteligentes, capturando informações como temperatura e umidade, além de avaliar a confiabilidade e eficiência dos dispositivos através do monitoramento de períodos de inatividade.

### 🎯 Objetivo Principal

Avaliar a **viabilidade do uso de dispositivos IoT de baixo custo** (ESP32, Arduino) para coleta massiva de dados em larga escala. Embora economicamente acessíveis, esses dispositivos apresentam limitações em performance, estabilidade e disponibilidade. O projeto busca mensurar o impacto dessas limitações e determinar sua adequação para aplicações de monitoramento contínuo.

---

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17+**
- **Spring Boot 4.0.0**
- **Spring Data JPA**
- **Bean Validation**
- **Lombok**

### Banco de Dados
- **PostgreSQL 16**
- **Hibernate ORM 7.1.8**

### Documentação
- **SpringDoc OpenAPI (Swagger)**

### Ferramentas
- **Maven**
- **Docker & Docker Compose**
- **Git**

### Hardware IoT (Integração Futura)
- **ESP32**
- **Arduino UNO**
- **Arduino IDE**

---

## 🏗️ Arquitetura do Sistema

O projeto segue uma arquitetura em camadas com separação clara de responsabilidades:
```
┌─────────────────────────────────────┐
│        Controllers (REST)           │  ← Endpoints HTTP
├─────────────────────────────────────┤
│           Services                  │  ← Lógica de Negócio
├─────────────────────────────────────┤
│         Repositories                │  ← Acesso ao Banco
├─────────────────────────────────────┤
│      Entities (JPA/Hibernate)       │  ← Modelo de Dados
└─────────────────────────────────────┘
              ↓
      ┌──────────────┐
      │  PostgreSQL  │
      └──────────────┘
```

---

## 📦 Entidades do Sistema

### 1. 👤 Usuario
Representa os proprietários e gerenciadores dos dispositivos IoT.

| Campo | Tipo | Descrição | Restrições |
|-------|------|-----------|------------|
| `id` | Long | Identificador único | PK, Auto-increment |
| `nome` | String | Nome completo do usuário | Obrigatório, máx. 100 caracteres |
| `email` | String | Email para login | Obrigatório, único, formato válido |
| `senha` | String | Senha criptografada | Obrigatório, mín. 8 caracteres |
| `dataCriacao` | LocalDateTime | Data de cadastro | Auto-gerado |

**Relacionamentos:**
- Um usuário pode ter vários dispositivos (1:N com Dispositivo)

---

### 2. 📱 Dispositivo
Representa um dispositivo IoT físico (ESP32, Arduino, etc.).

| Campo | Tipo | Descrição | Restrições |
|-------|------|-----------|------------|
| `id` | Long | Identificador único | PK, Auto-increment |
| `nome` | String | Nome identificador | Obrigatório, máx. 100 caracteres |
| `tipo` | String | Categoria do dispositivo | Obrigatório |
| `localizacao` | String | Local de instalação | Obrigatório, máx. 200 caracteres |
| `status` | Enum | Estado atual | ATIVO, INATIVO, MANUTENCAO |
| `enderecoMac` | String | Endereço MAC | Obrigatório, único, formato XX:XX:XX:XX:XX:XX |
| `dataCadastro` | LocalDateTime | Data de cadastro | Auto-gerado |
| `ultimaAtualizacao` | LocalDateTime | ⚠️ Última atualização | Auto-atualizado (CARTA-DESAFIO) |
| `usuarioId` | Long | ID do usuário proprietário | FK, Obrigatório |

**Relacionamentos:**
- Pertence a um usuário (N:1 com Usuario)
- Possui vários sensores (1:N com Sensor)

---

### 3. 📡 Sensor
Componentes de medição acoplados aos dispositivos.

| Campo | Tipo | Descrição | Restrições |
|-------|------|-----------|------------|
| `id` | Long | Identificador único | PK, Auto-increment |
| `nome` | String | Nome identificador | Obrigatório, máx. 100 caracteres |
| `tipoSensor` | String | Tipo de medição | Obrigatório (ex: "temperatura", "umidade") |
| `unidadeMedida` | String | Unidade | Obrigatório (ex: "°C", "%") |
| `limiteMinimo` | Double | Valor mínimo aceitável | Opcional |
| `limiteMaximo` | Double | Valor máximo aceitável | Opcional |
| `ativo` | Boolean | Status operacional | Padrão: true |
| `ultimaAtualizacao` | LocalDateTime | ⚠️ Última atualização | Auto-atualizado (CARTA-DESAFIO) |
| `dispositivoId` | Long | ID do dispositivo | FK, Obrigatório |

**Relacionamentos:**
- Pertence a um dispositivo (N:1 com Dispositivo)
- Gera várias leituras (1:N com Leitura)

---

### 4. 🌡️ Leitura
Dados coletados pelos sensores ao longo do tempo.

| Campo | Tipo | Descrição | Restrições |
|-------|------|-----------|------------|
| `id` | Long | Identificador único | PK, Auto-increment |
| `valor` | Double | Valor medido | Obrigatório |
| `dataHora` | LocalDateTime | Momento da coleta | Auto-gerado |
| `alerta` | Boolean | Indica se ultrapassou limites | Auto-calculado |
| `sensorId` | Long | ID do sensor | FK, Obrigatório |

**Relacionamentos:**
- Pertence a um sensor (N:1 com Sensor)

**Regras de Negócio:**
- Alerta é `true` quando o valor está fora dos limites configurados no sensor
- Ao registrar leitura, o `ultimaAtualizacao` do sensor é atualizado automaticamente

---

## 🔗 Diagrama de Relacionamentos
```
┌──────────────┐
│   Usuario    │
│   (1)        │
└──────┬───────┘
       │
       │ 1:N
       │
┌──────▼───────────┐
│   Dispositivo    │
│   (N)            │
│ ultimaAtualizacao│ ⚠️ CARTA-DESAFIO
└──────┬───────────┘
       │
       │ 1:N
       │
┌──────▼───────────┐
│     Sensor       │
│   (N)            │
│ ultimaAtualizacao│ ⚠️ CARTA-DESAFIO
└──────┬───────────┘
       │
       │ 1:N
       │
┌──────▼───────────┐
│    Leitura       │
│   (N)            │
└──────────────────┘
```

---

## 🎯 Carta-Desafio: Monitoramento de Inatividade

### 📖 Descrição

A carta-desafio **"Inativos"** exige que entidades possuam um atributo de última atualização e uma rota capaz de listar registros inativos por mais de **7 dias**.

### 🛠️ Implementação

#### 1. Atributo `ultimaAtualizacao`

Implementado nas entidades **Dispositivo** e **Sensor** usando `@UpdateTimestamp`:
```java
@UpdateTimestamp
@Column(nullable = false)
private LocalDateTime ultimaAtualizacao;
```

**Comportamento:**
- Atualizado automaticamente a cada modificação da entidade
- Para sensores: também atualizado ao registrar novas leituras
- Permite calcular o tempo desde a última atividade

#### 2. Lógica de Detecção

Queries JPQL nos repositories para buscar entidades inativas:
```java
@Query("SELECT d FROM Dispositivo d WHERE d.ultimaAtualizacao < :dataLimite")
Page<Dispositivo> findInativos(@Param("dataLimite") LocalDateTime dataLimite, Pageable pageable);
```

**Critério:** `dataLimite = LocalDateTime.now().minusDays(7)`

#### 3. Rotas Implementadas

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/dispositivos/inativos` | Lista dispositivos sem atualização há 7+ dias |
| GET | `/api/dispositivos/inativos/count` | Conta dispositivos inativos |
| GET | `/api/sensores/inativos` | Lista sensores sem atualização há 7+ dias |
| GET | `/api/sensores/inativos/count` | Conta sensores inativos |

#### 4. Exemplo de Resposta

**GET** `/api/dispositivos/inativos`
```json
{
  "content": [
    {
      "id": 15,
      "nome": "ESP32-LAB1",
      "tipo": "DISPOSITIVO",
      "localizacao": "Laboratório 1",
      "ultimaAtualizacao": "2025-11-10T14:23:00",
      "diasInativo": 16
    }
  ],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

### 📊 Impacto no Objetivo do Projeto

O monitoramento de inatividade é **essencial** para avaliar a viabilidade de dispositivos IoT de baixo custo:

✅ **Identifica falhas de conexão** - Detecta dispositivos com problemas de comunicação  
✅ **Métricas de confiabilidade** - Fornece dados sobre tempo médio de atividade  
✅ **Avaliação de estabilidade** - Permite comparar performance entre ESP32 e Arduino  
✅ **Manutenção proativa** - Alerta sobre dispositivos que precisam de atenção

---

## 🌐 API - Endpoints Disponíveis

### 👤 Usuários (`/api/usuarios`)

| Método | Rota | Descrição | Códigos HTTP |
|--------|------|-----------|--------------|
| POST | `/api/usuarios` | Criar usuário | 201, 400 |
| GET | `/api/usuarios` | Listar todos | 200 |
| GET | `/api/usuarios/{id}` | Buscar por ID | 200, 404 |
| GET | `/api/usuarios?nome={nome}` | Filtrar por nome | 200 |
| PUT | `/api/usuarios/{id}` | Atualizar | 200, 400, 404 |
| DELETE | `/api/usuarios/{id}` | Deletar | 204, 404, 409 |

---

### 📱 Dispositivos (`/api/dispositivos`)

| Método | Rota | Descrição | Códigos HTTP |
|--------|------|-----------|--------------|
| POST | `/api/dispositivos` | Criar dispositivo | 201, 400 |
| GET | `/api/dispositivos` | Listar todos | 200 |
| GET | `/api/dispositivos/{id}` | Buscar por ID | 200, 404 |
| GET | `/api/dispositivos?filtros` | Filtrar (nome, tipo, status, etc) | 200 |
| GET | `/api/dispositivos/inativos` | ⚠️ **Listar inativos (7+ dias)** | 200 |
| GET | `/api/dispositivos/inativos/count` | ⚠️ **Contar inativos** | 200 |
| PUT | `/api/dispositivos/{id}` | Atualizar | 200, 400, 404 |
| DELETE | `/api/dispositivos/{id}` | Deletar | 204, 404 |

**Filtros disponíveis:**
- `nome` - Busca parcial (case-insensitive)
- `tipo` - Filtrar por tipo
- `localizacao` - Filtrar por localização
- `status` - ATIVO, INATIVO, MANUTENCAO
- `usuarioId` - Dispositivos de um usuário específico
- `page`, `size`, `sort` - Paginação e ordenação

---

### 📡 Sensores (`/api/sensores`)

| Método | Rota | Descrição | Códigos HTTP |
|--------|------|-----------|--------------|
| POST | `/api/sensores` | Criar sensor | 201, 400 |
| GET | `/api/sensores` | Listar todos | 200 |
| GET | `/api/sensores/{id}` | Buscar por ID | 200, 404 |
| GET | `/api/sensores?filtros` | Filtrar (tipo, dispositivo, ativo) | 200 |
| GET | `/api/sensores/inativos` | ⚠️ **Listar inativos (7+ dias)** | 200 |
| GET | `/api/sensores/inativos/count` | ⚠️ **Contar inativos** | 200 |
| PUT | `/api/sensores/{id}` | Atualizar | 200, 400, 404 |
| DELETE | `/api/sensores/{id}` | Deletar | 204, 404 |

---

### 🌡️ Leituras (`/api/leituras`)

| Método | Rota | Descrição | Códigos HTTP |
|--------|------|-----------|--------------|
| POST | `/api/leituras` | Registrar leitura | 201, 400 |
| GET | `/api/leituras` | Listar todas | 200 |
| GET | `/api/leituras/{id}` | Buscar por ID | 200, 404 |
| GET | `/api/leituras?filtros` | Filtrar (sensor, período, alerta) | 200 |
| GET | `/api/leituras/sensor/{id}/ultimas` | Últimas leituras de um sensor | 200 |
| GET | `/api/leituras/sensor/{id}/estatisticas` | Estatísticas (média, min, max) | 200 |
| DELETE | `/api/leituras/{id}` | Deletar | 204, 404 |

---

## 📊 Recursos Implementados

### ✅ Requisitos Obrigatórios

- ✅ Serviço backend REST completo
- ✅ Arquitetura em camadas (Controller, Service, Repository)
- ✅ 4 entidades com relacionamentos (1:N, N:N)
- ✅ CRUD completo para todas as entidades
- ✅ Paginação e ordenação em todas as listagens
- ✅ Filtros de busca por múltiplos critérios
- ✅ DTOs para entrada e saída
- ✅ Validação de dados com Bean Validation
- ✅ Códigos HTTP apropriados
- ✅ Persistência com PostgreSQL
- ✅ Exception Handler global

### ⚠️ Carta-Desafio: Inativos

- ✅ Atributo `ultimaAtualizacao` em Dispositivo e Sensor
- ✅ Rotas `/inativos` para ambas as entidades
- ✅ Cálculo de dias de inatividade
- ✅ Query JPQL personalizada
- ✅ Paginação e ordenação dos inativos

### 🌟 Requisitos Extras

- ✅ PostgreSQL (banco diferente do H2)
- ✅ Swagger/OpenAPI (documentação automática)
- ⚠️ DTOs bem estruturados
- ⚠️ Exception handling robusto

---

## 📋 Exemplos de Requisições

### 1. Criar Usuário
```http
POST /api/usuarios
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha12345"
}
```

**Resposta (201 Created):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "dataCriacao": "2025-11-26T10:30:00"
}
```

---

### 2. Criar Dispositivo IoT
```http
POST /api/dispositivos
Content-Type: application/json

{
  "nome": "ESP32-LAB1",
  "tipo": "ESP32",
  "localizacao": "Laboratório 1",
  "status": "ATIVO",
  "enderecoMac": "AA:BB:CC:DD:EE:FF",
  "usuarioId": 1
}
```

---

### 3. Criar Sensor de Temperatura
```http
POST /api/sensores
Content-Type: application/json

{
  "nome": "Sensor Temperatura",
  "tipoSensor": "temperatura",
  "unidadeMedida": "°C",
  "limiteMinimo": 15.0,
  "limiteMaximo": 30.0,
  "ativo": true,
  "dispositivoId": 1
}
```

---

### 4. Registrar Leitura
```http
POST /api/leituras
Content-Type: application/json

{
  "valor": 25.5,
  "sensorId": 1
}
```

**Resposta (201 Created):**
```json
{
  "id": 1,
  "valor": 25.5,
  "dataHora": "2025-11-26T10:35:00",
  "alerta": false,
  "sensorId": 1,
  "sensorNome": "Sensor Temperatura",
  "unidadeMedida": "°C",
  "dispositivoId": 1,
  "dispositivoNome": "ESP32-LAB1"
}
```

---

### 5. Listar Dispositivos Inativos (CARTA-DESAFIO)
```http
GET /api/dispositivos/inativos?page=0&size=10&sort=ultimaAtualizacao,asc
```

---

### 6. Estatísticas de Sensor
```http
GET /api/leituras/sensor/1/estatisticas?dataInicio=2025-11-01T00:00:00&dataFim=2025-11-26T23:59:59
```

**Resposta (200 OK):**
```json
{
  "sensorId": 1,
  "sensorNome": "Sensor Temperatura",
  "dataInicio": "2025-11-01T00:00:00",
  "dataFim": "2025-11-26T23:59:59",
  "totalLeituras": 1234,
  "totalAlertas": 15,
  "valorMedio": 24.3,
  "valorMinimo": 18.5,
  "valorMaximo": 31.2,
  "unidadeMedida": "°C"
}
```

---

## ⚙️ Como Executar o Projeto Localmente

### Pré-requisitos

- Java 17 ou superior
- Docker e Docker Compose
- Maven 3.9+
- Git

### Passo a Passo

#### 1. Clonar o Repositório
```bash
git clone https://github.com/DoppelsoldnerHRE/TrabalhoBackEnd-1/tree/main
cd iot-api
```

#### 2. Configurar Banco de Dados (Docker)
```bash
docker-compose up -d
```

Isso iniciará um container PostgreSQL na porta 5432.

#### 3. Verificar Configuração

Edite `src/main/resources/application.properties` se necessário:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/iot_db
spring.datasource.username=iot_user
spring.datasource.password=iot_password
```

#### 4. Compilar e Executar

**Linux/Mac:**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

**Windows:**
```bash
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

#### 5. Acessar a API

- **API Base:** `http://localhost:8080/api`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **API Docs:** `http://localhost:8080/api-docs`

---

## 🧪 Testando a API

### Opção 1: Swagger UI (Recomendado)

1. Acesse `http://localhost:8080/swagger-ui.html`
2. Escolha um endpoint
3. Clique em "Try it out"
4. Preencha os dados
5. Clique em "Execute"

### Opção 2: cURL
```bash
# Criar usuário
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Teste","email":"teste@email.com","senha":"senha12345"}'

# Listar dispositivos inativos
curl http://localhost:8080/api/dispositivos/inativos
```

---

## 🚧 Limitações Conhecidas

1. **Autenticação:** O sistema não possui autenticação/autorização implementada
2. **Criptografia de Senha:** Senhas são armazenadas em texto plano (TODO: implementar BCrypt)
3. **Rate Limiting:** Não há limite de requisições por IP/usuário
4. **Notificações:** Sistema não envia alertas automáticos para dispositivos inativos
5. **Cache:** Não há cache implementado para consultas frequentes
6. **Integração Real com ESP32:** API está preparada mas não há código Arduino/ESP32 neste repositório

---

## 🔮 Melhorias Futuras

- [ ] Implementar autenticação JWT
- [ ] Criptografar senhas com BCrypt
- [ ] Sistema de notificações por email/webhook
- [ ] Dashboard web para visualização de dados
- [ ] Código Arduino para ESP32/Arduino UNO
- [ ] Testes automatizados (unitários e integração)
- [ ] Deploy em ambiente de produção (AWS/Railway/Render)
- [ ] Cache com Redis para estatísticas
- [ ] WebSocket para atualizações em tempo real


**Desenvolvido com ☕ e Spring Boot**
