import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class Client {
    private Client() {}
    public static void main(String[] args) {
        try {
            // Getting the registry
            // Here ip should also be passed as argument or parameter from class Start
            Registry registry = LocateRegistry.getRegistry("192.168.1.7");

            // Looking up the registry for the remote object
            GraphInterface stub = (GraphInterface) registry.lookup("Update");

            // Read commands from file as a single string
            // take a batch from it (i didn't understand this part)
            // Calling the remote method using the obtained object
            String res = stub.executeQuery("Q 1 3\n");

            // print final result
            System.out.println(res);

            // Sleep, choose a random amount first
            //Random rand = new Random();
            //Thread.sleep(rand.nextInt(9000) + 1000);

            // Repeat choosing another batch
            // ... res = stub.executeQuery("");


        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
