/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essbet;

/**
 *
 * @author Sephthia Winter
 */
public class Aposta {
    
    private int id;
    private String idApostador;
    private int idEvento;
    private char equipaAapostar;
    private double valorAapostar;

    public Aposta() {}
    
    
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
}
