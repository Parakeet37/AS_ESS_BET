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
	private String email;
	
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
	
	public String login() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		System.out.println("Insira o seu email:");
		email = lerEmail();
		System.out.println("Insira a sua palavra-passe(pode ser visualizada):");
		String password = lerString();
		MongoCollection<Document> col = db.getCollection("users");
		if (col.countDocuments(new Document("email", email).append("password", password)) != 0)
			if (observers.get(email) instanceof Jogador)
				return "J";
			else
				return "B";
		return "";
	}
	
	public void signIn() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		System.out.println("Insira o seu email:");
		email = lerEmail();
		while (observers.containsKey(email)) {
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
		Jogador j = new Jogador(email, pNome, uNome);
		observers.put(email, j);
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
		Evento e = new Evento(email, oddCasa, oddEmpate, oddFora, equipaCasa, equipaFora);
		e.setId(eventos.size()+1);
		eventos.put(e.getId(), e);
		addToDatabase(e);
	}
	
	public void fecharEvento() {
		if (verEventosAtivos()) {
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
			ArrayList<String> obsArr=e.getObservers();
			for (String idObserver: obsArr) {
				if (observers.get(idObserver) instanceof Jogador) {
					Jogador o = (Jogador) observers.get(idObserver);
					saveToDatabase(o);
				}
			}
		}
	}
	
	private boolean estaAtivo(int id) {
		return eventos.get(id).getEstado() instanceof AbertoState;
	}

	public void atualizarEvento() {
		if (verEventosAtivos()) {
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
	}
	
	public void verificarSaldoConta() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		System.out.println("Tem " + ((Jogador) observers.get(email)).getCredito() + " moedas disponíveis.");
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
	
	public void fazerAposta() {
		if (((Jogador) observers.get(email)).getCredito()!=0) {
			ArrayList<Integer> available = verEventosDisponiveis();
			if (available.size()>0) {
				System.out.println("Escolha o evento");
				int id = lerInt();
				Evento e = eventos.get(id);
				while (!available.contains(id) || e==null) {
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
				Jogador j = (Jogador) observers.get(email);
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
		} else {
			System.out.println("Nao tem créditos suficientes para fazer uma aposta!");
		}
	}
	
	private ArrayList<Integer> verEventosDisponiveis() {
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

	public void adicionarCredito() {
		int ind = 1;
		ArrayList<String> arr = new ArrayList<>(observers.keySet());
		ArrayList<String> jogadores = new ArrayList<>();
		for (String s: arr) {
			if (observers.get(s) instanceof Jogador) {
				System.out.println(ind+"-"+s);
				ind++;
				jogadores.add(s);
			} 
		}
		System.out.println("A que jogador quer adicionar credito?");
		int i = lerInt();
		while (i>ind) {
			System.out.println("Índice errado!");
			i = lerInt();
		}
		System.out.println("Quantas moedas quer adicionar?");
		double qti = lerDouble();
		Jogador j = (Jogador) observers.get(jogadores.get(i-1));
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

	public void notificarEvento() {
		ArrayList<Integer> available = verEventosDisponiveis();
		if (available.size()>0) {
			System.out.println("Escolha o evento");
			int id = lerInt();
			Evento e = eventos.get(id);
			while (!available.contains(id) || e==null) {
				System.out.println("ID de evento inválido.");
				id = lerInt();
				e = eventos.get(id);
			}
			e.addObserver(observers.get(email));
			saveToDatabase(e);
		} else {
			System.out.println("Não existem eventos disponíveis!");
		}
		
	}

	public void registarBookie() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		System.out.println("Insira o email do bookie:");
		email = lerEmail();
		while (observers.containsKey(email)) {
			System.out.println("O email escolhido já é usado por outro utilizador!");
			email=lerEmail();
		}
		String password = "default";
		System.out.println("Qual o primeiro nome?");
		String pNome = lerString();
		System.out.println("Qual o último nome?");
		String uNome = lerString();
		Bookie j = new Bookie(email, pNome, uNome);
		observers.put(email, j);
		addToDatabase(j, password);
	}

	public void mudarPassword() {
		client = DBConnection.getInstance().getConnection();
		loadDatabase();
		System.out.println("Insira a sua palavra-passe atual");
		String passwordAtual = lerString();
		MongoCollection<Document> col = db.getCollection("users");
		while (col.countDocuments(new Document("email", email).append("password", passwordAtual)) == 0) {
			System.out.println("Password incorreta!");
			passwordAtual = lerString();
		}
		System.out.println("Insira a sua palavra-passe(pode ser visualizada):");
		String password = lerString();
		while (password.length()<8) {
			System.out.println("A palavra passe tem de ter pelo menos 8 caracteres!");
			password= lerString();
		}
		Observer o = observers.get(email);
		if (o instanceof Bookie)
			saveToDatabase((Bookie) o, password);
		else
			saveToDatabase((Jogador) o, password);
	}
}
