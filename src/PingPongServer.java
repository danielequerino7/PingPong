import java.io.*;
import java.net.*;
import java.util.*;

public class PingPongServer {
    private static final int PORTA_TCP = 12345;
    private static final int PORTA_UDP = 12346;

    private static int bolaX = 20, bolaY = 5, dx = 1, dy = 1;
    private static int pontuacao1 = 0, pontuacao2 = 0;
    private static final int LARGURA = 40, ALTURA = 10;

    // posicoes iniciais das raquetes
    private static int jogador1Y = ALTURA / 2, jogador2Y = ALTURA / 2;  

    private static final List<ClienteInfo> clientes = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        ServerSocket socketServidor = new ServerSocket(PORTA_TCP);
        System.out.println("Servidor iniciado na porta TCP " + PORTA_TCP + " e UDP " + PORTA_UDP);

        // thread para mover a bola automaticamente
        new Thread(() -> {
            while (true) {
                moverBola();
                transmissaoEstadoJogo();
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
        }).start();

        // aceitar conexoes dos jogadores
        while (true) {
            Socket socket = socketServidor.accept();
            ClienteInfo cliente = new ClienteInfo(socket);
            clientes.add(cliente);

            String ClienteInfo = cliente.endereco.getHostAddress() + ":" + socket.getPort();
            System.out.println("Novo cliente conectado: " + ClienteInfo);
            System.out.println("Total de clientes conectados: " + clientes.size());

            new Thread(new ClientHandler(cliente)).start();
        }
    }

    static class ClienteInfo {
        Socket socket;
        PrintWriter saida;
        BufferedReader entrada;
        InetAddress endereco;
        int udpPort = PORTA_UDP;
        long ultimoPingTempo = System.currentTimeMillis();

        ClienteInfo(Socket socket) throws IOException {
            this.socket = socket;
            this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.saida = new PrintWriter(socket.getOutputStream(), true);
            this.endereco = socket.getInetAddress();
        }
    }

    static class ClientHandler implements Runnable {
        ClienteInfo client;

        ClientHandler(ClienteInfo cliente) {
            this.client = cliente;
        }

        public void run() {
            String clientID = client.endereco.getHostAddress() + ":" + client.socket.getPort();
            try {
                String comando;
                while ((comando = client.entrada.readLine()) != null) {
                    client.ultimoPingTempo = System.currentTimeMillis();
                    System.out.println("[TCP RECEBIDO de " + clientID + "] " + comando);

                    // controla as raquetes dos jogadores
                    if (comando.equalsIgnoreCase("W")) {
                        if (client.endereco.getHostAddress().equals(clientes.get(0).endereco.getHostAddress())) {
                            jogador1Y = Math.max(0, jogador1Y - 1);
                        } else {
                            jogador2Y = Math.max(0, jogador2Y - 1);
                        }
                        client.saida.println("RAQUETE MOVIDA: CIMA");
                    }
                    if (comando.equalsIgnoreCase("S")) {
                        if (client.endereco.getHostAddress().equals(clientes.get(0).endereco.getHostAddress())) {
                            jogador1Y = Math.min(ALTURA - 1, jogador1Y + 1);
                        } else {
                            jogador2Y = Math.min(ALTURA - 1, jogador2Y + 1);
                        }
                        client.saida.println("RAQUETE MOVIDA: BAIXO");
                    }
                }
            } catch (IOException e) {
                System.out.println("Cliente desconectado: " + clientID);
            } finally {
                try {
                    if (client.socket != null) client.socket.close();
                } catch (IOException ignored) {}
                clientes.remove(client);
                System.out.println("Total de clientes conectados: " + clientes.size());
            }
        }
    }

    private static void moverBola() {
        bolaX += dx;
        bolaY += dy;

        // verifica colisoes com as raquetes
        if (bolaX == 2 && bolaY >= jogador1Y - 1 && bolaY <= jogador1Y + 1) {
            dx *= -1;  // inverte direcao ao colidir com a raquete do jogador 1
        }
        if (bolaX == LARGURA - 4 && bolaY >= jogador2Y - 1 && bolaY <= jogador2Y + 1) {
            dx *= -1;  // inverte direcao ao colidir com a raquete do jogador 2
        }

        // verifica se a bola bateu nas bordas
        if (bolaY <= 0 || bolaY >= ALTURA - 1) dy *= -1;

        // verifica se algum jogador marcou ponto
        if (bolaX <= 0) { pontuacao2++; resetarBola(); }
        if (bolaX >= LARGURA - 2) { pontuacao1++; resetarBola(); }
    }

    private static void resetarBola() {
        bolaX = LARGURA / 2;
        bolaY = ALTURA / 2;
        dx *= -1;
    }

    private static void transmissaoEstadoJogo() {
        String estado = "bolaX=" + bolaX + ",bolaY=" + bolaY + ",pontuacao1=" + pontuacao1 + ",pontuacao2=" + pontuacao2 + ",jogador1Y=" + jogador1Y + ",jogador2Y=" + jogador2Y;

        // envia via TCP e UDP para todos os clientes
        for (ClienteInfo client : clientes) {
            try {
                // envia via TCP
                client.saida.println(estado);

                // envia via UDP
                byte[] data = estado.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, client.endereco, client.udpPort);
                DatagramSocket udpSocket = new DatagramSocket();
                udpSocket.send(packet);
                udpSocket.close(); // fecha o socket UDP após o envio

            } catch (IOException e) {
                System.out.println("Erro ao enviar estado para: " + client.endereco);
            }
        }
    }
}