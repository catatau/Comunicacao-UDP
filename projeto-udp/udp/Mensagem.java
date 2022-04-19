// Classe para implementacao do cabecalho das mensagens da transmissao
package udp;

// Importacao de biblioteca auxiliares
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Mensagem {

    // Declaracao de variaveis que serao os atributos para o cabecalho da mensagem
    private String id;
    private String mensagem;

    // Metodo para converter strings para um Map (Json)
    // O retorno desta funcao e o objeto Map
    public Map<String, String> convertToMap(){
        Map<String, String> dado = new HashMap<>();
        dado.put("id", id);
        dado.put("mensagem", mensagem);
        return dado;
    }

    // Metodo para converter o Map para uma String
    // O retorno desta funcao e uma string
    public String convertToString(){
        var dado = convertToMap();
        return dado.keySet().stream()
                .map(key -> key + "=" + dado.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    // Metodo que recebe uma string e instancia um Map (Json) com a estrutura do cabeÃ§alho
    public void setMensagemPorTexto(String mapAsString){

        Map<String, String> map = Arrays.stream(mapAsString.substring(1, mapAsString.length()-1).split(","))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(entry -> entry[0].trim(), entry -> entry[1]));

        setId(map.get("id"));
        setMensagem(map.get("mensagem"));
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
