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
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Sender {


    public static void main(String[] args) throws Exception {
        // Obtendo endereço IP do receiver (server)
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        
        // Criação de variável para controle da conexão
        Boolean statusConexao = true;

        // Indicação de inicio para envio das mensagens
        System.out.println("Enter String");

        ArrayList<Mensagem> listaMensagens = new ArrayList<>();
        ArrayList<String> listaAckRecebidos = new ArrayList<>();
        int janelaEnvio = 4;


        listaMensagens.add(new Mensagem("1", "Bolacha1"));
        listaMensagens.add(new Mensagem("2", "Bolacha2"));
        listaMensagens.add(new Mensagem("3", "Bolacha3"));
        listaMensagens.add(new Mensagem("4", "Bolacha4"));
        listaMensagens.add(new Mensagem("5", "Bolacha5"));
        listaMensagens.add(new Mensagem("6", "Bolacha6"));
        listaMensagens.add(new Mensagem("7", "Bolacha7"));
        listaMensagens.add(new Mensagem("8", "Bolacha8"));
        listaMensagens.add(new Mensagem("9", "Bolacha9"));

        String idUltimoPacoteSemResposta = "1";


        int quantidadeEnvioSucessoJanela;
        while (statusConexao) { // NOSONAR
            // Criação do Socket para uma porta designada pelo SO
            DatagramSocket clientSocket = new DatagramSocket();
            if (listaMensagens.size() > 0) {
                quantidadeEnvioSucessoJanela = 0;
                for (int i = 0; i < janelaEnvio; i++) {

                    if (listaMensagens.size() > i) {


                        // System.out.println(listaMensagens.get(i).convertToString());
                        System.out.println(listaMensagens.size() + " size  tag " + i);

                        byte[] sendData = new byte[1024];
                        sendData = listaMensagens.get(i).convertToString().getBytes();


                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

                        try {
                            clientSocket.send(sendPacket);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        // Criando buffer de recebimento
                        byte[] recBuffer = new byte[1024];

                        // Recebendo o pacote do servidor
                        DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);


                        try {
                            clientSocket.receive(recPkt); //BLOCKING
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        String ackRecebido = new String(recPkt.getData(),
                                recPkt.getOffset(),
                                recPkt.getLength());

                        System.out.println(ackRecebido + " ack servidor");

                        listaAckRecebidos.add(ackRecebido);


                        Mensagem ack = new Mensagem();
                        ack.setMensagemPorTexto(ackRecebido);


                        if (Objects.equals(ack.getId(), idUltimoPacoteSemResposta)) {
                            idUltimoPacoteSemResposta = String.valueOf(Integer.parseInt(idUltimoPacoteSemResposta) + 1);
                            quantidadeEnvioSucessoJanela = quantidadeEnvioSucessoJanela + 1;
                        }
                    }

                }
                // System.out.println("recebido do servidor: " + ack.convertToString() + listaMensagens.size());


                for (int aux = 0; aux < quantidadeEnvioSucessoJanela; aux++) {
                    listaMensagens.remove(0);
                }

            } else {
                // fechando a conexao
                clientSocket.close();
                statusConexao = false;
            }


            //


            //var novaMensagem = new Mensagem();

            //movimentarListaJanelaEnvio(listaExecucaoEnvio, novaMensagem);


            // versaoAntiga(IPAddress, clientSocket);
        }
    }

    private static void movimentarListaJanelaEnvio(ArrayList<Mensagem> listaExecucaoEnvio, Mensagem novaMensagem) {
        listaExecucaoEnvio.remove(0);
        listaExecucaoEnvio.add(novaMensagem);
    }


    private static void versaoAntiga(InetAddress IPAddress, DatagramSocket clientSocket) throws IOException, InterruptedException {
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

