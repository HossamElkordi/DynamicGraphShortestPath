import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Client {

    private File logFile;
    private List<String> addRemove, query;
    private Random rand;
    private String nodeName;
    public Client(String ip, String[] qFiles, int nodeId, String nodeName) throws RemoteException, NotBoundException {
        try{
            Registry registry = LocateRegistry.getRegistry(ip);
            GraphInterface stub = (GraphInterface) registry.lookup("Update");
            this.nodeName = nodeName;
            long start;
            this.logFile = new File("log" + nodeId);
            this.rand = new Random(nodeId);
            this.addRemove = this.readCommands(qFiles[0]);
            this.query = this.readCommands(qFiles[1]);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now;
            while(true) {
                String queries = this.readQueries(0.5f);
                System.out.println(queries);
                now = LocalDateTime.now();
                start = System.nanoTime();
                String res = stub.executeQuery(queries);
                start = System.nanoTime() - start;
                this.logResults(dtf.format(now) + ": sending batch\n" + queries + "\n");
                System.out.println("result");
                System.out.println(res);
                System.out.println("took ns: " + start);
                now = LocalDateTime.now();
                this.logResults(dtf.format(now) + ": Received response " + "Response time(ns):" + start + "\n" + res + "\n");
                // Sleep, choose a random amount first
                Thread.sleep(rand.nextInt(1000));
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private String readQueries(float percent) {
        StringBuilder queries = new StringBuilder();
        int size = rand.nextInt(5) + 8, arSize = (int)(percent * size);
        String[] idx = new String[size];
        int i = 0;
        while(arSize >= 0){
            i = rand.nextInt(size);
            if(idx[i]==null||idx[i].isEmpty()){
                idx[i] = this.addRemove.get(rand.nextInt(this.addRemove.size()));
                --arSize;
            }
        }
        for(int j = 0; j < size; ++j)
            if(idx[j]==null||idx[j].isEmpty()) idx[j] = this.query.get(rand.nextInt(this.query.size()));

        for(String s : idx){
            queries.append(s);
            queries.append("\n");
        }
        queries.append("F");
        return queries.toString();
    }

    private List<String> readCommands(String commandType) throws FileNotFoundException {
        List<String> list = new ArrayList<>();
        Scanner scan = new Scanner(new File(commandType));
        while(scan.hasNextLine()) list.add(scan.nextLine());
        scan.close();
        return list;
    }

    private void logResults(String res) throws IOException {
        FileWriter fw = new FileWriter(this.logFile,true);
        fw.append(res);
        fw.close();
    }
}
