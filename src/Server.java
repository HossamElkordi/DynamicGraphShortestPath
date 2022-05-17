import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server{
    public Server() {
    }

    public static void main(String args[]) {
        try {
            // set the server IP and create registry at port 1099
            // Don't know how to create the server port, try https://www.mscharhag.com/java/java-rmi-things-to-remember
            // All properties should be passed from the class Start (isn't created yet)
            System.setProperty("java.rmi.server.hostname", "192.168.1.7");
            LocateRegistry.createRegistry(1099);

            // Instantiating the implementation class
            Graph g = new Graph("C:\\Users\\ahm_e\\IntelliJIDEAProjects\\DynamicGraphShortestPath\\input");
            System.out.println("R");

            // Exporting the object of implementation class
            // (here we are exporting the remote object to the stub)
            // I think here the server port should be used
            GraphInterface stub = (GraphInterface) UnicastRemoteObject.exportObject(g, 49053);

            // Binding the remote object (stub) in the registry
            Registry registry = LocateRegistry.getRegistry("192.168.1.7");

            registry.bind("Update", stub);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
