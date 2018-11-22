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
public class Evento {
    
    String homeTeam;
    String awayTeam;
    double homeOdd;
    double awayOdd;
    Estado state;

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
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

    public Estado getState() {
        return state;
    }

    public void setState(Estado state) {
        this.state = state;
    }

    public Evento(String homeTeam, String awayTeam, double homeOdd, double awayOdd, Estado state) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeOdd = homeOdd;
        this.awayOdd = awayOdd;
        this.state = state;
    }
 
}
