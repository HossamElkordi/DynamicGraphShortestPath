import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Graph implements GraphInterface{

    private HashMap<Integer, Set<Integer>> adjList;
    private Lock lock = new ReentrantLock();


    public Graph() throws RemoteException {
        adjList = new HashMap<Integer, Set<Integer>>();
        Scanner scan = new Scanner(System.in);
        this.readInitialGraph(scan);
        scan.close();
    }


    public Graph(String path) throws FileNotFoundException, RemoteException {
        adjList = new HashMap<Integer, Set<Integer>>();
        File file = new File(path);
        Scanner scan = new Scanner(file);
        this.readInitialGraph(scan);
        scan.close();
        printGraph();
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


    private int query(int n1, int n2){

        HashMap<Integer, Integer> pred = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> dist = new HashMap<Integer, Integer>();
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(n1);
        pred.put(n1, -1);
        dist.put(n1, 0);
        int cur;
        if(!this.adjList.containsKey(n1)||!this.adjList.containsKey(n2)){
            return -1;
        }
        while(!queue.isEmpty()){
            //do{
                //if(queue.isEmpty())
               //     return -1;
                cur = queue.poll();
            //}while(!this.adjList.containsKey(cur));
            if(cur == n2) return dist.get(n2);
            for(int n : this.adjList.get(cur)){
                if(pred.containsKey(n)) continue;
                if(!this.adjList.containsKey(n))
                    System.out.println("-->"+n);
                queue.add(n);
                pred.put(n, cur);
                dist.put(n, 1 + dist.get(cur));
            }
        }
        return -1;
    }

    private void add(int n1, int n2) throws RemoteException {
        if(!adjList.containsKey(n1)) adjList.put(n1, new HashSet<Integer>());
        if(!adjList.containsKey(n2)) adjList.put(n2, new HashSet<Integer>());
        adjList.get(n1).add(n2);
    }


    private void remove(int n1, int n2) throws RemoteException {
        if(!adjList.containsKey(n1)) return;
        Set<Integer> adj = adjList.get(n1);
        adj.remove(n2);
        if(adj.size() == 0) {
            adjList.remove(n1);
            for(int k:adjList.keySet()){
                adjList.get(k).remove(n1);
            }
        }
    }


    public String executeQuery(String queries) throws RemoteException{
        lock.lock();
        StringBuilder res = new StringBuilder();
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
        return res.toString();
    }
}
