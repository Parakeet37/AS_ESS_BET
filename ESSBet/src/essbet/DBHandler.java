package essbet;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DBHandler {
    private JSONParser parser;
    private static final String FICHEIRO_APOSTAS = "apostas.txt";
    private static final String FICHEIRO_EVENTOS = "eventos.txt";
    private static final String FICHEIRO_JOGADORES = "jogadores.txt";
    private HashMap<String, ArrayList<Aposta>> apostas;
    private HashMap<String, Jogador> jogadores;
    private HashMap<Integer, Evento> eventos;
    
    public DBHandler (){
        parser = new JSONParser();
        apostas = new HashMap<>();
        jogadores = new HashMap<>();
        eventos = new HashMap<>();
    }
    
    public void carregarBD(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            JSONArray jogadoresArray = (JSONArray) parser.parse(new FileReader(System.getProperty("user.dir")+"\\"+FICHEIRO_JOGADORES));
            JSONArray eventosArray = (JSONArray) parser.parse(new FileReader(System.getProperty("user.dir")+"\\"+FICHEIRO_EVENTOS));
            JSONArray apostasArray = (JSONArray) parser.parse(new FileReader(System.getProperty("user.dir")+"\\"+FICHEIRO_APOSTAS));
            for (Object o: jogadoresArray){
                JSONObject json = (JSONObject) o;
                Jogador j = mapper.readValue(json.toJSONString(), Jogador.class);
                jogadores.put(j.getEmail(), j);
            }
            for (Object o: eventosArray){
                JSONObject json = (JSONObject) o;
                Evento e = mapper.readValue(json.toJSONString(), Evento.class);
                eventos.put(((Long) json.get("id")).intValue(), e);
            }
            for (Object o: apostasArray){
                JSONObject json = (JSONObject) o;
                Set<String> ids = jogadores.keySet();//mails
                for (String id: ids){
                    JSONArray arr = (JSONArray) json.get(id);
                    if (arr!=null){
                        ArrayList<Aposta> arrApostas = new ArrayList<>();
                        for (Object ob: arr){
                            arrApostas.add(mapper.readValue(((JSONObject)ob).toJSONString(), Aposta.class));
                        }
                        apostas.put(id, arrApostas);
                    }
                }
                
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void atualizarBD() {
    	ObjectMapper mapper = new ObjectMapper();
    	JSONArray jsonJogadores = new JSONArray();
    	for (String s: jogadores.keySet()) {
    		try {
				jsonJogadores.add( (JSONObject) parser.parse(mapper.writeValueAsString(jogadores.get(s))));
			} catch (JsonProcessingException | ParseException e) {
				System.out.println("erro no parse");
			}
    	}
    	JSONArray jsonEventos = new JSONArray();
    	for (int s: eventos.keySet()) {
    		try {
				jsonEventos.add((JSONObject)  parser.parse(mapper.writeValueAsString(eventos.get(s))));
			} catch (JsonProcessingException | ParseException e) {
				System.out.println("erro no parse");
			}
    	}
    	JSONArray jsonApostas = new JSONArray();
    	for (String s: apostas.keySet()) {
    		try {
	    		JSONArray arrJsonApostas = new JSONArray();
	    		for (Aposta a: apostas.get(s)) {
					arrJsonApostas.add((JSONObject) parser.parse(mapper.writeValueAsString(a)));
	    		}
	    		JSONObject jogador = new JSONObject();
	    		jogador.put(s, arrJsonApostas);
	    		jsonApostas.add(jogador);
    		} catch (JsonProcessingException | ParseException e) {
				System.out.println("erro no parse");
			}
    	}
    	try {
    		mapper.writeValue(new FileWriter(System.getProperty("user.dir")+"\\"+FICHEIRO_JOGADORES), jsonJogadores);
			mapper.writeValue(new FileWriter(System.getProperty("user.dir")+"\\"+FICHEIRO_EVENTOS), jsonEventos);
	    	mapper.writeValue(new FileWriter(System.getProperty("user.dir")+"\\"+FICHEIRO_APOSTAS), jsonApostas);
		} catch (IOException e) {
		}
    	
    }
    
    public Jogador loginUtilizador(String email, String password){
        Jogador j = jogadores.get(email);
        if (j==null) return null;
    	if (j.getPassword().equals(password)){
            return jogadores.get(email);
        } else {
            return null;
        }
    }
    
    public ArrayList<Evento> eventosAtivos(){
        ArrayList<Evento> arrEventos = new ArrayList<Evento>(eventos.values());
        ArrayList<Evento> eventosAtivos = new ArrayList<>();
        for (Evento e: arrEventos){
            if (e.getEstado()==Estado.aberto) eventosAtivos.add(e);
        }
        return eventosAtivos;
    }
    
    public Evento obterEvento(int id){
        return eventos.get(id);
    }
    
    public ArrayList<Evento> obterEventos(){
		return new ArrayList<Evento>(eventos.values());
}
    
    public ArrayList<Aposta> apostasJogador(String email){
        return apostas.get(email);
    }

	public Jogador registarJogador(String pNome, String uNome, String email, String password) {
		Jogador j = new Jogador(email, pNome, uNome, password);
		jogadores.put(email, j);
		atualizarBD();
		return j;
	}
	
	public void registarEvento(String equipaCasa, String equipaFora, double oddCasa, double oddFora, double oddEmpate) {
		Evento e = new Evento(oddCasa, oddEmpate, oddFora, equipaCasa, equipaFora);
		e.setID(eventos.size()+1);
		eventos.put(e.getID(), e);
		atualizarBD();
	}
	
	public void registarAposta(String idApostador, int idEvento, char equipaAapostar, double valorAapostar) {
		Aposta a = new Aposta(idApostador, idEvento, equipaAapostar, valorAapostar);
		Evento e = eventos.get(idEvento);
		ArrayList<Aposta> arrApostas = apostas.get(idApostador);
		if (arrApostas==null) {
			arrApostas = new ArrayList<>();
			a.setidEvento(1);
		} else 
			a.setidEvento(apostas.get(idApostador).size());
		e.adicionarAposta(a);
		eventos.put(idEvento, e);
		arrApostas.add(a);
		apostas.put(idApostador, arrApostas);
		Jogador j = jogadores.get(idApostador);
		j.retirarCredito(valorAapostar);
		jogadores.put(idApostador, j);
		atualizarBD();
	}

	public void adicionarCredito(String apostador, double d) {
		Jogador j = jogadores.get(apostador);
		j.adicionarCredito(d);
		jogadores.put(apostador, j);
		atualizarBD();
	}
}