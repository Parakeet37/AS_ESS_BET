package aposta;

public class Aposta {
	private int id;
    private String idApostador;
    private int idEvento;
    private char equipaAapostar;
    private double valorAapostar;

    public Aposta(int id, String idApostador, int idEvento, char equipaAapostar, double valorAapostar) {
    	this.idApostador = idApostador;
        this.idEvento = idEvento;
        this.equipaAapostar = equipaAapostar;
        this.valorAapostar = valorAapostar;
        this.id = id;
    }
    
    
    public Aposta(String idApostador, int idEvento, char equipaAapostar, double valorAapostar) {
        this.idApostador = idApostador;
        this.idEvento = idEvento;
        this.equipaAapostar = equipaAapostar;
        this.valorAapostar = valorAapostar;
        this.id = 0;
    }

    public String getidApostador() {
        return idApostador;
    }

    public void setidEvento(int id) {
        this.idEvento = id;
    }
    
    public void setidApostador(String idApostador) {
        this.idApostador = idApostador;
    }

    public int getidEvento() {
        return idEvento;
    }

    public char getEquipaAapostar() {
        return equipaAapostar;
    }

    public double getValorAapostar() {
        return valorAapostar;
    }


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
}
