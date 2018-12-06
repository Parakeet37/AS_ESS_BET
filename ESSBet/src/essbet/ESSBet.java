/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essbet;

import static java.lang.System.in;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ESSBet {

    static DBHandler handler;
    static String ADMIN_PASSWORD = "admin";
    
    public static void main(String[] args) {
        handler = new DBHandler();
        handler.carregarBD();
        menuPrincipal();
    }

    private static void menuPrincipal() {
        String escolha = "";
        while(!escolha.equals("S")){
        	System.out.println("Bem vindo ao ESSBet!\n"
        	        +"A - Login na plataforma\n"
        	        +"B - Registar na plataforma\n"
        	        +"C - Login administrador\n"
        	        +"S - Sair da plataforma\n");
            escolha = Input.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": login(); break;
                case "B": registar(); break;
                case "C": loginAdmin(); break;
                case "S": break;
                default: System.out.println("Opção inválida!");
            }
        }
	}

	private static void loginAdmin() {
        System.out.println("Insira a password de administrador");
        String password = Input.lerString();
        while (!password.equals(ADMIN_PASSWORD)){
        	System.out.println("Password incorreta.");
            System.out.println("Insira a sua password de administrador");
            password = Input.lerString();
        }
        menuAdmin();
    }		

	private static void menuAdmin() {
    	String escolha = "";
        while(!escolha.equals("L")){
        	System.out.println("Bem vindo, à interface de administrador do sistema!\n"
                    +"A - Criar evento\n"
                    +"B - Fechar evento\n"
                    +"C - Atualizar resultado de evento\n"
                    +"D - Invalidar golo de evento\n"
                    +"L - Logout\n");
            escolha = Input.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": criarEvento(); break;
                case "B": fecharEvento(); break;
                case "C": atualizarEvento(); break;
                case "D": invalidarGolo(); break;
                case "L": break;
                default: System.out.println("Opção inválida!");
            }
        }
	}

	private static void invalidarGolo() {
		verEventos();
		System.out.println("Escolha o evento");
		int id = Input.lerInt();
		Evento e = handler.obterEvento(id);
		while (e==null) {
			System.out.println("ID de evento inválido.");
			id = Input.lerInt();
			e = handler.obterEvento(id);
		}
		System.out.println("Que equipa teve um golo inválido?");
		System.out.println("A - " + e.getEquipaCasa());
		System.out.println("B - " + e.getEquipaFora());
		String escolha = "";
		while(!escolha.equals("A") && !escolha.equals("B")){
            escolha = Input.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": e.invalidadoCasa(); break;
                case "B": e.invalidadoFora(); break;
                default: System.out.println("Opção inválida!");
            }
		}
		handler.atualizarBD();
	}
	
	private static void atualizarEvento() {
		verEventos();
		System.out.println("Escolha o evento");
		int id = Input.lerInt();
		Evento e = handler.obterEvento(id);
		while (e==null) {
			System.out.println("ID de evento inválido.");
			id = Input.lerInt();
			e = handler.obterEvento(id);
		}
		System.out.println("Que equipa marcou?");
		System.out.println("A - " + e.getEquipaCasa());
		System.out.println("B - " + e.getEquipaFora());
		String escolha = "";
		while(!escolha.equals("A") && !escolha.equals("B")){
            escolha = Input.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": e.goloCasa(); break;
                case "B": e.goloFora(); break;
                default: System.out.println("Opção inválida!");
            }
		}
		handler.atualizarBD();
	}

	private static void fecharEvento() {
		verEventos();
		System.out.println("Qual o evento que quer fechar?");
		int id = Input.lerInt();
		Evento e = handler.obterEvento(id);
		while (e==null) {
			System.out.println("ID de evento inválido.");
			id = Input.lerInt();
			e = handler.obterEvento(id);
		}
		e.setEstado(Estado.fechado);
		ArrayList<Aposta> apostas = e.getApostas();
		for (Aposta a: apostas) {
			if (e.getresultadoCasa()>e.getresultadoFora() && a.getEquipaAapostar()=='1') {
				System.out.println("Parabéns, "+a.getidApostador()+", ganhou a aposta! Amealhou "+(a.getValorAapostar()*e.getHomeOdd())+" moedas.");
				handler.adicionarCredito(a.getidApostador(), a.getValorAapostar()*e.getHomeOdd());
			} else if (e.getresultadoCasa()<e.getresultadoFora() && a.getEquipaAapostar()=='2') {
				System.out.println("Parabéns, "+a.getidApostador()+", ganhou a aposta! Amealhou "+(a.getValorAapostar()*e.getAwayOdd())+" moedas.");
				handler.adicionarCredito(a.getidApostador(), a.getValorAapostar()*e.getAwayOdd());
			} else if (e.getresultadoCasa()==e.getresultadoFora() && a.getEquipaAapostar()=='x'){
				System.out.println("Parabéns, "+a.getidApostador()+", ganhou a aposta! Amealhou "+(a.getValorAapostar()*e.getOddEmpate())+" moedas.");
				handler.adicionarCredito(a.getidApostador(), a.getValorAapostar()*e.getOddEmpate());
			} else {
				System.out.println("Perdeu a aposta, " + a.getidApostador() + ".");
			}
		}
		handler.atualizarBD();
	}

	private static void criarEvento() {
		System.out.println("Equipa da casa");
        String equipaCasa = Input.lerString();
        System.out.println("Equipa de fora");
        String equipaFora = Input.lerString();
    	System.out.println("Odd da equipa da casa");
        double oddCasa = Input.lerDouble();
        System.out.println("Odd da equipa de fora");
        double oddFora = Input.lerDouble();
    	System.out.println("Odd do empate");
        double oddEmpate = Input.lerDouble();
        handler.registarEvento(equipaCasa, equipaFora, oddCasa, oddFora, oddEmpate);
	}

	private static void login() {
        System.out.println("Insira o seu email");
        String email = Input.lerEmail();
        System.out.println("Insira a sua password");
        String password = Input.lerString();
        Jogador j = null;
        while (j==null){
            j = handler.loginUtilizador(email, password);
            if (j==null) {
            	System.out.println("Email ou password incorretos.");
	            System.out.println("Insira o seu email");
	            email = Input.lerEmail();
	            System.out.println("Insira a sua password");
	            password = Input.lerString();
            }
        }
        menu(j);
    }

    private static void registar() {
    	System.out.println("Começará com 100 moedas de crédito");
    	System.out.println("Insira o seu primeiro nome");
        String pnome = Input.lerString();
        System.out.println("Insira o seu último nome");
        String unome = Input.lerString();
    	System.out.println("Insira o email que pretende utilizar");
        String email = Input.lerEmail();
        System.out.println("Insira a password pretendida");
        String password = Input.lerString();
        Jogador j = handler.registarJogador(pnome, unome, email, password);
        menu(j);
    }
    
    private static void menu(Jogador j){
    	String escolha = "";
        while(!escolha.equals("L")){
        	System.out.println("Bem vindo, ao menu principal!\n"
                    +"A - Verificar saldo da conta\n"
                    +"B - Ver apostas feitas\n"
                    +"C - Ver eventos ativos\n"
                    +"D - Ver todos os eventos\n"
                    +"E - Fazer aposta\n"
                    +"L - Logout\n");
            escolha = Input.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": System.out.println("O seu saldo corrente é: " + j.getCredito()); break;
                case "B": verApostas(j); break;
                case "C": verEventos(); break;
                case "D": verTodosOsEventos(); break;
                case "E": fazerAposta(j); break;
                case "L": break;
                default: System.out.println("Opção inválida!");
            }
        }
    }

	private static void fazerAposta(Jogador j) {
		verEventos();
		System.out.println("Escolha o evento");
		int id = Input.lerInt();
		Evento e = handler.obterEvento(id);
		while (e==null) {
			System.out.println("ID de evento inválido.");
			id = Input.lerInt();
			e = handler.obterEvento(id);
		}
		System.out.println("Em que equipa quer apostar?");
		System.out.println("A - " + e.getEquipaCasa());
		System.out.println("B - " + e.getEquipaFora());
		System.out.println("C - Empate");
		String escolha = "";
		char equipa = 'a';
		while(!escolha.equals("A") && !escolha.equals("B") && !escolha.equals("C")){
            escolha = Input.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": equipa='1'; break;
                case "B": equipa='2'; break;
                case "C": equipa='x'; break;
                default: System.out.println("Opção inválida!");
            }
		}
		System.out.println("Qual o montante que quer apostar (Tem neste momento " + j.getCredito() + " fichas)?");
		double aposta = Input.lerDouble();
		while (aposta>j.getCredito()) {
			System.out.println("Não tem saldo suficiente para apostar esse montante.");
			aposta = Input.lerDouble();
		}
		handler.registarAposta(j.getEmail(), id, equipa, aposta);
	}

	private static ArrayList<Evento> verTodosOsEventos() {
		ArrayList<Evento> eventos = handler.obterEventos();
		for (Evento e: eventos) {
			System.out.println(e);
		}
		return eventos;
	}

	private static ArrayList<Evento> verEventos() {
		ArrayList<Evento> eventosAtivos = handler.eventosAtivos();
		for (Evento e: eventosAtivos) {
			System.out.println(e);
		}
		return eventosAtivos;
	}

	private static void verApostas(Jogador j) {
		ArrayList<Aposta> apostas = handler.apostasJogador(j.getEmail());
		if (apostas!=null){
			for (Aposta a: apostas) {
				Evento e = handler.obterEvento(a.getidEvento());
				String equipa="";
				switch(a.getEquipaAapostar()) {
					case '1': equipa=e.getEquipaCasa(); break;
					case '2': equipa=e.getEquipaFora(); break;
					default: equipa="empate";
				}
				if (!equipa.equals("empate")) {
					System.out.println("Apostou "+a.getValorAapostar()+" na equipa " + equipa + ", no evento " + e.getEquipaCasa() +
					"-" + e.getEquipaFora()+", cujo resultado final foi "+e.getresultadoCasa()+"-"+e.getresultadoFora()+".");
				} else {
					System.out.println("Apostou "+a.getValorAapostar()+" no empate, no evento " + e.getEquipaCasa() +
							"-" + e.getEquipaFora()+", cujo resultado final foi "+e.getresultadoCasa()+"-"+e.getresultadoFora()+".");
				}
				if (e.getresultadoCasa()>e.getresultadoFora() && a.getEquipaAapostar()=='1') {
					System.out.println("Ganhou a aposta! Amealhou "+(a.getValorAapostar()*e.getHomeOdd())+" moedas.");
				} else if (e.getresultadoCasa()<e.getresultadoFora() && a.getEquipaAapostar()=='2') {
					System.out.println("Ganhou a aposta! Amealhou "+(a.getValorAapostar()*e.getAwayOdd())+" moedas.");
				} else if (e.getresultadoCasa()==e.getresultadoFora() && a.getEquipaAapostar()=='x'){
					System.out.println("Ganhou a aposta! Amealhou "+(a.getValorAapostar()*e.getOddEmpate())+" moedas.");
				} else {
					System.out.println("Perdeu a aposta!");
				}
			}
		} else {
			System.out.println("Não realizou nenhuma aposta!");
		}
	}
}
