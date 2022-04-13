package udp;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {

    public static void main(String[] args) throws Exception {

        Mensagem ultimoPacote = new Mensagem();
        DatagramSocket serverSocket = new DatagramSocket(9876);
        System.out.println("Servidor iniciado");

        while(true) { //NOSONAR

            byte[] recBuffer = new byte[1024];
            DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);

            // Recebendo o pacote
            serverSocket.receive(recPkt); // BLOCKING

            // Convertendo pacote para Mensagem
            var mensagemRecebida = new Mensagem();
            mensagemRecebida.setMesagemPorTexto(new String(recPkt.getData()).trim());

            // Verifica se é a primeira mensagem
            if(ultimoPacote.getId() == null){
                ultimoPacote = mensagemRecebida;
            }

            // Verifica se é a mensagem recebida está na ordem correta
            if(ultimoPacote.getId() != null && mensagemRecebida.getId() != null) {
                if ((Integer.parseInt(ultimoPacote.getId()) + 1) == Integer.parseInt(mensagemRecebida.getId())) {
                    ultimoPacote = mensagemRecebida;
                }
            }



            // Resposta ack
            byte[] ack = new byte[1024];
            ack = ultimoPacote.convertToString().getBytes();

            InetAddress IPAddress = recPkt.getAddress();
            int port = recPkt.getPort();
            DatagramPacket sendPacket = new DatagramPacket(ack, ack.length, IPAddress, port);

            serverSocket.send(sendPacket);
        }
    }

}
