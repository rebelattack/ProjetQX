import java.net.*;
import java.io.*;

public class ComsServerThread extends Thread
{  
	private ComsServer       server    = null;
	private Socket           socket    = null;
	private int              ID        = -1;
	private DataInputStream  streamIn  =  null;
	private DataOutputStream streamOut = null;
	private String           name      = null;
	
	public ComsServerThread(ComsServer _server, Socket _socket, int _id)
	{
		super();
		server = _server;
		socket = _socket;
		ID     = _id;
	}
	
	public void send(String _msg)
	{
		try
		{
			streamOut.writeUTF(_msg);
			streamOut.flush();
		}
		catch(IOException ioe)
		{
			System.out.println(ID + " ERROR sending: " + ioe.getMessage());
			server.remove(ID);
			stop();
		}
	}
   
	public int getID()
	{
		return ID;
	}
   
	public void run()
	{
		System.out.println("Server Thread " + ID + " running.");
		while (true)
		{
			try
			{
				if(name == null)
				{
					name = streamIn.readUTF();
					System.out.println("[SERVER] Client : "+name+" is connected");
					server.setClientName(ID,name);
					
				}else{						
					server.handle(ID, streamIn.readUTF());
				}
				
			}
			catch(IOException ioe)
			{
				System.out.println(ID + " ERROR reading: " + ioe.getMessage());
				server.remove(ID);
				stop();
			}
		}
	}
   
	public void open() throws IOException
	{
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}
	
	public void close() throws IOException
	{
		if (socket != null)    socket.close();
		if (streamIn != null)  streamIn.close();
		if (streamOut != null) streamOut.close();
	}
}