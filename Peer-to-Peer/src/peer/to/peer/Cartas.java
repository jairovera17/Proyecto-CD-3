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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    
    public Cartas(int range, String cartas,ArrayList<Nodo> players){
        this.rangedown=range-9;
        this.rangeup=range;
        this.initCartas = parsearArray(cartas);
        this.players=players;
        
        iniciarOrdenamiento();
    }
   
    
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
        
        Collections.sort(misCartas);
        Collections.sort(noCartas);
      
       
    }
    
    private void iniciarOrdenamiento(){
        separar();
        System.out.println("Mis cartas: "+Arrays.toString(misCartas.toArray()));
        System.out.println("No mis cartas: "+Arrays.toString(noCartas.toArray()));
         
        if(misCartas.size()==10&&noCartas.isEmpty()){
              Collections.sort(misCartas);
        }
        else{
            ExecutorService ex = Executors.newCachedThreadPool();
            ex.submit(new Ofrezco());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex1) {
                Logger.getLogger(Cartas.class.getName()).log(Level.SEVERE, null, ex1);
            }
            //me excluyo (players size -1)
            for(int i=0;i<players.size();i++){
                ex.submit(new Solicito(players.get(i).address));
            }
            
            ex.shutdown();
            while(!ex.isShutdown()){
                
            }
            Collections.sort(misCartas);
        }
        System.out.println("**************************************************");
        System.out.println("Mis cartas: "+Arrays.toString(misCartas.toArray()));
        System.out.println("No mis cartas: "+Arrays.toString(noCartas.toArray()));
        
        
    }
    
    
    private class Solicito implements Runnable{

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
                 
                 
                //le doy mi rango
                out.println(rangeup);
                String response = in.readLine();
                StringTokenizer tokens = new StringTokenizer(response,"|");
                while(tokens.hasMoreTokens()){
                    int aux =Integer.parseInt(tokens.nextToken());
                    misCartas.add(aux);
                     
                }
                
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Cartas.class.getName()).log(Level.SEVERE, null, ex);
            }
            
           
        }
        
    }
    private class Ofrezco implements Runnable{

        
        
        @Override
        public void run() {
            try {
                ExecutorService ofrezcoEX = Executors.newCachedThreadPool();
                ServerSocket serversocket = new ServerSocket(6000);
            
              for(int i=0;i<players.size();i++){
                  Socket socket = serversocket.accept();
                    ofrezcoEX.submit(new ofrescoThread(socket));
              }
                    
                
                
            } catch (IOException ex) {
                Logger.getLogger(Cartas.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        class ofrescoThread implements Runnable{
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
                    
                    out.println(parsearString(response));
                  socket.close();
                            
                            
                            
                } catch (IOException ex) {
                    Logger.getLogger(Cartas.class.getName()).log(Level.SEVERE, null, ex);
                }
                
               }
        }
        
    }
    
    
    
    
}
