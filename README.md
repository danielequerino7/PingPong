# 🏓 Ping Pong Server em Java

Este projeto implementa um servidor multiplayer de Ping Pong utilizando Java com comunicação via TCP e UDP.

## 💻 Requisitos

- Java JDK 11 ou superior instalado
- Terminal ou prompt de comando
- Arquivos:
  - `PingPongServer.java` (servidor)
  - `PingPongClient.java` (cliente)

---

## 🌐 Conexão entre máquinas com Tailscale

Caso os jogadores não estejam na mesma rede local, você pode utilizar o software [Tailscale](https://tailscale.com/) para criar uma **rede virtual (LAN)**:

- Instale o Tailscale em todas as máquinas
- Faça login com a **mesma conta** em todas
- Utilize o **endereço IP fornecido pelo Tailscale** para conectar os clientes ao servidor
- No arquivo `PingPongClient.java`, altere a linha onde o IP do servidor é definido (por exemplo, na linha 10) e insira o IP do servidor exibido no Tailscale

---

## 📁 Estrutura

- `PingPongServer.java` — Código do servidor (**apenas um jogador roda**)
- `PingPongClient.java` — Código do cliente (**cada jogador roda o seu**)
- O jogo funciona via terminal (linha de comando)

---

## ▶️ Como rodar o jogo

### 1. Compile os arquivos

No terminal, dentro da pasta onde estão os arquivos `.java`, digite:

```bash
javac PingPongServer.java PingPongClient.java
```

### 2. No terminal para rodar o Servidor

Digite:

bash
java PingPongServer.java


### 3. Para os clientes entrarem no servidor

Cada jogador deve dar este comando no terminal:

bash
java PingPongServer.java
