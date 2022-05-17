import java.io.File;
import java.io.FileNotFoundException;
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
            out.put(splitted[0],splitted[1]);
        }
        return out;
    }
    public static void main(String[] args) throws RemoteException, FileNotFoundException {

        Scanner scan=new Scanner(new File("C:\\Users\\ahm_e\\IntelliJIDEAProjects\\DynamicGraphShortestPath\\system.properties"));
        HashMap<String,String>prop=read(scan);
        scan.close();
        System.setProperty("java.rmi.server.hostname", prop.get("GSP.server"));
        LocateRegistry.createRegistry(Integer.parseInt(prop.get("GSP.rmiregistry.port")));
        Server srv=new Server(Integer.parseInt(prop.get("GSP.server.port")),prop.get("GSP.server"));
    }
}
