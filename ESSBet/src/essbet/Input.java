/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package essbet;
import static java.lang.System.out;
import static java.lang.System.in;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Input {

 /**
  * Métodos de Classe
  */
  /**
  * @return string se bem formulada
  */
 public static String lerString() {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     String txt = "";
     while(!ok) {
         try {
             txt = input.nextLine();
             ok = true;
         }
         catch(InputMismatchException e) 
             { out.println("Texto Invalido"); 
               out.print("Novo valor: ");
             }
     }
     //input.close();
     return txt;
  }
 public static String lerEmail() {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     String txt = "";
     while(!ok) {
         try {
             txt = input.nextLine();
             if (txt.contains("@")) ok = true;
         }
         catch(InputMismatchException e) 
             { out.println("Texto Invalido"); 
               out.print("Novo valor: ");
             }
     }
     //input.close();
     return txt;
  }

 public static int lerInt() {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     int i = 0; 
     while(!ok) {
         try {
             i = Integer.parseInt(input.nextLine());
             ok = true;
         }
         catch(InputMismatchException e) 
             { out.println("Inteiro Invalido"); 
               out.print("Novo valor: ");
             
             }
     }
     //input.close();
     return i;
  } 
  
  public static double lerDouble() {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     double d = 0.0; 
     while(!ok) {
         try {
             d = Double.parseDouble(input.nextLine());
             ok = true;
         }
         catch(InputMismatchException | NumberFormatException e) 
             { out.println("Valor real Invalido"); 
               out.print("Novo valor: ");
             }
     }
     //input.close();
     return d;
  }  
}
