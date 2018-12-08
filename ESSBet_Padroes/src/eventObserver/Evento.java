/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventObserver;

import java.util.ArrayList;

import aposta.Aposta;
import state.AbertoState;
import state.EventoState;

public class Evento {
    
    
    private double oddEmpate;
    private int resultadoCasa;
    private int resultadoFora;
	private double oddCasa;
	private double oddFora;
	private String equipaCasa;
	private String equipaFora;
	private int id;
	private String idBookie;
	private EventoState estado;
	private ArrayList<Observer> observers;
	private ArrayList<Aposta> apostas;
    
	public Evento(int id, String idBookie, double homeOdd, double awayOdd, double oddEmpate, String equipaCasa, String equipaFora, EventoState state, int resultadoCasa, int resultadoFora, ArrayList<Observer> observers) {
    	this.oddCasa = homeOdd;
    	this.oddFora = awayOdd;
    	this.equipaCasa = equipaCasa;
    	this.equipaFora = equipaFora;
    	this.id = id;
    	this.estado = state;
        this.oddEmpate = oddEmpate;
        this.resultadoCasa = resultadoCasa;
        this.resultadoFora = resultadoFora;
        this.observers = observers;
        this.apostas = new ArrayList<>();
        this.idBookie = idBookie;
    }
	
	public Evento(String idBookie, double homeOdd, double awayOdd, double oddEmpate, String equipaCasa, String equipaFora) {
    	this.oddCasa = homeOdd;
    	this.oddFora = awayOdd;
    	this.equipaCasa = equipaCasa;
    	this.equipaFora = equipaFora;
    	this.estado = new AbertoState();
        this.oddEmpate = oddEmpate;
        this.resultadoCasa = 0;
        this.resultadoFora = 0;
        this.observers = new ArrayList<>();
        this.apostas = new ArrayList<>();
        this.idBookie = idBookie;
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

	public void notifyObservers() {
		int count = 0;
		int ganhos = 0;
		for (Observer o: observers) {
			for (Aposta a: apostas) {
				if (a.getidApostador().equals(o.getEmail())) {
					Jogador j = (Jogador) o;
					if (resultadoCasa>resultadoFora && a.getEquipaAapostar()=='1') {
						System.out.println("Parabéns, "+a.getidApostador()+", ganhou a aposta! Amealhou "+(a.getValorAapostar()*oddCasa)+" moedas.");
						j.adicionarCredito(a.getValorAapostar()*oddCasa);
						ganhos-=a.getValorAapostar()*oddCasa;
					} else if (resultadoCasa<resultadoFora && a.getEquipaAapostar()=='2') {
						System.out.println("Parabéns, "+a.getidApostador()+", ganhou a aposta! Amealhou "+(a.getValorAapostar()*oddFora)+" moedas.");
						j.adicionarCredito(a.getValorAapostar()*oddFora);
						ganhos-=a.getValorAapostar()*oddFora;
					} else if (resultadoCasa==resultadoFora && a.getEquipaAapostar()=='x'){
						System.out.println("Parabéns, "+a.getidApostador()+", ganhou a aposta! Amealhou "+(a.getValorAapostar()*oddEmpate)+" moedas.");
						j.adicionarCredito(a.getValorAapostar()*oddEmpate);
						ganhos-=a.getValorAapostar()*oddEmpate;
					} else {
						System.out.println("Perdeu a aposta, " + a.getidApostador() + ".");
						ganhos+=a.getValorAapostar();
					}
					count++;
				} 
			}
			if (idBookie.equals(o.getEmail())) {
				System.out.println(o.getEmail()+", o evento " + this + " terminou!");
				count++;
			}
			if (count == observers.size()) System.out.println("Este evento teve um saldo total de " + ganhos + " moedas, " + idBookie + "");
		}
	}

	public double getOddCasa() {
		return oddCasa;
	}

	public void setOddCasa(double oddCasa) {
		this.oddCasa = oddCasa;
	}

	public double getOddFora() {
		return oddFora;
	}

	public void setOddFora(double oddFora) {
		this.oddFora = oddFora;
	}

	public String getEquipaCasa() {
		return equipaCasa;
	}

	public void setEquipaCasa(String equipaCasa) {
		this.equipaCasa = equipaCasa;
	}

	public String getEquipaFora() {
		return equipaFora;
	}

	public void setEquipaFora(String equipaFora) {
		this.equipaFora = equipaFora;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EventoState getEstado() {
		return estado;
	}

	public void setEstado(EventoState estado) {
		this.estado = estado;
	}

	public ArrayList<String> getObservers() {
		ArrayList<String> ids = new ArrayList<>();
		for (Observer o: observers) {
			ids.add(o.getEmail());
		}
		return ids;
	}

	public void setObservers(ArrayList<Observer> observers) {
		this.observers = observers;
	}

	public void addObserver(Observer j) {
		observers.add(j);
	}
	
	public void addAposta(Aposta a) {
		apostas.add(a);
	}

	@Override
	public String toString() {
		return "Evento " + id + ", " + equipaCasa + " - " + equipaFora + " ("+resultadoCasa + "-" + resultadoFora + ") - " 
				+ estado;
	}

	public String getIdBookie() {
		return idBookie;
	}

	public void setIdBookie(String idBookie) {
		this.idBookie = idBookie;
	}
}