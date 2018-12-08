package main;

import facade.Facade;

public class BetEss {

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
                	String res = facade.login();
                	if (res.equals("J")) 
                		menu(facade); 
                	else if (res.equals("B"))
                		menuBookie(facade);
                	else 
                		System.out.println("Credenciais inválidas"); break;
                case "B": facade.signIn(); break;
                case "C": loginAdmin(facade); break;
                case "S": break;
                default: System.out.println("Opção inválida!");
            }
        }
	}

	private static void menuBookie(Facade f) {
		String escolha = "";
        while(!escolha.equals("L")){
        	System.out.println("Bem vindo à interface de bookie!\n"
                    +"A - Criar evento\n"
        			+"B - Receber notificaçõew sobre fecho de um evento\n"
                    +"C - Mudar password\n"
                    +"L - Logout\n");
            escolha = f.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": f.criarEvento(); break;
                case "B": f.notificarEvento(); break;
                case "C": f.mudarPassword(); break;
                case "L": break;
                default: System.out.println("Opção inválida!");
            }
        }
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
        	System.out.println("Bem vindo à interface de administrador do sistema!\n"
                    +"A - Fechar evento\n"
                    +"B - Atualizar resultado de evento\n"
                    +"C - Adicionar crédito a jogador\n"
                    +"D - Registar bookie\n"
                    +"L - Logout\n");
            escolha = f.lerString();
            escolha = escolha.toUpperCase();
            switch(escolha){
                case "A": f.fecharEvento(); break;
                case "B": f.atualizarEvento(); break;
                case "C": f.adicionarCredito(); break;
                case "D": f.registarBookie(); break;
                case "L": break;
                default: System.out.println("Opção inválida!");
            }
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
                case "A": f.verificarSaldoConta(); break;
                case "B": f.verApostasFeitas(); break;
                case "C": f.verEventosAtivos(); break;
                case "D": f.verTodosOsEventos(); break;
                case "E": f.fazerAposta(); break;
                case "F": f.mudarPassword(); break;
                case "L": break;
                default: System.out.println("Opção inválida!");
            }
        }
    }
	
}
