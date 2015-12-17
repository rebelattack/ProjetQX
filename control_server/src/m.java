import java.io.IOException;
import java.net.ServerSocket;

public class m {
	
	public static void main(String[] args) throws IOException
	{  
		
        ServerSocket listener = new ServerSocket(1235);
        try {
            while (true) {
                //new ComsServer(listener.accept()).start();
            }
        } finally {
            listener.close();
        }

	}
	
	 /*public static void main(String args[])
	   {  
		  ComsClient client = null;
	      if (args.length != 2)
	         System.out.println("Usage: java ChatClient host port");
	      else
	         client = new ComsClient(args[0], Integer.parseInt(args[1]));
	   }*/
}
