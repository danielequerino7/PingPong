# 🏓 Ping Pong Server em Java

Este projeto implementa um servidor multiplayer de Ping Pong utilizando Java com comunicação via TCP e UDP.

## 💻 Requisitos

- Java JDK 11 ou superior instalado
- Terminal ou prompt de comando
- Arquivos:
  - `PingPongServer.java` (servidor)
  - `PingPongClient.java` (cliente)

--- Pode ser utilizado o TailScale para conectar os clientes no mesmo servidor, quando usar o software use a mesma conta para ambos os clientes.
--- O TailScale cria-se uma LAN virtual para que conecte-se os clientes no mesmo servidor.
--- No arquivo PingPongClient na linha 10 coloque o IP que aparece no TailScale.

## 📁 Estrutura

- `PingPongServer.java` — Código do servidor (apenas um roda este)
- `PingPongClient.java` — Código do cliente (cada jogador roda este)
- O jogo funciona via terminal (linha de comando)

---

## ▶️ Como rodar o jogo

### 1. Compile os arquivos

Abra o terminal na pasta onde estão os arquivos `.java` e digite:

```bash
javac PingPongServer.java PingPongClient.java
```

### 2. No terminal para rodar o Servidor

Digite:

```bash
java PingPongServer.java
```

### 3. Para os clientes entrarem no servidor

Cada jogador deve dar o este comando no terminal:

```bash
java PingPongServer.java
```
