import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.rmi.server.LogStream.log;

public class PingPongClient {
    private static final Object consoleTrancar = new Object();
    private static final String ENDERECO_SERVIDOR = "100.110.84.12";
    private static final int PORTA_TCP = 12345;
    private static final int PORTA_UDP = 12346;
    private static final String ARQUIVO_LOG = "client_log.txt";

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(ENDERECO_SERVIDOR, PORTA_TCP);
        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader respostaServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(new UDPHandler()).start(); // ouve as mensagens via UDP

        String comando;
        while (true) {
            synchronized (consoleTrancar) { //para que não haja comandos simultaneos no console
                System.out.println("Digite 'W' (subir) ou 'S' (descer):");
            }
            comando = entrada.readLine();
            long inicio = System.nanoTime();
            saida.println(comando);

            String estado = respostaServidor.readLine();
            long fim = System.nanoTime();

            synchronized (consoleTrancar) {
                log("TCP: " + estado + " | Latência: " + (fim - inicio) / 1_000_000.0 + "ms");
            }
        }
    }

    static class UDPHandler implements Runnable {
        @Override
        public void run() {
            try {
                DatagramSocket socket = new DatagramSocket(PORTA_UDP);
                byte[] buffer = new byte[1024];

                while (true) {
                    DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
                    long inicio = System.nanoTime();
                    socket.receive(pacote);
                    long fim = System.nanoTime();

                    String recebido = new String(pacote.getData(), 0, pacote.getLength());
                    String[] partes = recebido.split(",");
                    int bolaX = Integer.parseInt(partes[0].split("=")[1]);
                    int bolaY = Integer.parseInt(partes[1].split("=")[1]);
                    int pontuacao1 = Integer.parseInt(partes[2].split("=")[1]);
                    int pontuacao2 = Integer.parseInt(partes[3].split("=")[1]);
                    int jogador1Y = Integer.parseInt(partes[4].split("=")[1]);
                    int jogador2Y = Integer.parseInt(partes[5].split("=")[1]);


                    log("UDP: " + recebido + " | Latência: " + (fim - inicio) / 1_000_000.0 + "ms");
                    desenhaGame(bolaX, bolaY, pontuacao1, pontuacao2, jogador1Y, jogador2Y);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void desenhaGame(int bolaX, int bolaY, int pontuacao1, int pontuacao2, int jogador1Y, int jogador2Y) {
            synchronized (PingPongClient.consoleTrancar) {
                final int LARGURA = 40, ALTURA = 10;
                System.out.print("\033[H\033[2J"); // Limpa console
                System.out.flush();
                System.out.println("Placar: " + pontuacao1 + " x " + pontuacao2);
                System.out.println("-".repeat(LARGURA));
                for (int y = 0; y < ALTURA; y++) {
                    StringBuilder linha = new StringBuilder("|");

                    for (int x = 0; x < LARGURA - 2; x++) {
                        if (x == bolaX && y == bolaY) {
                            linha.append("O"); // bola
                        } else if (x == 1 && (y == jogador1Y || y == jogador1Y - 1 || y == jogador1Y + 1)) {
                            linha.append("|"); // raquete esquerda
                        } else if (x == LARGURA - 4 && (y == jogador2Y || y == jogador2Y - 1 || y == jogador2Y + 1)) {
                            linha.append("|"); // raquete direita
                        } else {
                            linha.append(" ");
                        }
                    }

                    linha.append("|");
                    System.out.println(linha);
                }
                System.out.println("-".repeat(LARGURA));
            }
        }


        private void log(String message) {
            try (FileWriter fw = new FileWriter(ARQUIVO_LOG, true)) {
                fw.write(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " - " + message + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
