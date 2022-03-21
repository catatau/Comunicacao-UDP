// Classe para implementação das regras de negócio da máquina de envio    
package udp;

// Importação de biblioteca auxiliares, inclusive dos Sockets e Buffers
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

public class Sender {

    public static void main(String[] args) throws Exception {
        // Obtendo endereço IP do receiver (server)
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        
        // Criação de variável para controle da conexão
        Boolean statusConexao = true;

        // Indicação de inicio para envio das mensagens
        System.out.println("Enter String");

        while (statusConexao) { // NOSONAR
            // Criação do Socket para uma porta designada pelo SO
            DatagramSocket clientSocket = new DatagramSocket();

            // Criação de variável para ser o Buffer e armazenar a mensagem à ser enviada
            var br = new BufferedReader(new InputStreamReader(System.in));

            // Declaração e preenchimento do buffer de envio
            byte[] sendData = new byte[1024];
            sendData = new Mensagem(UUID.randomUUID().toString(), br.readLine())
                                    .convertToString()
                                    .getBytes();

            // Após envio da mensagem, sender deve escolher a forma de envio
            System.out.println("Escolha a opção de envio:\n1 - Lenta\n2 - Perda\n3 - Fora de ordem\n4 - Duplicada\n5 - Normal");
            
            // Lendo a opção de envio escolhida pelo sender e armazenando em buffer
            var opcaoUsuario = new BufferedReader(new InputStreamReader(System.in)).readLine();

            // Escolha-caso dado a opção do sender
            switch (opcaoUsuario) {
                // envio lento
                case "1":
                    Thread.sleep(3000);
                    envioNormal(IPAddress, clientSocket, sendData);
                    System.out.println("Mensagem enviada com lentidão!");
                    break;
                // envio com perda
                case "2":
                    Thread.sleep(5000);
                    System.out.println("Mensagem perdida!");
                    break;
                // envio fora de ordem
                case "3":
                    // Code here
                    break;
                // envio com duplicidade    
                case "4":
                    envioDuplicado(IPAddress, clientSocket, sendData);
                    System.out.println("Mensagem duplicada enviada com sucesso!");
                    break;
                // envio normal
                case "5":
                    envioNormal(IPAddress, clientSocket, sendData);
                    System.out.println("Mensagem enviada com sucesso!");
                    break;
            }
        }
    }

    private static void envioNormal(InetAddress IPAddress, DatagramSocket clientSocket, byte[] sendData) throws IOException {
        // Criação do pacote de envio
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

        // Enviando o pacote ao servidor
        clientSocket.send(sendPacket);
        
        // Criando buffer de recebimento
        byte[] recBuffer = new byte[1024];

        // Recebendo o pacote do servidor
        DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);

        // Recebimento do datagrama do servidor
        // Método bloqueante - não avança, enquanto não concluir
        clientSocket.receive(recPkt);

        String informacao = new String (recPkt.getData(),
                                        recPkt.getOffset(),
                                        recPkt.getLength()); // IMPORTANTE

        // System.out.println("recebido do servidor: " + informacao);

        // Fechando a conexao
        clientSocket.close();
    }

    private static void envioDuplicado(InetAddress IPAddress, DatagramSocket clientSocket, byte[] sendData) throws IOException {
        // Criação do pacote de envio
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

        // Enviando o pacote duplicado ao servidor
        clientSocket.send(sendPacket);
        clientSocket.send(sendPacket);

        // Criando buffer de recebimento
        byte[] recBuffer = new byte[1024];

        // Recebendo o pacote do servidor
        DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);

        // Recebimento do datagrama do servidor
        // Método bloqueante - não avança, enquanto não concluir
        clientSocket.receive(recPkt); //BLOCKING

        String informacao = new String (recPkt.getData(),
                                        recPkt.getOffset(),
                                        recPkt.getLength()); // IMPORTANTE

        // System.out.println("recebido do servidor: " + informacao);

        // Fechando a conexao
        clientSocket.close();
    }


}

