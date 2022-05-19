import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Client {

    private File logFile;
    private List<String> addRemove, query;
    private Random rand;
    public Client(String ip, String qFile, String logfile) throws RemoteException, NotBoundException {
        try{
            Registry registry = LocateRegistry.getRegistry(ip);
            GraphInterface stub = (GraphInterface) registry.lookup("Update");
            logFile = new File(logfile);
            addRemove = this.readCommands("AddRemove");
            query = this.readCommands("Query");
            rand = new Random(1);
            String queries = this.readQueries(0.6f);
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

    private String readQueries(float percent) {
        StringBuilder queries = new StringBuilder();
        int size = rand.nextInt(5) + 4, arSize = (int)(percent * size);
        String[] idx = new String[size];
        int i = 0;
        while(arSize >= 0){
            i = rand.nextInt(size);
            if(idx[i].isEmpty()){
                idx[i] = this.addRemove.get(rand.nextInt(this.addRemove.size()));
                --arSize;
            }
        }
        for(int j = 0; j < size; ++j)
            if(idx[j].isEmpty()) idx[j] = this.query.get(rand.nextInt(this.query.size()));

        for(String s : idx){
            queries.append(s);
            queries.append("\n");
        }
        queries.append("F");
        return queries.toString();
    }

    private List<String> readCommands(String commandType) throws FileNotFoundException {
        List<String> list = new ArrayList<String>();
        Scanner scan = new Scanner(new File(commandType));
        while(scan.hasNextLine()) list.add(scan.nextLine());
        scan.close();
        return list;
    }

    private void logResults(String res) throws IOException {
        FileWriter fw = new FileWriter(this.logFile);
        fw.append(res);
        fw.close();
    }
}
