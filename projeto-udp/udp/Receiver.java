package udp;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {

    public static void main(String[] args) throws Exception {

        DatagramSocket serverSocket = new DatagramSocket(9876);
        System.out.println("Servidor iniciado");

        while(true) { //NOSONAR

            byte[] recBuffer = new byte[1024];

            DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);


            serverSocket.receive(recPkt); // BLOCKING




            var mensagemRecebida = new Mensagem();
            mensagemRecebida.setMensagemPorTexto(new String(recPkt.getData()).trim());
            System.out.println(mensagemRecebida.getMensagem()+"  ID ->> "+ mensagemRecebida.getId());


            byte[] sendBuf = new byte[1024];
            sendBuf = "Sou o servidor".getBytes();

            InetAddress IPAddress = recPkt.getAddress();
            int port = recPkt.getPort();

            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, IPAddress, port);

            serverSocket.send(sendPacket);
            //System.out.println("Mensagem enviada pelo server");
        }
    }

}
