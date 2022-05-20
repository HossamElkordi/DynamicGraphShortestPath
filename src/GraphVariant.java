import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GraphVariant implements GraphInterface{

    private HashMap<Integer, Set<Integer>> adjList;
    private Map<Integer, Map<Integer, Integer>> distances;
    private Lock lock = new ReentrantLock();
    private File logfile;
    private String nodeName;
    private DateTimeFormatter dtf;
    private LocalDateTime now;
    private long start;

    public GraphVariant(String nodeName) throws FileNotFoundException, RemoteException, IOException {
        this.nodeName = nodeName;
        logfile = new File("log0");
        if(logfile.exists()){
            logfile.delete();
        }
        logfile.createNewFile();
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        this.logResults(dtf.format(LocalDateTime.now()) + " Node " + nodeName + " starting \n");

        adjList = new HashMap<>();
        Scanner scan = new Scanner(System.in);
        start = System.nanoTime();
        this.readInitialGraph(scan);
        this.distances = totalDistances();
        start = System.nanoTime() - start;
        this.logResults(dtf.format(LocalDateTime.now()) + " R Initialization time(ns): " + start + "\n");
        scan.close();
    }

    public GraphVariant(String path, String nodeName) throws FileNotFoundException, RemoteException, IOException {
        this.nodeName = nodeName;
        logfile = new File("log0");
        if(logfile.exists()){
            logfile.delete();
        }
        logfile.createNewFile();
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        now = LocalDateTime.now();
        this.logResults(dtf.format(now) + " Node " + nodeName + " starting \n");

        adjList = new HashMap<>();
        File file = new File(path);
        Scanner scan = new Scanner(file);
        start = System.nanoTime();
        this.readInitialGraph(scan);
        this.distances = totalDistances();
        start = System.nanoTime() - start;
        now = LocalDateTime.now();
        this.logResults(dtf.format(now) + " R Initialization time(ns): " + start + "\n");
        scan.close();
    }

    private void printGraph(){
        for(int n : this.adjList.keySet()){
            System.out.print(n + " -> ");
            for(int n1 : this.adjList.get(n)) System.out.print(n1 + " ");
            System.out.println();
        }
        System.out.println();
    }

    private void readInitialGraph(Scanner scan) throws RemoteException {
        String input = "";
        String[] splitted = null;
        while(true){
            input = scan.nextLine();
            if(input.equals("S")) break;
            splitted = input.split(" ");
            this.add(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]));
        }
    }


    private Map<Integer, Map<Integer, Integer>> totalDistances(){
        Map<Integer, Map<Integer, Integer>> dist = new HashMap<>();
        for(int n1 : adjList.keySet()){
            Map<Integer, Integer> destDist = new HashMap<>();
            for(int n2 : adjList.keySet()){
                if(n1 != n2) destDist.put(n2, simpleShortestDIstance(n1, n2));
            }
            dist.put(n1, destDist);
        }
        return dist;
    }
    
    private int simpleShortestDIstance(int n1, int n2){
        if(!this.adjList.containsKey(n1) || !this.adjList.containsKey(n2)){
            return -1;
        }
        if(n1 == n2) return 0;
        HashMap<Integer, Integer> pred = new HashMap<>();
        HashMap<Integer, Integer> dist = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(n1);
        pred.put(n1, -1);
        dist.put(n1, 0);
        int cur;
        while(!queue.isEmpty()){;
            cur = queue.poll();
            if(cur == n2) return dist.get(n2);
            for(int n : this.adjList.get(cur)){
                if(pred.containsKey(n)) continue;
                queue.add(n);
                pred.put(n, cur);
                dist.put(n, 1 + dist.get(cur));
            }
        }
        return -1;
    }

    private int query(int n1, int n2){
        if(!this.adjList.containsKey(n1) || !this.adjList.containsKey(n2)){
            return -1;
        }
        if(n1 == n2) return 0;
        return distances.get(n1).get(n2);
    }

    private void add(int n1, int n2) throws RemoteException {
        if(!adjList.containsKey(n1)) adjList.put(n1, new HashSet<>());
        if(!adjList.containsKey(n2)) adjList.put(n2, new HashSet<>());
        adjList.get(n1).add(n2);
        distances = totalDistances();
    }


    private void remove(int n1, int n2) throws RemoteException {
        if(!adjList.containsKey(n1)) return;
        Set<Integer> adj = adjList.get(n1);
        adj.remove(n2);
        if(adj.size() == 0) {
            adjList.remove(n1);
            for(int k : adjList.keySet()){
                adjList.get(k).remove(n1);
            }
        }
        distances = totalDistances();
    }

    public String executeQuery(String queries, String clientNode) throws RemoteException, IOException {
        lock.lock();
        StringBuilder res = new StringBuilder();
        now = LocalDateTime.now();
        this.logResults(dtf.format(now) + ": Receiving batch from node " + clientNode + "\n" + queries + "\n");
        start = System.nanoTime();
        try{
            String[] qs = queries.split("\n");
            String[] qSplit;
            for(String s: qs){
                if(s.equals("F")) break;
                qSplit = s.split(" ");
                switch (qSplit[0]) {
                    case "A" : this.add(Integer.parseInt(qSplit[1]), Integer.parseInt(qSplit[2]));break;
                    case "R" : this.remove(Integer.parseInt(qSplit[1]), Integer.parseInt(qSplit[2]));break;
                    case "Q" :
                        res.append(this.query(Integer.parseInt(qSplit[1]), Integer.parseInt(qSplit[2]))).append("\n");
                }
            }

        }finally {
            lock.unlock();
        }
        start = System.nanoTime() - start;
        String result = res.toString();
        now = LocalDateTime.now();
        this.logResults(dtf.format(now) + ": Sending response to node " + clientNode + " Execution time(ns): " + start + "\n" + result + "\n");
        return result;
    }

    private void logResults(String res) throws IOException {
        FileWriter fw = new FileWriter(this.logfile,true);
        fw.append(res);
        fw.close();
    }
}
