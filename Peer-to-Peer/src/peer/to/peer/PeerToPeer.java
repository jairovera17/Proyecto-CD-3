/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer.to.peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jairo
 */
public class PeerToPeer {

    static String name;
    static InetAddress ipBroadcast;
    static String interfaz;
    static int portEscucha;
    static int portEnvio;
    static ArrayList<Nodo> lista;
    static TreeMap tree;
    static String filepath="src/archivos/palabras.txt";
    public static void main(String[] args) throws UnknownHostException {
        
        if(args.length!=4){
            System.out.println("/src/archivos/palabras.txt");
            System.exit(0);
        }
        lista = new ArrayList<>();
        interfaz=args[0];
       name=args[1];
       System.out.println(name);
      portEscucha=Integer.parseInt(args[2]);
       portEnvio=Integer.parseInt(args[3]);
//           interfaz="wlp8s0";
//           name="Jose";
//           portEscucha=6000;
//           portEnvio=5000;

       
 TreeMap tree = getTreeMapFile(filepath);
        empezarP2P();
      
       
       // imprimirTree(tree);
    }
    
    static void empezarP2P(){
        ipBroadcast= setBroadcastip();
        System.out.println("ipBroadCast\t"+ipBroadcast.getHostAddress());
        if(ipBroadcast == null){
            System.out.println("No se pudo obtener la ipBroadCast de la red");
            System.exit(0);
        }
        
        ExecutorService ex = Executors.newCachedThreadPool();
     
        ex.submit(new Salida(ipBroadcast, portEnvio, name,lista));
        ex.submit(new Ingreso(ipBroadcast, portEscucha,name,lista));   
        
        
       
    }
    
    
    
    private static ArrayList<String> leerArchivo(String dir){
        try {
            Scanner s = new Scanner(new File(dir));
            ArrayList<String> list = new ArrayList<>();
            while (s.hasNext()){
                StringTokenizer tokens = new StringTokenizer(s.next()," ");
                while(tokens.hasMoreTokens())
                list.add(tokens.nextToken());
            }
            
            s.close();
            return list;
            
            
           
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
         return null;
    }
    
    private static TreeMap<Integer,String> getTreeMapFile(String dir){
        TreeMap<Integer,String> nuevoTree = new TreeMap<>();
        ArrayList<String> list = leerArchivo(dir);
        for(int i =0;i<list.size();i++){
            nuevoTree.put(i+1, list.get(i));
        }
        
       
      return nuevoTree;
    }
    
    private static void imprimirTree(TreeMap <Integer,String> nuevoTree){
          Set set = nuevoTree.entrySet();
      Iterator iterator = set.iterator();
      while(iterator.hasNext()) {
         Map.Entry mentry = (Map.Entry)iterator.next();
         System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
         System.out.println(mentry.getValue());
      }
    }

    private static InetAddress setBroadcastip(){
        try {
            
            NetworkInterface network = NetworkInterface.getByName(interfaz);
            for(InterfaceAddress temp : network.getInterfaceAddresses()){
                InetAddress add = temp.getBroadcast();
                if(add ==null)
                    continue;
                else{
                   
                    return add;
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
