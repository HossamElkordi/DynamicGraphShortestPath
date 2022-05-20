import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Scanner;

public class Start {
    private static HashMap<String,String> read(Scanner scan){
        String input = "";
        String[] splitted = null;
        HashMap<String,String>out=new HashMap<>();
        while(scan.hasNextLine()){
            input = scan.nextLine();
            splitted = input.split("=");
            out.put(splitted[0], splitted[1]);
        }
        return out;
    }
    public static void main(String[] args) throws RemoteException, FileNotFoundException, InterruptedException {

        Scanner scan = new Scanner(new File("D:\\aa\\DynamicGraphShortestPath\\src\\system.properties"));
        HashMap<String,String>prop=read(scan);
        scan.close();
        System.setProperty("java.rmi.server.hostname", prop.get("GSP.server"));
        LocateRegistry.createRegistry(Integer.parseInt(prop.get("GSP.rmiregistry.port")));
        Thread serverThread = new Thread(new Runnable(){
            @Override
            public void run() {
                Server srv = new Server(Integer.parseInt(prop.get("GSP.server.port")), prop.get("GSP.server"));
            }
        });

        Thread clientThread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Client clt = new Client(prop.get("GSP.server"), "client1", "D:\\aa\\DynamicGraphShortestPath\\src\\log1");
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread client2Thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Client clt = new Client(prop.get("GSP.server"), "client2", "D:\\aa\\DynamicGraphShortestPath\\src\\log2");
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        serverThread.start();
        Thread.sleep(1000);
        clientThread.start();
        client2Thread.start();

        clientThread.join();
        client2Thread.join();
        serverThread.join();

    }
}
