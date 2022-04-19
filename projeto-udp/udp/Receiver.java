// Classe para implementacao das regras de negocio da maquina receptora
package udp;

// Importacao de biblioteca auxiliares, inclusive dos Sockets
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//Classe Receiver para implementacao da logica de recebimento das mensagens
public class Receiver {

    public static void main(String[] args) throws Exception {

        // Criando variavel para controle do ultimo pacote recebido
        Mensagem ultimoPacote = new Mensagem();

        // Criacao do Socket do "servidor", canal de comunicacao via porta 9876
        DatagramSocket serverSocket = new DatagramSocket(9876);

        // Print para indicar que o servidor foi iniciado
        System.out.println("Servidor iniciado");


        // ------------------LOOP PARA RECEBIMENTO DAS MENSAGENS--------------------

        while(true) { // NOSONAR

            // Declaracao do buffer de recebimento dos dados da mensagem
            byte[] recBuffer = new byte[1024];
            DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);

            // Recebendo o pacote
            serverSocket.receive(recPkt); // BLOCKING

            // Convertendo pacote para Mensagem
            var mensagemRecebida = new Mensagem();
            mensagemRecebida.setMensagemPorTexto(new String(recPkt.getData()).trim());

            // Verifica se eh a primeira mensagem
            if(ultimoPacote.getId() == null){
                ultimoPacote = mensagemRecebida;
            }

            // Verifica se a mensagem recebida esta na ordem correta
            if(ultimoPacote.getId() != null && mensagemRecebida.getId() != null) {
                if ((Integer.parseInt(ultimoPacote.getId()) + 1) == Integer.parseInt(mensagemRecebida.getId())) {
                    ultimoPacote = mensagemRecebida;
                }
            }

            // Apos instanciada a mensagemRecebida, foi chamado o metodo setMensagemPorTexto para converter o pacote recebido (string) via recPkt para uma Map (Json)
            mensagemRecebida.setMensagemPorTexto(new String(recPkt.getData()).trim());

            // Print na tela da mensagemRecebida com o Id
            System.out.println("Mensagem id "  + mensagemRecebida.getId() + " recebida na ordem, entregando para a camada de aplicacao.");

            // Resposta ack
            byte[] ack = new byte[1024];
            ack = ultimoPacote.convertToString().getBytes();

            // Obtendo endereco de IP e porta do cliente para retorno do status (ACK) da mensagem
            InetAddress IPAddress = recPkt.getAddress();
            int port = recPkt.getPort();
            DatagramPacket sendPacket = new DatagramPacket(ack, ack.length, IPAddress, port);

            // Envio do datagrama ao cliente
            serverSocket.send(sendPacket);
        }
    }

}
