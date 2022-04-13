// Classe para implementação das regras de negócio da máquina receptora    
package udp;

// Importação de biblioteca auxiliares, inclusive dos Sockets
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {

    public static void main(String[] args) throws Exception {

        Mensagem ultimoPacote = new Mensagem();
        // Criação do Socket do "servidor", canal de comunicação via porta 9876
        DatagramSocket serverSocket = new DatagramSocket(9876);
        System.out.println("Servidor iniciado");

        while(true) { //NOSONAR

            // Declaração do buffer de recebimento dos dados da mensagem
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


            // Após instanciada a mensagemRecebida, foi chamado o método setMensagemPorTexto para converter o pacote recebido (string) 
            // via recPkt para uma Map (Json)
            mensagemRecebida.setMensagemPorTexto(new String(recPkt.getData()).trim());
            
            // Print na tela da mensagemRecebida com o Id gerado aleatóriamente
            System.out.println(mensagemRecebida.getMensagem() + "  ID ->> " + mensagemRecebida.getId());

            // Resposta ack
            byte[] ack = new byte[1024];
            ack = ultimoPacote.convertToString().getBytes();

            // Obtendo endereço de IP e porta do cliente para retorno do status (ACK) da mensagem 
            InetAddress IPAddress = recPkt.getAddress();
            int port = recPkt.getPort();
            DatagramPacket sendPacket = new DatagramPacket(ack, ack.length, IPAddress, port);

            // Envio do datagrama ao cliente
            serverSocket.send(sendPacket);
        }
    }

}
