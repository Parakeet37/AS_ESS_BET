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
import eventObserver.Evento;
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
	private HashMap<String, Observer> jogadores;
	private HashMap<Integer, Evento> eventos;
	private String email;
	
	public Facade() {
		apostas = new HashMap<>();
		jogadores = new HashMap<>();
		eventos = new HashMap<>();
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.SEVERE); 
		client = DBConnection.getInstance().getConnection();
		db = client.getDatabase(DATABASE);
		loadDatabase();
	}
	
	//adiciona um novo observer(cliente)
	private void addToDatabase(Observer j, String password) {
		MongoCollection<Document> col = db.getCollection(USERS_COLLECTION);
		col.insertOne(new Document("email", j.getEmail())
				.append("password", password)
				.append("credito", j.getCredito())
				.append("pnome", j.getPNome())
				.append("unome", j.getUNome())
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
	private void saveToDatabase(Observer j) {
		MongoCollection<Document> col = db.getCollection(USERS_COLLECTION);
		col.updateOne(new Document("email", j.getEmail()), 
				new Document("$set", 
						new Document("credito", j.getCredito())
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
			jogadores.put(d.getString("email"), new Observer(d.getString("email"), d.getString("pnome"), d.getString("unome"), d.getDouble("credito")));
		}
		col = db.getCollection(EVENTS_COLLECTION);
		iter = col.find();
		for (Document d: iter) {
			EventoState state = new AbertoState();
			ArrayList<Document> arrObs = (ArrayList<Document>) d.get("observers");
			ArrayList<Observer> observers = new ArrayList<>();
			for (Document doc: arrObs) {
				observers.add(jogadores.get(doc.getString("email")));
			}
			if (d.getString("estado").equals("fechado")) state = new FechadoState();
			eventos.put(d.getInteger("id"), new Evento(d.getInteger("id"), 
					d.getDouble("oddCasa"), 
					d.getDouble("oddFora"), 
					d.getDouble("oddEmpate"), 
					d.getString("equipaCasa"), 
					d.getString("equipaFora"),
					state,
					d.getInteger("resultadoCasa"), 
					d.getInteger("resultadoFora"), 
					observers));
		}
		col = db.getCollection(BETS_COLLECTION);
		for (String idApostador: jogadores.keySet()) {
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
	
	public boolean login() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		System.out.println("Insira o seu email:");
		email = lerEmail();
		System.out.println("Insira a sua palavra-passe(pode ser visualizada):");
		String password = lerString();
		MongoCollection<Document> col = db.getCollection("users");
		return col.countDocuments(new Document("email", email).append("password", password)) != 0;
	}
	
	public void signIn() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		System.out.println("Insira o seu email:");
		email = lerEmail();
		while (jogadores.containsKey(email)) {
			System.out.println("O email escolhido já é usado por outro utilizador!");
			email=lerEmail();
		}
		System.out.println("Insira a sua palavra-passe(pode ser visualizada):");
		String password = lerString();
		while (password.length()<8) {
			System.out.println("A palavra passe tem de ter pelo menos 8 caracteres!");
			password= lerString();
		}
		System.out.println("Qual o seu primeiro nome?");
		String pNome = lerString();
		System.out.println("Qual o seu último nome?");
		String uNome = lerString();
		Observer j = new Observer(email, pNome, uNome);
		jogadores.put(email, j);
		addToDatabase(j, password);
	}
	
	public void criarEvento() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		System.out.println("Qual a equipa da casa?");
		String equipaCasa = lerString();
		System.out.println("Qual a equipa de fora?");
		String equipaFora = lerString();
		System.out.println("Qual a odd da equipa da casa?");
		double oddCasa = lerDouble();
		System.out.println("Qual a odd da equipa de fora?");
		double oddFora = lerDouble();
		System.out.println("Qual a odd do empate?");
		double oddEmpate = lerDouble();
		Evento e = new Evento(oddCasa, oddEmpate, oddFora, equipaCasa, equipaFora);
		e.setId(eventos.size()+1);
		eventos.put(e.getId(), e);
		addToDatabase(e);
	}
	
	public void fecharEvento() {
		verEventosAtivos();
		System.out.println("Escolha o evento");
		int id = lerInt();
		while(!estaAtivo(id)) {
			System.out.println("O evento escolhido não está na lista!");
			id = lerInt();
		}
		Evento e = eventos.get(id);
		FechadoState state = new FechadoState();
		state.changeState(e);
		saveToDatabase(e);
		ArrayList<String> observers=e.getObservers();
		for (String idObserver: observers) {
			Observer o = jogadores.get(idObserver);
			saveToDatabase(o);
		}
	}
	
	private boolean estaAtivo(int id) {
		return eventos.get(id).getEstado() instanceof AbertoState;
	}

	public void atualizarEvento() {
		verEventosAtivos();
		System.out.println("Escolha o evento");
		int id = lerInt();
		while(!estaAtivo(id)) {
			System.out.println("O evento escolhido não está na lista!");
			id = lerInt();
		}
		Evento e = eventos.get(id);
		System.out.println("Que aconteceu?");
		System.out.println("A - A equipa da casa marcou!");
		System.out.println("B - A equipa de fora marcou!");
		System.out.println("C - A equipa da casa teve um golo inválido!");
		System.out.println("B - A equipa de fora teve um golo inválido!");
		String escolha = "";
		while(!escolha.equals("A") && !escolha.equals("B") && !escolha.equals("C")&& !escolha.equals("D")){
            escolha = lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": e.goloCasa(); break;
                case "B": e.goloFora(); break;
                case "C": e.invalidadoCasa(); break;
                case "D": e.invalidadoFora(); break;
                default: System.out.println("Opção inválida!");
            }
		}
		saveToDatabase(e);
	}
	
	public void verificarSaldoConta() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		System.out.println("Tem " + jogadores.get(email).getCredito() + " moedas disponíveis.");
	}
	
	public void verApostasFeitas() {
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
	
	public void verEventosAtivos() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		ArrayList<Evento> arrEventos = new ArrayList<>(eventos.values());
		if (arrEventos.size()==0) {
			System.out.println("Não exitem eventos ativos");
		}
		for (Evento e: arrEventos){
            if (e.getEstado() instanceof AbertoState) System.out.println(e);
        }
	}
	
	public void verTodosOsEventos() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		ArrayList<Evento> arrEventos = new ArrayList<>(eventos.values());
		if (arrEventos.size()==0) {
			System.out.println("Não exitem eventos ativos");
		}
		for (Evento e: arrEventos){
            System.out.println(e);
        }
	}
	
	public void fazerAposta() {
		if (verEventosDisponiveis()) {
			System.out.println("Escolha o evento");
			int id = lerInt();
			Evento e = eventos.get(id);
			while (e==null) {
				System.out.println("ID de evento inválido.");
				id = lerInt();
				e = eventos.get(id);
			}
			System.out.println("Em que equipa quer apostar?");
			System.out.println("A - " + e.getEquipaCasa());
			System.out.println("B - " + e.getEquipaFora());
			System.out.println("C - Empate");
			String escolha = "";
			char equipa = 'a';
			while(!escolha.equals("A") && !escolha.equals("B") && !escolha.equals("C")){
	            escolha = lerString();
	            escolha = escolha.toUpperCase();
	            switch(escolha){
	                case "A": equipa='1'; break;
	                case "B": equipa='2'; break;
	                case "C": equipa='x'; break;
	                default: System.out.println("Opção inválida!");
	            }
			}
			Observer j = jogadores.get(email);
			System.out.println("Qual o montante que quer apostar (Tem neste momento " + j.getCredito() + " fichas)?");
			double aposta = lerDouble();
			while (aposta>j.getCredito()) {
				System.out.println("Não tem saldo suficiente para apostar esse montante.");
				aposta = lerDouble();
			}
			j.retirarCredito(aposta);
			Aposta a = new Aposta(email, e.getId(), equipa, aposta);
			e.addObserver(j);
			addToDatabase(a);
			saveToDatabase(j);
			saveToDatabase(e);
		} else {
			System.out.println("Não existem eventos disponíveis!");
		}
	}
	
	private boolean verEventosDisponiveis() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		ArrayList<Evento> arrEventos = new ArrayList<>(eventos.values());
		if (arrEventos.size()==0) {
			System.out.println("Não exitem eventos ativos");
		}
		boolean t = false;
		for (Evento e: arrEventos){
            if (e.getEstado() instanceof AbertoState && !e.getObservers().contains(email)) {
            	System.out.println(e);
            	t = true;
            }
        }
		return t;
	}

	public void adicionarCredito() {
		int ind = 1;
		ArrayList<String> arr = new ArrayList<>(jogadores.keySet());
		for (String s: arr) {
			System.out.println(ind+"-"+s);
		}
		System.out.println("A que jogador quer adicionar credito?");
		int i = lerInt();
		while (i>ind) {
			System.out.println("Índice errado!");
			i = lerInt();
		}
		System.out.println("Quantas moedas quer adicionar?");
		double qti = lerDouble();
		Observer j = jogadores.get(arr.get(i-1));
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
}
