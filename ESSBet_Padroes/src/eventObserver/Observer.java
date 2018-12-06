package eventObserver;

public class Observer {
	
	private String email;
    private String pNome;
    private String uNome;
    private double credito;
    
    public Observer(String email, String pNome, String uNome, double credito) {
    	this.email = email;
        this.pNome = pNome;
        this.uNome = uNome;
        this.credito = credito;
    }
    
    public Observer(String email, String pNome, String uNome){
        this.email = email;
        this.pNome = pNome;
        this.uNome = uNome;
        this.credito = 100;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPNome() {
        return pNome;
    }

    public void setPNome(String pNome) {
        this.pNome = pNome;
    }

    public String getUNome() {
        return uNome;
    }

    public void setUNome(String uNome) {
        this.uNome = uNome;
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
