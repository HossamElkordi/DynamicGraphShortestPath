import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Scanner;

public class Start {
    private static HashMap<String,String> readProps(Scanner scan){
        String input;
        String[] splitted;
        HashMap<String,String>out = new HashMap<>();
        while(scan.hasNextLine()){
            input = scan.nextLine();
            splitted = input.split("=");
            out.put(splitted[0], splitted[1]);
        }
        return out;
    }
    public static void main(String[] args) throws RemoteException, FileNotFoundException, InterruptedException {
        // Read System's Properties
        Scanner scan = new Scanner(new File("system.properties"));
        HashMap<String, String> prop = readProps(scan);
        scan.close();

        // Setting Server IP and Create the Naming Registry
        System.setProperty("java.rmi.server.hostname", prop.get("GSP.server"));
        LocateRegistry.createRegistry(Integer.parseInt(prop.get("GSP.rmiregistry.port")));

        // Create the server node (thread) listening on the specified port number
        Thread serverThread = new Thread(new Runnable(){
            @Override
            public void run() {
                new Server(Integer.parseInt(prop.get("GSP.server.port")), prop.get("GSP.server"),
                        prop.get("GSP.node0"), "input.txt");
            }
        });

        // Paths to files with randomly generated queries
        String[] qFiles = {"addRemove.txt", "query.txt"};

        // Create clients' nodes (threads)
        Thread[] clientsNodes = new Thread[Integer.parseInt(prop.get("GSP.numberOfnodes")) - 1];
        for(int i = 0; i < clientsNodes.length; ++i){
            int id = i + 1;
            clientsNodes[i] = new Thread(new Runnable() {
                public void run() {
                    try {
                        new Client(prop.get("GSP.server"), qFiles, id, prop.get("GSP.node" + id));
                    } catch (RemoteException | NotBoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        serverThread.start();
        Thread.sleep(1000);

        for(Thread t : clientsNodes)
            t.start();

        for(Thread t : clientsNodes)
            t.join();

        serverThread.join();

    }
}
