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
public class Jogador {
    
    private String email;
    private String pNome;
    private String uNome;
    private String password;
    private double credito;
    
    public Jogador() {}
    
    public Jogador(String email, String pNome, String uNome, String password){
        this.email = email;
        this.pNome = pNome;
        this.uNome = uNome;
        this.credito = 100;
        this.password = password;
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
        if (moedas > credito){
            credito-=moedas;
        }
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public String getPassword(){
        return password;
    }
}
