package testng.commons.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IExecutionListenerRemote extends Remote{
	public void report(String message) throws RemoteException;
}
