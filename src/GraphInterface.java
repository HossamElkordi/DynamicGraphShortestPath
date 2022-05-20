import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GraphInterface extends Remote {
    public String executeQuery(String queries, String clientNode) throws RemoteException, IOException;
}
