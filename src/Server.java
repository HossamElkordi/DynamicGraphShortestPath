import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server{

    String nodeName;
    public Server(int port, String ip, String nodeName, String initialGraph) {
        try {
            this.nodeName = nodeName;
            // Instantiating the implementation class
            Graph g = new Graph(initialGraph);

            // Exporting the object of implementation class
            // (here we are exporting the remote object to the stub)
            // I think here the server port should be used
            GraphInterface stub = (GraphInterface) UnicastRemoteObject.exportObject(g, port);

            // Binding the remote object (stub) in the registry
            Registry registry = LocateRegistry.getRegistry(ip);

            registry.bind("Update", stub);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
