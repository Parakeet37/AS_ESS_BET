/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essbet;

import java.util.ArrayList;

public class Evento {
    
    private int id;
    private double homeOdd;
    private double awayOdd;
    private Estado state;
    private String equipaCasa;
    private String equipaFora;
    private double oddEmpate;
    private int resultadoCasa;
    private int resultadoFora;
    private ArrayList<Aposta> apostas;
    
    public Evento(){}
    
    public int getID() {
    	return id;
    }
    
    public double getHomeOdd() {
        return homeOdd;
    }

    public void setHomeOdd(double homeOdd) {
        this.homeOdd = homeOdd;
    }
    
    public double getAwayOdd() {
        return awayOdd;
    }

    public void setAwayOdd(double awayOdd) {
        this.awayOdd = awayOdd;
    }

    public Estado getEstado() {
        return state;
    }

    public void setEstado(Estado e) {
        this.state = e;
    }

    public String getEquipaCasa() {
        return equipaCasa;
    }

    public String getEquipaFora() {
        return equipaFora;
    }
    
    public double getOddEmpate() {
        return oddEmpate;
    }

    public void setOddEmpate(double oddEmpate) {
        this.oddEmpate = oddEmpate;
    }
    
    public int getresultadoCasa() {
    	return resultadoCasa;
    }
    
    public int getresultadoFora() {
    	return resultadoFora;
    }

    public void invalidadoCasa() {
        if (resultadoCasa>0)resultadoCasa--;
    }

    public void goloCasa() {
        resultadoCasa++;
    }

    public void invalidadoFora() {
        if (resultadoFora>0)resultadoFora--;
    }

    public void goloFora() {
        resultadoFora++;
    }
    
    public void setResultadoCasa(int i) {
    	resultadoCasa = i;
    }
    
    public void setResultadoFora(int i) {
    	resultadoFora = i;
    }
    public Evento(double homeOdd, double awayOdd, double oddEmpate, String equipaCasa, String equipaFora) {
        this.homeOdd = homeOdd;
        this.awayOdd = awayOdd;
        this.state = Estado.aberto;
        this.equipaCasa = equipaCasa;
        this.equipaFora = equipaFora;
        this.oddEmpate = oddEmpate;
        this.resultadoCasa = 0;
        this.resultadoFora = 0;
        this.apostas = new ArrayList<Aposta>();
        this.id = 0;
    }

	@Override
	public String toString() {
		return "Evento [id=" + id + ", homeOdd=" + homeOdd + ", awayOdd=" + awayOdd + ", state=" + state
				+ ", equipaCasa=" + equipaCasa + ", equipaFora=" + equipaFora + ", oddEmpate=" + oddEmpate
				+ ", resultadoCasa=" + resultadoCasa + ", resultadoFora=" + resultadoFora + "]";
	}

	public ArrayList<Aposta> getApostas() {
		return apostas;
	}

	public void adicionarAposta(Aposta a) {
		apostas.add(a);
	}
    
    public void setID(int i) {
    	this.id = i;
    }
 
}
