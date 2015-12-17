import java.lang.reflect.Constructor;
import java.util.Arrays;

import net.java.games.input.*;

public class USBmanager implements Runnable{
	
	
	private ComsClient client;
	private Controller usbcontroller;
	private ControllerEnvironment ce;
	private Thread thread;
	private USBcontroller tep;
	
	private String IHMname = "IHM";
	
	public USBmanager(String _servername, int _port) throws ReflectiveOperationException {
		client = new ComsClient(_servername,_port,"CONTROL_PC");
        thread = new Thread(this);                   
        thread.start();
	}	
	
	
	public static void main(String[] args) throws ReflectiveOperationException {
		USBmanager usb = new USBmanager("127.0.0.1",1314);
	}
	

	@Override
	public void run() {		
		while(true){	

			if(!client.isConnected()) { // si déconnexion du client
				usbcontroller = null;
			}
			
			Controller[] controllerList = null;
			try {
				controllerList = createDefaultEnvironment().getControllers();
			} catch (ReflectiveOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			// Si on a déjà un controller
			if(usbcontroller != null) {
				boolean stillConnected = false;
				for(Controller newcontroller : controllerList ) {
					
					if(newcontroller.getName().equals( usbcontroller.getName())) {
						stillConnected = true;
					}
				}
				
				// Si le controller est déconnecté.
				if(!stillConnected) {
					usbcontroller = null;
					tep.close();
					tep = null;
					//System.out.println("Controller disconnected");
				}				
			}
			// Sinon
			else {
				//System.out.println("Looking for controller");
				try {
					usbcontroller = findOneController(); // on cherche un nouveau
					if(usbcontroller == null) {
						sendIHMControllerFound(false);
					}
					else {
						sendIHMControllerFound(true);
						tep = new USBcontroller(usbcontroller, client);						
					}
				} catch (ReflectiveOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	// méthode qui envoie un message à l'IHM si aucun controller n'est branché
		public void sendIHMControllerFound(boolean found){
			String msg;
			if(found){
				msg = "Controller trouvé.";
				
			}else{
				 msg = "Pas de controller trouvé. Veuillez brancher un gamepad ou un joystick";				
			}
			
			while (!client.isConnected()){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			client.sendTo(IHMname, msg);
					
		}
	
	private static ControllerEnvironment createDefaultEnvironment() throws ReflectiveOperationException {

	    // Find constructor (class is package private, so we can't access it directly)
	    Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>)
	        Class.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];

	    // Constructor is package private, so we have to deactivate access control checks
	    constructor.setAccessible(true);

	    // Create object with default constructor
	    return constructor.newInstance();
	}
	
	public Controller findOneController() throws ReflectiveOperationException{
		Controller[] controllerList = createDefaultEnvironment().getControllers();
		Controller controllerFound = findGamepad(controllerList);
		if (controllerFound==null){
			controllerFound = findStick(controllerList);
			
		}
		//System.out.println("Found controller: " + controllerFound.getName() + "\ncontroller type: " + controllerFound.getType());
		return controllerFound;
	
	}	
	
	
	public Controller findStick(Controller[] controllerList){
		
		Controller firstStick=null;
		for(int i=0;i<controllerList.length && firstStick==null;i++) {
			if(controllerList[i].getType()==Controller.Type.STICK) {
             // Found a stick
				firstStick = controllerList[i];
			}
		}
		if(firstStick==null) {
        // Couldn't find a stick
			//System.out.println("Found no stick");
			return firstStick;
		}
     
		return firstStick;
	
	}	
	
	public Controller findGamepad(Controller[] controllerList){
		
		Controller firstGamepad=null;
		for(int i=0;i<controllerList.length && firstGamepad==null;i++) {
			if(controllerList[i].getType()==Controller.Type.GAMEPAD) {
             // Found a gamepad
				firstGamepad = controllerList[i];
			}
		}
		if(firstGamepad==null) {
        // Couldn't find a gamepad
			//System.out.println("Found no gamepad"); 
			return firstGamepad;
		}
     
		return firstGamepad;
	
	}	

}
