package eventObserver;

public class Jogador extends Observer{

	private double credito;
	
	public Jogador(String email, String pNome, String uNome, double credito) {
		super(email, pNome, uNome);
		this.credito = credito;
	}
	
	public Jogador(String email, String pNome, String uNome) {
		super(email, pNome, uNome);
		this.credito = 100;
	}

	public double getCredito() {
        return credito;
    }

    public void adicionarCredito(double moedas){
        credito+=moedas;
    }
    
    public void retirarCredito(double moedas){
        if (moedas <= credito){
            credito-=moedas;
        }
    }
	
}
