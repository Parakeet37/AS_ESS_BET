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
public class Cliente {
    
    String email;
    String firstName;
    String lastnName;
    double availableCoins;
    
    public Cliente(String e, String fn, String ln, double c){
        this.email = e;
        this.firstName = fn;
        this.lastnName = ln;
        this.availableCoins = c;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastnName;
    }

    public void setLastName(String lastnName) {
        this.lastnName = lastnName;
    }

    public double getAvailableCoins() {
        return availableCoins;
    }

    public void setAvailableCoins(double availableCoins) {
        this.availableCoins = availableCoins;
    }
    
    
}
