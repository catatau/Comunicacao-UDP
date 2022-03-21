// Classe para implementação do cabeçalho das mensagens da transmissão       
package udp;

// Importação de biblioteca auxiliares
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Mensagem {

    // Declaração de variáveis que serão os atributos para o cabeçalho da mensagem
    private String id;
    private String mensagem;

    // Método para converter strings para um Map (Json) 
    // O retorno desta função é o objeto Map
    public Map<String, String> convertToMap(){
        Map<String, String> dado = new HashMap<>();
        dado.put("id", id);
        dado.put("mensagem", mensagem);
        return dado;
    }

    // Método para converter o Map para uma String
    // O retorno desta função é uma string
    public String convertToString(){
        var dado = convertToMap();
        return dado.keySet().stream()
                .map(key -> key + "=" + dado.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    // Método que recebe uma string e instancia um Map (Json) com a estrutura do cabeçalho 
    public void setMensagemPorTexto(String mapAsString){

        Map<String, String> map = Arrays.stream(mapAsString.substring(1, mapAsString.length()-1).split(","))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(entry -> entry[0].trim(), entry -> entry[1]));

        setId(map.get("id"));
        setMensagemPorTexto(map.get("mensagem"));
    }

    // Construtor inicial da classe mensagem
    public Mensagem(String id, String msg) {
        this.id = id;
        this.mensagem = msg;
    }

    // Construtor vazio para a classe mensagem
    public Mensagem() {
    }

    // Construtor get do atributo Id 
    public String getId() {
        return id;
    }

    // Construtor set atributo Id
    public void setId(String id) {
        this.id = id;
    }

    // Construtor get para retornar a mensagem
    public String getMensagem() {
        return mensagem;
    }

    // Construtor set incluir a mensagem
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
