package facade;

import static java.lang.System.in;
import static java.lang.System.out;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import aposta.Aposta;
import eventObserver.Bookie;
import eventObserver.Evento;
import eventObserver.Jogador;
import eventObserver.Observer;
import singleton.DBConnection;
import state.AbertoState;
import state.EventoState;
import state.FechadoState;

public class Facade {
	
	private static final String USERS_COLLECTION = "users";
	private static final String EVENTS_COLLECTION = "events";
	private static final String BETS_COLLECTION = "bets";
	private MongoClient client;
	private static final String DATABASE = "essbet";
	private MongoDatabase db;
	private HashMap<String, ArrayList<Aposta>> apostas;
	private HashMap<String, Observer> observers;
	private HashMap<Integer, Evento> eventos;
	
	public Facade() {
		apostas = new HashMap<>();
		observers = new HashMap<>();
		eventos = new HashMap<>();
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.SEVERE); 
		client = DBConnection.getInstance().getConnection();
		db = client.getDatabase(DATABASE);
		loadDatabase();
	}
	
	//adiciona um novo observer(cliente)
	private void addToDatabase(Jogador j, String password) {
		MongoCollection<Document> col = db.getCollection(USERS_COLLECTION);
		col.insertOne(new Document("email", j.getEmail())
				.append("password", password)
				.append("credito", j.getCredito())
				.append("pnome", j.getPNome())
				.append("unome", j.getUNome())
				.append("tipo", "jogador")
		);
	}
	
	private void addToDatabase(Bookie j, String password) {
		MongoCollection<Document> col = db.getCollection(USERS_COLLECTION);
		col.insertOne(new Document("email", j.getEmail())
				.append("password", password)
				.append("pnome", j.getPNome())
				.append("unome", j.getUNome())
				.append("tipo", "bookie")
		);
	}
	
	private void addToDatabase(Evento e) {
		MongoCollection<Document> col = db.getCollection(EVENTS_COLLECTION);
		String state = "";
		if (e.getEstado() instanceof FechadoState) state = "fechado";
		else state = "aberto";
		col.insertOne(new Document("id", e.getId())
				.append("equipaCasa", e.getEquipaCasa())
				.append("equipaFora", e.getEquipaFora())
				.append("estado", state)
				.append("oddCasa", e.getOddCasa())
				.append("oddFora", e.getOddFora())
				.append("oddEmpate", e.getOddEmpate())
				.append("resultadoCasa", e.getresultadoCasa())
				.append("resultadoFora", e.getresultadoFora())
				.append("observers", new ArrayList<Document>())
				.append("idBookie", e.getIdBookie())
		);
	}
	
	private void addToDatabase(Aposta a) {
		MongoCollection<Document> col = db.getCollection(BETS_COLLECTION);
		col.insertOne(new Document("id", a.getId())
				.append("equipa", a.getEquipaAapostar())
				.append("idApostador", a.getidApostador())
				.append("idEvento", a.getidEvento())
				.append("valor", a.getValorAapostar())
		);
	}
	
	//grava um observer(cliente) atualizado
	private void saveToDatabase(Jogador j) {
		MongoCollection<Document> col = db.getCollection(USERS_COLLECTION);
		col.updateOne(new Document("email", j.getEmail()), 
				new Document("$set", 
						new Document("credito", j.getCredito())
				)
		);
	}
	
	private void saveToDatabase(Bookie b, String password) {
		MongoCollection<Document> col = db.getCollection(USERS_COLLECTION);
		col.updateOne(new Document("email", b.getEmail()), 
				new Document("$set", 
						new Document("password", password)
				)
		);
	}
	
	private void saveToDatabase(Jogador b, String password) {
		MongoCollection<Document> col = db.getCollection(USERS_COLLECTION);
		col.updateOne(new Document("email", b.getEmail()), 
				new Document("$set", 
						new Document("password", password)
				)
		);
	}
	
	//grava um evento atualizado
	private void saveToDatabase(Evento e) {
		MongoCollection<Document> col = db.getCollection(EVENTS_COLLECTION);
		String state = "";
		if (e.getEstado() instanceof FechadoState) state = "fechado";
		else state = "aberto";
		ArrayList<Document> arrObservers = new ArrayList<>();
		for (String o: e.getObservers()) {
			arrObservers.add(new Document("email", o));
		}
		col.updateOne(new Document("id", e.getId()), 
				new Document("$set", 
						new Document("resultadoCasa", e.getresultadoCasa())
						.append("resultadoFora", e.getresultadoFora())
						.append("estado", state)
						.append("observers", arrObservers)
				)
		);
	}
	
	private void loadDatabase() {
		MongoCollection<Document> col = db.getCollection(USERS_COLLECTION);
		FindIterable<Document> iter = col.find();
		for (Document d: iter) {
			if (d.getString("tipo").equals("jogador"))
				observers.put(d.getString("email"), new Jogador(d.getString("email"), d.getString("pnome"), d.getString("unome"), d.getDouble("credito")));
			else 
				observers.put(d.getString("email"), new Bookie(d.getString("email"), d.getString("pnome"), d.getString("unome")));
		}
		col = db.getCollection(EVENTS_COLLECTION);
		iter = col.find();
		for (Document d: iter) {
			EventoState state = new AbertoState();
			ArrayList<Document> arrObs = (ArrayList<Document>) d.get("observers");
			ArrayList<Observer> obsArr = new ArrayList<>();
			for (Document doc: arrObs) {
				obsArr.add(observers.get(doc.getString("email")));
			}
			if (d.getString("estado").equals("fechado")) state = new FechadoState();
			eventos.put(d.getInteger("id"), new Evento(d.getInteger("id"),
					d.getString("idBookie"),
					d.getDouble("oddCasa"), 
					d.getDouble("oddFora"), 
					d.getDouble("oddEmpate"), 
					d.getString("equipaCasa"), 
					d.getString("equipaFora"),
					state,
					d.getInteger("resultadoCasa"), 
					d.getInteger("resultadoFora"), 
					obsArr));
		}
		col = db.getCollection(BETS_COLLECTION);
		for (String idApostador: observers.keySet()) {
			iter = col.find(new Document("idApostador", idApostador));
			ArrayList<Aposta> arrApostas = new ArrayList<>();
			for (Document d: iter) {
				Aposta a = new Aposta(d.getInteger("id"), 
						idApostador,
						d.getInteger("idEvento"), 
						d.getString("equipa").charAt(0), 
						d.getDouble("valor"));
				arrApostas.add(a);
				eventos.get(d.getInteger("idEvento")).addAposta(a);;
			}
			apostas.put(idApostador, arrApostas);
		}
	}
	
	public String login(String email, String password) {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		MongoCollection<Document> col = db.getCollection("users");
		if (col.countDocuments(new Document("email", email).append("password", password)) != 0)
			if (observers.get(email) instanceof Jogador)
				return "J";
			else
				return "B";
		return "";
	}
	
	public void signIn(String email, String password, String pNome, String uNome) {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		Jogador j = new Jogador(email, pNome, uNome);
		observers.put(email, j);
		addToDatabase(j, password);
	}
	
	public void criarEvento(String email, double oddCasa, double oddFora, double oddEmpate, String equipaCasa, String equipaFora) {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		Evento e = new Evento(email, oddCasa, oddEmpate, oddFora, equipaCasa, equipaFora);
		e.setId(eventos.size()+1);
		eventos.put(e.getId(), e);
		addToDatabase(e);
	}
	
	public void fecharEvento(int id) {
		Evento e = eventos.get(id);
		FechadoState state = new FechadoState();
		state.changeState(e);
		saveToDatabase(e);
		ArrayList<String> obsArr=e.getObservers();
		for (String idObserver: obsArr) {
			if (observers.get(idObserver) instanceof Jogador) {
				Jogador o = (Jogador) observers.get(idObserver);
				saveToDatabase(o);
			}
		}
	}
	
	public boolean estaAtivo(int id) {
		return eventos.get(id).getEstado() instanceof AbertoState;
	}

	public void atualizarEvento(int id, String escolha) {
		Evento e = eventos.get(id);
        switch(escolha){
            case "A": e.goloCasa(); break;
            case "B": e.goloFora(); break;
            case "C": e.invalidadoCasa(); break;
            default: e.invalidadoFora(); break;
        }
		saveToDatabase(e);
	}
	
	public double verificarSaldoConta(String email) {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		return ((Jogador) observers.get(email)).getCredito();
	}
	
	public void verApostasFeitas(String email) {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		if (apostas.get(email).size()==0) {
			System.out.println("Não realizou nenhuma aposta");
		}
		for (Aposta a: apostas.get(email)) {
			Evento e = eventos.get(a.getidEvento());
			System.out.println("Apostou no evento " + e + ".");
			if (e.getEstado() instanceof FechadoState) {
				//evento fechado
				System.out.println("O evento encontra-se fechado!");
				if (e.getresultadoCasa()>e.getresultadoFora() && a.getEquipaAapostar()=='1') {
					System.out.println("Parabéns, "+a.getidApostador()+", ganhou a aposta! Amealhou "+(a.getValorAapostar()*e.getOddCasa())+" moedas.");
				} else if (e.getresultadoCasa()<e.getresultadoFora() && a.getEquipaAapostar()=='2') {
					System.out.println("Parabéns, "+a.getidApostador()+", ganhou a aposta! Amealhou "+(a.getValorAapostar()*e.getOddFora())+" moedas.");
				} else if (e.getresultadoCasa()==e.getresultadoFora() && a.getEquipaAapostar()=='x'){
					System.out.println("Parabéns, "+a.getidApostador()+", ganhou a aposta! Amealhou "+(a.getValorAapostar()*e.getOddEmpate())+" moedas.");
				} else {
					System.out.println("Perdeu a aposta, " + a.getidApostador() + ".");
				}
			}
		}
		
	}
	
	public boolean verEventosAtivos() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		ArrayList<Evento> arrEventos = new ArrayList<>(eventos.values());
		if (arrEventos.size()==0) {
			System.out.println("Não exitem eventos ativos");
			return false;
		}
		for (Evento e: arrEventos){
            if (e.getEstado() instanceof AbertoState) System.out.println(e);
        }
		return true;
	}
	
	public boolean verTodosOsEventos() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		ArrayList<Evento> arrEventos = new ArrayList<>(eventos.values());
		if (arrEventos.size()==0) {
			System.out.println("Não exitem eventos ativos");
			return false;
		}
		for (Evento e: arrEventos){
            System.out.println(e);
        }
		return true;
	}
	
	public void fazerAposta(String email, int idEvento, char equipa, double aposta) {
		Jogador j = (Jogador) observers.get(email);
		j.retirarCredito(aposta);
		Aposta a = new Aposta(email, idEvento, equipa, aposta);
		Evento e = eventos.get(idEvento);
		e.addObserver(j);
		addToDatabase(a);
		saveToDatabase(j);
		saveToDatabase(e);
	}
	
	public ArrayList<Integer> verEventosDisponiveis(String email) {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		ArrayList<Evento> arrEventos = new ArrayList<>(eventos.values());
		if (arrEventos.size()==0) {
			System.out.println("Não exitem eventos ativos");
		}
		ArrayList<Integer> disponiveis = new ArrayList<>();
		for (Evento e: arrEventos){
            if (e.getEstado() instanceof AbertoState && !e.getObservers().contains(email)) {
            	System.out.println(e);
            	disponiveis.add(e.getId());
            }
        }
		return disponiveis;
	}

	public ArrayList<String> jogadores(){
		ArrayList<String> arr = new ArrayList<>(observers.keySet());
		ArrayList<String> jogadores = new ArrayList<>();
		for (String s: arr) {
			if (observers.get(s) instanceof Jogador) {
				jogadores.add(s);
			} 
		}
		return jogadores;
	}
	
	public void adicionarCredito(String email, double qti) {
		Jogador j = (Jogador) observers.get(email);
		j.adicionarCredito(qti);
		saveToDatabase(j);
	}
	
	public String lerString() {
		Scanner input = new Scanner(in);
		boolean ok = false; 
		String txt = "";
		while(!ok) {
			try {
				txt = input.nextLine();
				ok = true;
			}
			catch(InputMismatchException e) { 
				out.println("Invalido"); 
				out.print("Novo valor: ");
			}
		}
		//input.close();
		return txt;
	}
	
	public String lerEmail() {
		Scanner input = new Scanner(in);
		boolean ok = false; 
		String txt = "";
		while(!ok) {
			try {
				txt = input.nextLine();
				if (txt.contains("@")) ok = true;
			}
			catch(InputMismatchException e){ 
				out.println("Email Invalido"); 
				out.print("Novo valor: ");
			}
		}
		//input.close();
		return txt;
	}
	 
	public int lerInt() {
		Scanner input = new Scanner(in);
		boolean ok = false; 
		int i = 0; 
		while(!ok) {
			try {
				i = Integer.parseInt(input.nextLine());
				if (i>0) ok = true;
			}
			catch(InputMismatchException | NumberFormatException e) { 
				out.println("Inteiro Invalido"); 
		   	 	out.print("Novo valor: ");
			}
		}
		//input.close();
		return i;
	} 
	  
	public double lerDouble() {
		Scanner input = new Scanner(in);
		boolean ok = false; 
		double d = 0.0; 
		while(!ok) {
			try {
				d = Double.parseDouble(input.nextLine());
				if (d>0) ok = true;
			}
			catch(InputMismatchException | NumberFormatException e) { 
				out.println("Valor real Invalido"); 
				out.print("Novo valor: ");
			}
		}
		//input.close();
		return d;
	}

	public void notificarEvento(int id, String email) {
		ArrayList<Integer> available = verEventosDisponiveis(email);
		if (available.size()>0) {
			Evento e = eventos.get(id);
			e.addObserver(observers.get(email));
			saveToDatabase(e);
		} else {
			System.out.println("Não existem eventos disponíveis!");
		}
		
	}

	public void registarBookie(String email, String pNome, String uNome) {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		Bookie j = new Bookie(email, pNome, uNome);
		observers.put(email, j);
		addToDatabase(j, "default");
	}

	public void mudarPassword(String email, String password) {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		Observer o = observers.get(email);
		if (o instanceof Bookie)
			saveToDatabase((Bookie) o, password);
		else
			saveToDatabase((Jogador) o, password);
	}

	public boolean userExiste(String email) {
		return observers.containsKey(email);
	}

	public boolean verificarPassword(String email, String password) {
		MongoCollection<Document> col = db.getCollection("users");
		return col.countDocuments(new Document("email", email).append("password", password)) != 0;
	}

	public String equipaCasa(int id) {
		return eventos.get(id).getEquipaCasa();
	}

	public String equipaFora(int id) {
		return eventos.get(id).getEquipaFora();
	}
}
