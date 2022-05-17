import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class Client {
    private Client(String ip) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(ip);
        GraphInterface stub = (GraphInterface) registry.lookup("Update");
        String res = stub.executeQuery("Q 1 3\n");
        System.out.println(res);
        // Sleep, choose a random amount first
        //Random rand = new Random();
        //Thread.sleep(rand.nextInt(9000) + 1000);
    }

}
