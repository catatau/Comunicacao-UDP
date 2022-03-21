// Classe para implementação das regras de negócio da máquina receptora    
package udp;

// Importação de biblioteca auxiliares, inclusive dos Sockets
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {

    public static void main(String[] args) throws Exception {

        // Criação do Socket do "servidor", canal de comunicação via porta 9876
        DatagramSocket serverSocket = new DatagramSocket(9876);
        System.out.println("Servidor iniciado");

        while(true) { //NOSONAR

            // Declaração do buffer de recebimento dos dados da mensagem
            byte[] recBuffer = new byte[1024];

            // Declaração do datagrama a ser recebido com os dados
            DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);

            // Recebimento do datagrama do destinatário
            // Método bloqueante - não avança, enquanto não concluir
            serverSocket.receive(recPkt);

            // Variável para armazenar uma nova mensagem
            var mensagemRecebida = new Mensagem();

            // Após instanciada a mensagemRecebida, foi chamado o método setMensagemPorTexto para converter o pacote recebido (string) 
            // via recPkt para uma Map (Json)
            mensagemRecebida.setMensagemPorTexto(new String(recPkt.getData()).trim());
            
            // Print na tela da mensagemRecebida com o Id gerado aleatóriamente
            System.out.println(mensagemRecebida.getMensagem() + "  ID ->> " + mensagemRecebida.getId());

            // Declaração e preenchmento do buffer de envio
            byte[] sendBuf = new byte[1024];
            sendBuf = "Sou o servidor".getBytes();

            // Obtendo endereço de IP e porta do cliente para retorno do status (ACK) da mensagem 
            InetAddress IPAddress = recPkt.getAddress();
            int port = recPkt.getPort();

            // Criação do datagrama a ser enviado como resposta ao cliente, com o cabeçalho UDP
            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, IPAddress, port);

            // Envio do datagrama ao cliente
            serverSocket.send(sendPacket);
        }
    }

}
