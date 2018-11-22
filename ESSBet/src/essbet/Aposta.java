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
    
       Cliente better;
    Evento event;
    char teamToBet;         //x -> empate; 1 -> casa; 2 -> fora
    double valueToBet;

    public Aposta(Cliente better, Evento event, char teamToBet, double valueToBet) {
        this.better = better;
        this.event = event;
        this.teamToBet = teamToBet;
        this.valueToBet = valueToBet;
    }

    public Cliente getBetter() {
        return better;
    }

    public void setBetter(Cliente better) {
        this.better = better;
    }

    public Evento getEvent() {
        return event;
    }

    public void setEvent(Evento event) {
        this.event = event;
    }

    public char getTeamToBet() {
        return teamToBet;
    }

    public void setTeamToBet(char teamToBet) {
        this.teamToBet = teamToBet;
    }

    public double getValueToBet() {
        return valueToBet;
    }

    public void setValueToBet(double valueToBet) {
        this.valueToBet = valueToBet;
    }
    
}
