import java.net.*;
import java.io.*;

public class ComsClient implements Runnable
{  
	private Socket              	socket = null;
	private Thread              	thread = null;
	private DataOutputStream 	 streamOut = null;
	private String          	serverName = null;
	private int             	serverPort = -1;	
	private boolean              connected = false;
	private ComsClientThread         client = null;
	
	
	private String name = null;
	
	private int pingDelay = 1000; // delay between each ping
	private int connectionDelay = 2000; // delay before try another connection
	
	public ComsClient(String _serverName, int _serverPort, String _name)
	{  
		serverName = _serverName;
		serverPort = _serverPort;
		name = _name;
		connect();
		
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	 public void handle(String msg)
	   {  if (msg.equals(":stop:"))
	      {  
		   	 System.out.println("Good bye. Press RETURN to exit ...");
	         stop();
	      }
	      else
	         System.out.println(msg);
	   }
	
	private void connect()
	{
		while(socket == null)
		{
			System.out.println("[Client] Establishing connection. Please wait ...");
			try
			{  
				socket = new Socket(serverName, serverPort);
		    	System.out.println("[Client] Connected: " + socket);		    	
		    	start();
			}
			catch(UnknownHostException uhe)
			{
				System.out.println("[Client] Host unknown: " + uhe.getMessage()); 
			}
			catch(IOException ioe)
			{  
			   System.out.println("[Client] " + ioe.getMessage()); 
			}
			
			try {
				Thread.sleep(connectionDelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void start() throws IOException {
		streamOut = new DataOutputStream(socket.getOutputStream());
		streamOut.writeUTF(name);
        streamOut.flush();
        
        connected  = true;
        if (thread == null)
        {  
           client = new ComsClientThread(this, socket);
           thread = new Thread(this);                   
           thread.start();
        }
	}

	public void stop()
	{
		connected = false;
		socket = null;
		streamOut = null;		
		thread = null;
	}
	

	public void run()
	{
		while(connected == true)
		{
			try
	        {  
				streamOut.writeUTF(":ping:");
	            streamOut.flush();
	        }
	        catch(IOException ioe)
	        {  
	        	System.out.println("[Client] Sending error: " + ioe.getMessage());
	            stop();
	        }
			
			try {
				Thread.sleep(pingDelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		System.out.println("[Client] Connection lost");
		connect();
	}
   
	public void sendAll(String _msg)
	{
		try
        {  
			streamOut.writeUTF("0,"+_msg);
            streamOut.flush();
        }
        catch(IOException ioe)
        {  
        	System.out.println("[Client] Sending error: " + ioe.getMessage());
            stop();
        }
	}
	
	public void sendTo(String _name, String _msg)
	{
		try
        {  
			streamOut.writeUTF(_name+","+_msg);
            streamOut.flush();
        }
        catch(IOException ioe)
        {  
        	System.out.println("[Client] Sending error: " + ioe.getMessage());
            stop();
        }
	}
   
	public static void main(String args[])
	{  
		
		
		if(args.length != 3)
		{
			System.out.println("Usage : java -jar serveur.jar <server_ip> <port> <client_name>");
		}else{
			ComsClient client = new ComsClient(args[0],Integer.parseInt(args[1]),args[2]);
			client.sendAll("message global");
			client.sendTo("AZERTY", "coucou");
		}
		
		
	}



	
}