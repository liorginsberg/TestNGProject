package org.testngwebrunner.app.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IExecutionActionRemote extends Remote{
	public void pause() throws RemoteException;
	public void resume() throws RemoteException;
	public void skipAll() throws RemoteException;
}
