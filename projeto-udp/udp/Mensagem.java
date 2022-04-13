package udp;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Mensagem {


    private String id;
    private String mensagem;



    public Map<String, String> convertToMap(){
        Map<String, String> dado = new HashMap<>();
        dado.put("id", id);
        dado.put("mensagem", mensagem);
        return dado;
    }

    public String convertToString(){
        var dado = convertToMap();
        return dado.keySet().stream()
                .map(key -> key + "=" + dado.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    public void setMesagemPorTexto(String mapAsString){

        Map<String, String> map = Arrays.stream(mapAsString.substring(1, mapAsString.length()-1).split(","))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(entry -> entry[0].trim(), entry -> entry[1]));

        setId(map.get("id"));
        setMensagem(map.get("mensagem"));
    }


    public Mensagem(String id, String msg) {
        this.id = id;
        this.mensagem = msg;
    }

    public Mensagem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
