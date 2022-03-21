package udp;


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

        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        Boolean statusConexao = true;

        System.out.println("Enter String");

        while (statusConexao) { // NOSONAR
            DatagramSocket clientSocket = new DatagramSocket();

            var br = new BufferedReader(new InputStreamReader(System.in));


            byte[] sendData = new byte[1024];
            sendData = new Mensagem(UUID.randomUUID().toString(), br.readLine())
                    .convertToString()
                    .getBytes();

            System.out.println("Escolha a opção de envio:\n1 - Lenta\n2 - Perda\n3 - Fora de ordem\n4 - Duplicada\n5 - Normal");
            var opcaoUsuario = new BufferedReader(new InputStreamReader(System.in)).readLine();

            switch (opcaoUsuario) {
                case "1":
                    Thread.sleep(3000);
                    envioNormal(IPAddress, clientSocket, sendData);
                    System.out.println("Mensagem enviada com lentidão!");
                    break;
                case "2":
                    Thread.sleep(5000);
                    System.out.println("Mensagem perdida!");
                    break;
                case "4":
                    envioDuplicado(IPAddress, clientSocket, sendData);
                    System.out.println("Mensagem duplicada enviada com sucesso!");
                    break;
                case "5":
                    envioNormal(IPAddress, clientSocket, sendData);
                    System.out.println("Mensagem enviada com sucesso!");
                    break;
            }

        }

    }

    private static void envioNormal(InetAddress IPAddress, DatagramSocket clientSocket, byte[] sendData) throws IOException {
        // Cria��o do pacote de envio
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

        // Enviando o pacote ao servidor
        clientSocket.send(sendPacket);
        //System.out.println("mensagem enviada para o servidor");

        // Criando buffer de recebimento
        byte[] recBuffer = new byte[1024];

        // Recebendo o pacote do servidor
        DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);


        clientSocket.receive(recPkt); //BLOCKING

        String informacao = new String(recPkt.getData(),
                recPkt.getOffset(),
                recPkt.getLength()); // IMPORTANTE

        // System.out.println("recebido do servidor: " + informacao);

        // fechando a conexao
        clientSocket.close();
    }

    private static void envioDuplicado(InetAddress IPAddress, DatagramSocket clientSocket, byte[] sendData) throws IOException {
        // Cria��o do pacote de envio
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

        // Enviando o pacote ao servidor
        clientSocket.send(sendPacket);
        clientSocket.send(sendPacket);

        // Criando buffer de recebimento
        byte[] recBuffer = new byte[1024];

        // Recebendo o pacote do servidor
        DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);


        clientSocket.receive(recPkt); //BLOCKING

        String informacao = new String(recPkt.getData(),
                recPkt.getOffset(),
                recPkt.getLength()); // IMPORTANTE

        // System.out.println("recebido do servidor: " + informacao);

        // fechando a conexao
        clientSocket.close();
    }


}

