package main;

import java.util.ArrayList;

import facade.Facade;

public class BetEss {

	private static String email;
	private static final String ADMIN_PASSWORD = "admin";

	public static void main(String[] args) {
		Facade facade = new Facade();
		String escolha = "";
        while(!escolha.equals("S")){
        	System.out.println("Bem vindo ao BetESS!\n"
        	        +"A - Login na plataforma\n"
        	        +"B - Registar na plataforma\n"
        	        +"C - Login administrador\n"
        	        +"S - Sair da plataforma\n");
            escolha = facade.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": 
                	String res = login(facade);
                	if (res.equals("J")) 
                		menu(facade); 
                	else if (res.equals("B"))
                		menuBookie(facade);
                	else 
                		System.out.println("Credenciais inv�lidas"); break;
                case "B": signIn(facade); break;
                case "C": loginAdmin(facade); break;
                case "S": break;
                default: System.out.println("Op��o inv�lida!");
            }
        }
	}

	private static void menuBookie(Facade f) {
		String escolha = "";
        while(!escolha.equals("L")){
        	System.out.println("Bem vindo � interface de bookie!\n"
                    +"A - Criar evento\n"
        			+"B - Receber notifica��ew sobre fecho de um evento\n"
                    +"C - Mudar password\n"
                    +"L - Logout\n");
            escolha = f.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": criarEvento(f); break;
                case "B": notificarEvento(f); break;
                case "C": mudarPassword(f); break;
                case "L": break;
                default: System.out.println("Op��o inv�lida!");
            }
        }
	}

	private static void mudarPassword(Facade f) {
		System.out.println("Insira a sua palavra-passe atual");
		String passwordAtual = f.lerString();
		while (!f.verificarPassword(email, passwordAtual)) {
			System.out.println("Password incorreta!");
			passwordAtual = f.lerString();
		}
		System.out.println("Insira a sua palavra-passe(pode ser visualizada):");
		String password = f.lerString();
		while (password.length()<8) {
			System.out.println("A palavra passe tem de ter pelo menos 8 caracteres!");
			password= f.lerString();
		}
		f.mudarPassword(email, password);
	}

	private static void notificarEvento(Facade f) {
		ArrayList<Integer> available = f.verEventosDisponiveis(email);
		if (available.size()>0) {
			System.out.println("Escolha o evento");
			int id = f.lerInt();
			while (!available.contains(id)) {
				System.out.println("ID de evento inv�lido.");
				id = f.lerInt();
			}
			f.notificarEvento(id, email);
		} else {
			System.out.println("N�o existem eventos dispon�veis!");
		}
	}

	private static void criarEvento(Facade f) {
		System.out.println("Qual a equipa da casa?");
		String equipaCasa = f.lerString();
		System.out.println("Qual a equipa de fora?");
		String equipaFora = f.lerString();
		System.out.println("Qual a odd da equipa da casa?");
		double oddCasa = f.lerDouble();
		System.out.println("Qual a odd da equipa de fora?");
		double oddFora = f.lerDouble();
		System.out.println("Qual a odd do empate?");
		double oddEmpate = f.lerDouble();
		f.criarEvento(equipaFora, oddCasa, oddFora, oddEmpate, equipaCasa, equipaFora);
	}

	private static void loginAdmin(Facade f) {
		System.out.println("Insira a palavra-passe de administrador(pode ser visualizada):");
		String password = f.lerString();
        while (!password.equals(ADMIN_PASSWORD)){
        	System.out.println("Password incorreta.");
            System.out.println("Insira a sua password de administrador");
            password = f.lerString();
        }
        menuAdmin(f);
    }

	private static void menuAdmin(Facade f) {
    	String escolha = "";
        while(!escolha.equals("L")){
        	System.out.println("Bem vindo � interface de administrador do sistema!\n"
                    +"A - Fechar evento\n"
                    +"B - Atualizar resultado de evento\n"
                    +"C - Adicionar cr�dito a jogador\n"
                    +"D - Registar bookie\n"
                    +"L - Logout\n");
            escolha = f.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": fecharEvento(f); break;
                case "B": atualizarEvento(f); break;
                case "C": adicionarCredito(f); break;
                case "D": registarBookie(f); break;
                case "L": break;
                default: System.out.println("Op��o inv�lida!");
            }
        }
	}
	
	private static void registarBookie(Facade f) {
		System.out.println("Insira o email do bookie:");
		String email = f.lerEmail();
		while (f.userExiste(email)) {
			System.out.println("O email escolhido j� � usado por outro utilizador!");
			email=f.lerEmail();
		}
		System.out.println("Qual o primeiro nome?");
		String pNome = f.lerString();
		System.out.println("Qual o �ltimo nome?");
		String uNome = f.lerString();
		f.registarBookie(email, pNome, uNome);
	}

	private static void adicionarCredito(Facade f) {
		int ind = 1;
		ArrayList<String> jogadores = f.jogadores();
		for (String s: jogadores) {
			System.out.println(ind+"-"+s);
			ind++;
		}
		System.out.println("A que jogador quer adicionar credito?");
		int i = f.lerInt();
		while (i>ind || i<1) {
			System.out.println("�ndice errado!");
			i = f.lerInt();
		}
		System.out.println("Quantas moedas quer adicionar?");
		double qti = f.lerDouble();
		f.adicionarCredito(jogadores.get(i-1), qti);
	}

	private static void atualizarEvento(Facade f) {
		if (f.verEventosAtivos()) {
			System.out.println("Escolha o evento");
			int id = f.lerInt();
			while(!f.estaAtivo(id)) {
				System.out.println("O evento escolhido n�o est� na lista!");
				id = f.lerInt();
			}
			System.out.println("Que aconteceu?");
			System.out.println("A - A equipa da casa marcou!");
			System.out.println("B - A equipa de fora marcou!");
			System.out.println("C - A equipa da casa teve um golo inv�lido!");
			System.out.println("B - A equipa de fora teve um golo inv�lido!");
			String escolha = "";
			while(!escolha.equals("A") && !escolha.equals("B") && !escolha.equals("C")&& !escolha.equals("D")){
				System.out.println("Op��o inv�lida!");
				escolha = f.lerString();
	            escolha = escolha.toUpperCase();
			}
			f.atualizarEvento(id, escolha);;
		}		
	}

	private static void fecharEvento(Facade f) {
		if (f.verEventosAtivos()) {
			System.out.println("Escolha o evento");
			int id = f.lerInt();
			while(!f.estaAtivo(id)) {
				System.out.println("O evento escolhido n�o est� na lista!");
				id = f.lerInt();
			}
			f.fecharEvento(id);
		}
	}

	private static void menu(Facade f){
    	String escolha = "";
        while(!escolha.equals("L")){
        	System.out.println("Menu principal\n"
                    +"A - Verificar saldo da conta\n"
                    +"B - Ver apostas feitas\n"
                    +"C - Ver eventos ativos\n"
                    +"D - Ver todos os eventos\n"
                    +"E - Fazer aposta\n"
                    +"F - Alterar palavra-passe\n"
                    +"L - Logout\n");
            escolha = f.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": verificarSaldoConta(f); break;
                case "B": f.verApostasFeitas(email); break;
                case "C": f.verEventosAtivos(); break;
                case "D": f.verTodosOsEventos(); break;
                case "E": fazerAposta(f); break;
                case "F": mudarPassword(f); break;
                case "L": break;
                default: System.out.println("Op��o inv�lida!");
            }
        }
    }

	private static void fazerAposta(Facade f) {
		if (f.verificarSaldoConta(email)!=0) {
			ArrayList<Integer> available = f.verEventosDisponiveis(email);
			if (available.size()>0) {
				System.out.println("Escolha o evento");
				int id = f.lerInt();
				while (!available.contains(id)) {
					System.out.println("ID de evento inv�lido.");
					id = f.lerInt();
				}
				System.out.println("Em que equipa quer apostar?");
				System.out.println("A - " + f.equipaCasa(id));
				System.out.println("B - " + f.equipaFora(id));
				System.out.println("C - Empate");
				String escolha = "";
				char equipa = 'a';
				while(!escolha.equals("A") && !escolha.equals("B") && !escolha.equals("C")){
		            escolha = f.lerString();
		            escolha = escolha.toUpperCase();
		            switch(escolha){
		                case "A": equipa='1'; break;
		                case "B": equipa='2'; break;
		                case "C": equipa='x'; break;
		                default: System.out.println("Op��o inv�lida!");
		            }
				}
				System.out.println("Qual o montante que quer apostar (Tem neste momento " + f.verificarSaldoConta(email) + " fichas)?");
				double aposta = f.lerDouble();
				while (aposta>f.verificarSaldoConta(email)) {
					System.out.println("N�o tem saldo suficiente para apostar esse montante.");
					aposta = f.lerDouble();
				}
				f.fazerAposta(email, id, equipa, aposta);
			} else {
				System.out.println("N�o existem eventos dispon�veis!");
			}
		} else {
			System.out.println("Nao tem cr�ditos suficientes para fazer uma aposta!");
		}		
	}

	private static void verificarSaldoConta(Facade f) {
		System.out.println("Tem " + f.verificarSaldoConta(email) + " moedas dispon�veis.");		
	}

	private static String login(Facade f) {
		System.out.println("Insira o seu email:");
		email = f.lerEmail();
		System.out.println("Insira a sua palavra-passe(pode ser visualizada):");
		String password = f.lerString();
		return f.login(email, password);
	}
	
	private static void signIn(Facade f) {
		System.out.println("Insira o seu email:");
		email = f.lerEmail();
		while (f.userExiste(email)) {
			System.out.println("O email escolhido j� � usado por outro utilizador!");
			email=f.lerEmail();
		}
		System.out.println("Insira a sua palavra-passe(pode ser visualizada):");
		String password = f.lerString();
		while (password.length()<8) {
			System.out.println("A palavra passe tem de ter pelo menos 8 caracteres!");
			password= f.lerString();
		}
		System.out.println("Qual o seu primeiro nome?");
		String pNome = f.lerString();
		System.out.println("Qual o seu �ltimo nome?");
		String uNome = f.lerString();
		f.signIn(email, password, pNome, uNome);
	}
	
}
