import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ComsServer implements Runnable{
	
	private int clientPort = 1234;
	private int clientServer = 1235;
	private ComsServer[] clients = new ComsServer[10];
	private Thread thread = null;
	
	
	private ServerSocket server = null; // For client (Gamepad, ETAT, SLAM)
	
	public void ComsServer(){
		
		System.out.println("[SERVER] Starting server...");
		try {			
			server = new ServerSocket(clientPort);
			this.start();
		} catch (IOException ioe) {
			System.out.println("[SERVER] Error : \n\t Server A can not bind to port " + clientPort + ": " + ioe.getMessage());
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

	@SuppressWarnings("deprecation")
	public void stop()
	{  
		if (thread != null)
		{  
			thread.stop(); 
			thread = null;
		}
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
	   			System.out.println("[SERVER] Error : \n\t Server accept error: " + ioe); stop(); 
	   		}
	   	}
	}
	
	private void addThread(Socket socket)
   	{  
   		if (clientCount < clients.length)
   		{  
   			System.out.println("[SERVER] Client accepted: " + socket);
   			clients[clientCount] = new ComsServerThread(this, socket);
   			try
   			{
   				clients[clientCount].open(); 
   				clients[clientCount].start();   				
   				clientCount++; 
   			}
   			catch(IOException ioe)
   			{ 
   				System.out.println("[SERVER] Error : \n\t Error opening thread: " + ioe); } 
   			}
   		else
   		{
   			System.out.println("[SERVER] Error : \n\t Client refused: maximum " + clients.length + " reached.");
   		}
   	}
	
}
