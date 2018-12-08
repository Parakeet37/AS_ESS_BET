package eventObserver;

public abstract class Observer {
	
	private String email;
    private String pNome;
    private String uNome;
    
    public Observer(String email, String pNome, String uNome){
        this.email = email;
        this.pNome = pNome;
        this.uNome = uNome;
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
}
