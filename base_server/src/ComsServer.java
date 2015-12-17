import java.net.*;
import java.io.*;

public class ComsServer implements Runnable
{  
	private ComsServerThread clients[] = new ComsServerThread[50];
	private String           clientsName[] = new String[50];
	private ServerSocket server = null;
	private Thread       thread = null;
	private int clientCount = 0;
	private int ping=0;
	public ComsServer(int port)
	{  
		try
		{  
			System.out.println("[SERVER] Binding to port " + port + ", please wait  ...");
			server = new ServerSocket(port);  
			System.out.println("[SERVER] Server started: " + server);
			start(); 
		}
		catch(IOException ioe)
		{ 
			System.out.println("[SERVER] Can not bind to port " + port + ": " + ioe.getMessage()); 
		}
	}
	
	public void setClientName(int _id, String _name)
	{
		clientsName[_id] = _name;
	}
	
	public void run()
	{  
		while (thread != null)
		{
			try
			{
				System.out.println("[SERVER] Waiting for a client ..."); 
				addThread(server.accept()); 
			}
			catch(IOException ioe)
			{ 
				System.out.println("Server accept error: " + ioe); stop(); 
			}
		}
	}
   
	public void start()
	{
		if (thread == null)
		{
			thread = new Thread(this); 
			thread.start();
		}
	}
   
	public void stop()
	{
		if (thread != null)
		{
			thread.stop(); 
			thread = null;
		}
	}
   
	private int findClientByID(int _id)
	{
		for (int i = 0; i < clientCount; i++)
			if (clients[i].getID() == _id)
				return i;
		return -1;
	}
	
	private int findClientByName(String _name)
	{
		for (int i = 0; i < clientCount; i++)
			if (clientsName[i].equals(_name))
				return i;
		return -1;
	}
   
	public synchronized void handle(int ID, String input)
	{
		
		if (input.equals(":stop:"))
		{
			clients[findClientByID(ID)].send(":stop:");
			remove(ID); 
		}
		else if(input.equals(":ping:"))
		{
			ping++;
			System.out.println(":ping:");
		}
		else
		{
			String parts[] = input.split(",");
			
			if(parts[0].equals("0"))
			{
				sendAll(ID, parts[1]);
				System.out.println(clientsName[ID] + " (G) : " + input);
			}
			else
			{
				sendTo(ID,parts[0],parts[1]);
				System.out.println(clientsName[ID] + " ("+parts[0]+") : " + parts[1]);
			}
							
			
		}
			  
	}
   
	private void sendAll(int _id, String _msg)
	{
		for (int i = 0; i < clientCount; i++) 
		{
			if(_id != i)
			{
				clients[i].send(_msg);
			}				
		}
	}
	
	private void sendTo(int ID,String _name, String _msg)
	{
		int id = findClientByName(_name);
		
		if(id != -1 && id != ID)
		{
			clients[id].send(_msg);
		}
		
	}
	
	public synchronized void remove(int ID)
	{
		int pos = findClientByID(ID);
		if (pos >= 0)
		{ 
			ComsServerThread toTerminate = clients[pos];
			System.out.println("Removing client thread " + ID + " at " + pos);
			if (pos < clientCount-1) {
				for (int i = pos+1; i < clientCount; i++)
				{
					clients[i-1] = clients[i];
					clientsName[i-1] = clientsName[i];
				}
			}
			clientCount--;
			try
			{  
				toTerminate.close(); 
			}
			catch(IOException ioe)
			{
				System.out.println("Error closing thread: " + ioe); 
			}
			toTerminate.stop(); }
	}
   
	private void addThread(Socket socket)
	{
		if (clientCount < clients.length)
		{
			System.out.println("Client accepted: " + socket);
			clients[clientCount] = new ComsServerThread(this, socket,clientCount);
			try
			{
				clients[clientCount].open(); 
				clients[clientCount].start();  
				clientCount++; 
			}
			catch(IOException ioe)
			{
				System.out.println("Error opening thread: " + ioe); 
			}
		}
		else
			System.out.println("Client refused: maximum " + clients.length + " reached.");
	}
   
	public static void main(String args[])
	{
		if(args.length != 1)
		{
			System.out.println("Usage : java -jar serveur.jar <port>");
		}else{
			ComsServer server = new ComsServer(Integer.parseInt(args[0]));
		}
	}
	
}