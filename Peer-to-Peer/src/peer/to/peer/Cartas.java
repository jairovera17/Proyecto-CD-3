/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer.to.peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.StringTokenizer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jairo
 */
public class Cartas {
    
    int rangedown;
    int rangeup;
    ArrayList<String> initCartas;
    ArrayList<Integer> misCartas;
    ArrayList<Integer> noCartas;
    public ArrayList<Nodo> players;
    Nodo yo;
    
    /*public Cartas(Nodo yo,ArrayList<Nodo> players){
        this.rangedown=range-9;
        this.rangeup=range;
        this.initCartas = parsearArray(cartas);
        this.players=players;
        
        iniciarOrdenamiento();
    }*/
   
    
    private ArrayList<String> parsearArray(String cadena){
        ArrayList<String> salida = new ArrayList<>();
        StringTokenizer tokens =new StringTokenizer(cadena,"|");
        while(tokens.hasMoreTokens()){
            salida.add(tokens.nextToken());
        }
        
        return salida;
        
    }
    
    private String parsearString(ArrayList<Integer> lista){
        String salida="";
        for (int i = 0; i < lista.size()-1; i++) {
            salida+=lista.get(i)+"|";
        }
        salida+=lista.get(lista.size()-1);
        return salida;
        
    }
    
    private void separar(){
        misCartas= new ArrayList<>();
        noCartas=new ArrayList<>();
        for(String cadena:initCartas){
            int aux = Integer.parseInt(cadena);
            if(aux>=rangedown && aux<=rangeup){
                this.misCartas.add(aux);
            }
            else{
                this.noCartas.add(aux);
            }
            
        }
        
      //  Collections.sort(misCartas);
       // Collections.sort(noCartas);
      
       
    }
    
    private void iniciarOrdenamiento(){
        separar();
        System.out.println("Mis cartas: "+Arrays.toString(misCartas.toArray()));
        System.out.println("No mis cartas: "+Arrays.toString(noCartas.toArray()));
         
        if(misCartas.size()==10&&noCartas.isEmpty()){
              Collections.sort(misCartas);
        }
        else{
            System.out.println("Usuarios en juego"+players.size());
           ArrayList<Thread> array = new ArrayList<>();
           array.add(new Ofrezco());
           array.get(0).start();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex1) {
                Logger.getLogger(Cartas.class.getName()).log(Level.SEVERE, null, ex1);
            }
            //me excluyo (players size -1)
            for(int i=0;i<players.size();i++){
                System.out.println(players.get(i).address);
                array.add(new Solicito(players.get(i).address));
                array.get(i+1).start();
              
            }
            
            for(Thread t : array){
                try {
                    t.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Cartas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
            
            Collections.sort(misCartas);
        }
        System.out.println("*****************FIN*********************************");
        System.out.println("Mis cartas: "+Arrays.toString(misCartas.toArray()));
        System.out.println("No mis cartas: "+Arrays.toString(noCartas.toArray()));
        
        
    }
    
    
    private class Solicito extends Thread{

        String hostname;
        public Solicito(String ip){
            this.hostname=ip;
        }
        
        @Override
        public void run() {
            
            try {
               
                Socket socket = new Socket(hostname,6000);
                 PrintWriter out = null;
                 BufferedReader in=null;
                 out = new PrintWriter(socket.getOutputStream(), true);
                 in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 
                 System.out.println("Estoy dentro");
                //le doy mi rango
                out.println(rangeup);
                String response = in.readLine();
                System.out.println(response);
                StringTokenizer tokens = new StringTokenizer(response,"|");
                while(tokens.hasMoreTokens()){
                    int aux =Integer.parseInt(tokens.nextToken());
                    misCartas.add(aux);
                     
                }
                
               
            } catch (IOException ex) {
                Logger.getLogger(Cartas.class.getName()).log(Level.SEVERE, null, ex);
            }
            
           
        }
        
    }
    private class Ofrezco extends Thread{

        
        
        @Override
        public void run() {
            try {
                
                ServerSocket serversocket = new ServerSocket(6000);
            ofrescoThread ar [] = new ofrescoThread[players.size()];
              for(int i=0;i<players.size();i++){
                  Socket socket = serversocket.accept();
                  ar[i]= new ofrescoThread(socket);
                  ar[i].start();
              }
              
             
              for(ofrescoThread temp : ar){
                  temp.join();
              }
                    
                
                
            } catch (IOException ex) {
                Logger.getLogger(Cartas.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Cartas.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        class ofrescoThread extends Thread{
            private Socket socket;
            public ofrescoThread(Socket socket){
                this.socket = socket;
            }

            @Override
            public void run() {
                try {
                    PrintWriter out = null;
                    BufferedReader in=null;
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    
                    int maxRango = Integer.parseInt(in.readLine());
                    int minRango = maxRango-9;
                    ArrayList<Integer> response = new ArrayList<>();
                    for(Integer aux : noCartas){
                        
                        if(aux>=minRango||aux<=maxRango){
                            response.add(aux);
                            noCartas.remove(aux);
                        }
                        
                        
                    }
                    String respuesta ="Respuesta"+parsearStringOfrezco(response);
                    out.println(respuesta);
                  
                            
                            
                            
                } catch (IOException ex) {
                    Logger.getLogger(Cartas.class.getName()).log(Level.SEVERE, null, ex);
                }
                
               }
            
              private String parsearStringOfrezco(ArrayList<Integer> lista){
        String salida="";
        for (int i = 0; i < lista.size()-1; i++) {
            salida+=lista.get(i)+"|";
        }
        salida+=lista.get(lista.size()-1);
        return salida;
        
    }
        }
        
    }
    
    
    
    
}
