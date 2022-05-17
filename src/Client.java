import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;

public class Client {

    private File logFile;
    public Client(String ip, String qFile, String logfile) throws RemoteException, NotBoundException {
        try{
            Registry registry = LocateRegistry.getRegistry(ip);
            GraphInterface stub = (GraphInterface) registry.lookup("Update");
            logFile = new File(logfile);
            String queries = this.readQueries(qFile);
            String res = stub.executeQuery(queries);
            this.logResults(res);
            // Sleep, choose a random amount first
            //Random rand = new Random();
            //Thread.sleep(rand.nextInt(9000) + 1000);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private String readQueries(String path) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(path));
        StringBuilder queries = new StringBuilder();
        String line = scan.nextLine();
        while(true){
            queries.append(line);
            line = scan.nextLine();
            if(line.equals("F")) break;
            queries.append("\n");
        }
        scan.close();
        return queries.toString();
    }

    private void logResults(String res) throws IOException {
        FileWriter fw = new FileWriter(this.logFile);
        fw.append(res);
        fw.close();
    }
}
