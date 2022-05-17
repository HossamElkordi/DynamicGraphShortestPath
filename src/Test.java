import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Test {
    public static void main(String[] args) throws FileNotFoundException{
        GClass g = new GClass("input");
        g.printGraph();
        g.add(3, 4);
        g.printGraph();
        System.out.println(g.query(1, 4));
        System.out.println();
        g.remove(2, 4);
        g.printGraph();
        System.out.println(g.query(1, 4));
        g.remove(3, 4);
        g.printGraph();
        System.out.println(g.query(2, 4));
    }
}

class GClass{
    private HashMap<Integer, Set<Integer>> adjList;

    public GClass(String path) throws FileNotFoundException{
        adjList = new HashMap<Integer, Set<Integer>>();
        File file = new File(path);
        Scanner scan = new Scanner(file);
        this.readInitialGraph(scan);
        scan.close();
    }

    private void readInitialGraph(Scanner scan){
        String input = "";
        String[] splitted = null;
        while(true){
            input = scan.nextLine();
            if(input.equals("S")) break;
            splitted = input.split(" ");
            this.add(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]));
        }
    }

    public void printGraph(){
        for(int n : this.adjList.keySet()){
            System.out.print(n + " -> ");
            for(int n1 : this.adjList.get(n)) System.out.print(n1 + " ");
            System.out.println();
        }
        System.out.println();
    }


    public int query(int n1, int n2){
        HashMap<Integer, Integer> pred = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> dist = new HashMap<Integer, Integer>();
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(n1);
        pred.put(n1, -1);
        dist.put(n1, 0);
        int cur;

        while(!queue.isEmpty()){
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

    public void add(int n1, int n2){
        if(!adjList.containsKey(n1)) adjList.put(n1, new HashSet<Integer>());
        if(!adjList.containsKey(n2)) adjList.put(n2, new HashSet<Integer>());
        adjList.get(n1).add(n2);
    }


    public void remove(int n1, int n2){
        if(!adjList.containsKey(n1)) return;
        Set<Integer> adj = adjList.get(n1);
        adj.remove(n2);
        if(adj.size() == 0) adjList.remove(n1);
    }
}