
// Classe para implementacao das regras de negocio da maquina de envio
package udp;

// Importacao de biblioteca auxiliares, inclusive dos Sockets, Buffers e Timer p o temporizador

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;

// Classe Sender para implementacao da logica de envio das mensagens
public class Sender {

    public static void main(String[] args) throws Exception {
        // Obtendo endereco IP do receiver (server)
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

        // Criacao de variavel para controle da conexao
        Boolean statusConexao = true;

        // Indicacao de inicio para envio das mensagens
        System.out.println("Enter String");

        // Criando arrays para guardar as mensagens e os acksRecebidos
        ArrayList<String> listaAckRecebidos = new ArrayList<>();

        // Pré-determinando o tamanho da janela como 4 e o ID 0
        int janelaEnvio = 4;
        int id = 0;

        // Lendo mensagem do usuário
        BufferedReader usuarioMensagem = new BufferedReader((new InputStreamReader(System.in)));
        String mensagemCliente = usuarioMensagem.readLine();

        // Usuario escolhendo o tipo de envio
        System.out.println("Escolha a opcao de envio:\n"
                + "1 - Lenta\n"
                + "2 - Perda\n"
                + "3 - Fora de ordem\n"
                + "4 - Duplicada\n"
                + "5 - Normal");
        var opcaoUsuario = new BufferedReader(new InputStreamReader(System.in)).readLine();

        ArrayList<Mensagem> listaMensagens = new ArrayList<>();
        // Chamando metodo auxiliar que realizar a quebra dos pacotes
        String[] pacotesDeMensagem = quebrarMensagemEmPacotes(mensagemCliente);

        // Percorrendo a lista de pacotes de mensagem para adicionar à lista de mensagens
        for (int i = 0; i < pacotesDeMensagem.length; i++) {
            id = id + 1;
            listaMensagens.add(new Mensagem(Integer.toString(id), pacotesDeMensagem[i]));
        }

        if (Objects.equals(opcaoUsuario, "3")) {
            Mensagem ultimoItemLista = listaMensagens.get(listaMensagens.size() - 1);
            listaMensagens.set(listaMensagens.size() - 1, listaMensagens.get(listaMensagens.size() - 2));
            listaMensagens.set(listaMensagens.size() - 2, ultimoItemLista);
        }



        // Criando variavel para controle do ultimo pacote enviado
        String idUltimoPacoteSemResposta = "1";

        // Criando variavel para controle da quantidade de pacotes enviados com sucesso na janela disponivel
        int quantidadeEnvioSucessoJanela;

        // ------------------LOOP PARA ENVIO DAS MENSAGENS--------------------
        while (statusConexao) {

            // Criando o Socket para uma porta designada pelo Sistema Operacional
            DatagramSocket clientSocket = new DatagramSocket();

            // Caso a lista de mensagem conter mensagens, tentar o envio
            if (listaMensagens.size() > 0) {

                quantidadeEnvioSucessoJanela = 0;

                for (int i = 0; i < janelaEnvio; i++) {


                    if (listaMensagens.size() > i) {

                        // Criando buffer do sendData para enviar o pacote ao servidor
                        byte[] sendData = new byte[1024];
                        sendData = listaMensagens.get(i).convertToString().getBytes();

                        // Criando o sendPacket para enviar o datagrama ao servidor
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

                        // Tenta enviar o pacote, se não cai em uma excessão exibida na tela
                        try {

                            // Enviando conforme opcao escolhida pelo usuario

                            switch (opcaoUsuario) {
                                case "1":
                                    // Enviando os dados após delay de 3 segundos
                                    Thread.sleep(3000);
                                    clientSocket.send(sendPacket);
                                    break;
                                case "2":
                                    // Printando a saida necessaria e sem enviar os dados
                                    for (int a = 0; a < listaMensagens.size(); a++)
                                        System.out.println("Mensagem " + listaMensagens.get(a).getMensagem() + " enviada como Perda com id " + listaMensagens.get(a).getId());
                                    break;
                                case "3":
                                    // code here
                                    clientSocket.send(sendPacket);
                                    break;
                                case "4":
                                    clientSocket.send(sendPacket);
                                    clientSocket.send(sendPacket);
                                    break;
                                case "5":
                                    clientSocket.send(sendPacket);
                                    break;
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        // Criando buffer de recebimento
                        byte[] recBuffer = new byte[1024];

                        // Recebendo o pacote do servidor
                        DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);

                        // Tenta receber o pacote, se não cai em uma excessão exibida na tela
                        try {
                            clientSocket.receive(recPkt);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        // Criando variavel para receber o ack do receiver
                        String ackRecebido = new String(recPkt.getData(),
                                recPkt.getOffset(),
                                recPkt.getLength());

                        // Adicionando o ackRecebido na lista de Acks recebidos
                        listaAckRecebidos.add(ackRecebido);

                        // Criando um ack do tipo Mensagem
                        Mensagem ack = new Mensagem();

                        // Preenchendo o ack do tipo Mensagem com os valores recebidos
                        ack.setMensagemPorTexto(ackRecebido);

                        // Printando na tela as informações enviadas aos servidor, dado cada caso
                        switch (opcaoUsuario) {
                            case "1":
                                System.out.println("Mensagem " + ack.getMensagem() + " enviada como [Lenta] com id " + ack.getId());
                                System.out.println("Mensagem id " + ack.getId() + " recebida pelo receiver.");
                                break;

                            case "3":
                                System.out.println("Mensagem " + ack.getMensagem() + " enviada como [Fora de Ordem] com id " + ack.getId());
                                System.out.println("Mensagem id " + ack.getId() + " recebida pelo receiver.");
                                break;
                            case "4":
                                System.out.println("Mensagem " + ack.getMensagem() + " enviada como [Duplicada] com id " + ack.getId());
                                System.out.println("Mensagem id " + ack.getId() + " recebida pelo receiver.");
                                break;
                            case "5":
                                System.out.println("Mensagem " + ack.getMensagem() + " enviada como [Normal] com id " + ack.getId());
                                System.out.println("Mensagem id " + ack.getId() + " recebida pelo receiver.");
                                break;
                        }


                        // Se o ID do ack recebido for igual ao ultimo ID sem resposta, acrescenta o ultimo ID sem resposta e a quantidade de envio da janela
                        if (Objects.equals(ack.getId(), idUltimoPacoteSemResposta)) {
                            idUltimoPacoteSemResposta = String.valueOf(Integer.parseInt(idUltimoPacoteSemResposta) + 1);
                            quantidadeEnvioSucessoJanela = quantidadeEnvioSucessoJanela + 1;
                        }
                    }


                }

                // Realizando o controle de limpeza da janela, quando os acks ja foram reconhecidos por ambos as partes (Sender e Receiver)

                for (int aux = 0; aux < quantidadeEnvioSucessoJanela; aux++) {
                    listaMensagens.remove(0);
                }

            } else {

                System.out.println("Deseja continuar? (s/n) ");

                var respostaFecharServidor = new BufferedReader(new InputStreamReader(System.in)).readLine();

                switch (respostaFecharServidor.toUpperCase()) {
                    case "S":
                        System.out.println("Enter String");

                        // Lendo mensagem do usuário
                        usuarioMensagem = new BufferedReader((new InputStreamReader(System.in)));
                        mensagemCliente = usuarioMensagem.readLine();

                        // Usuario escolhendo o tipo de envio
                        System.out.println("Escolha a opcao de envio:\n"
                                + "1 - Lenta\n"
                                + "2 - Perda\n"
                                + "3 - Fora de ordem\n"
                                + "4 - Duplicada\n"
                                + "5 - Normal");
                        opcaoUsuario = new BufferedReader(new InputStreamReader(System.in)).readLine();

                        listaMensagens = new ArrayList<>();
                        // Chamando metodo auxiliar que realizar a quebra dos pacotes
                        pacotesDeMensagem = quebrarMensagemEmPacotes(mensagemCliente);

                        // Percorrendo a lista de pacotes de mensagem para adicionar à lista de mensagens
                        for (int i = 0; i < pacotesDeMensagem.length; i++) {
                            id = id + 1;
                            listaMensagens.add(new Mensagem(Integer.toString(id), pacotesDeMensagem[i]));
                        }

                        break;
                    default:
                        // Fechando a conexao
                        clientSocket.close();
                        statusConexao = false;

                }
            }
        }
    }

    private static String[] quebrarMensagemEmPacotes(String MensagemRecebida) {
        String[] quebraMensagem = MensagemRecebida.split(" ", -1);

        return quebraMensagem;
    }

    private static void movimentarListaJanelaEnvio(ArrayList<Mensagem> listaExecucaoEnvio, Mensagem novaMensagem) {
        listaExecucaoEnvio.remove(0);
        listaExecucaoEnvio.add(novaMensagem);
    }

}


