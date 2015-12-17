import net.java.games.input.*;
import net.java.games.input.Component.Identifier;


public class USBcontroller extends Thread{
	
	private Controller droneControl;
	private ComsClient client;
	private float Xaxis, Yaxis, Zaxis, Power;
	private float sensibilityLeft = 1, sensibilityRight = 1;
	
	private float sensitivityMax = 1.5f, sensitivityMin = 0.5f;
	
	private int deltaT = 50; // temps (en ms) entre 2 envois des valeurs des axes
	
	private boolean ControllerisConnected = true;
	
	private String remotePeer = "CONTROL_DRONE";
	
	public USBcontroller(Controller cont, ComsClient client){
		
		this.droneControl = cont;
		this.client = client;
		start();

	}
	
	public void close(){
		ControllerisConnected = false;		
	}
	
	public void	run(){
		
		while(client.isConnected() && ControllerisConnected){
			
			droneControl.poll();
			EventQueue queue = droneControl.getEventQueue();
			Event event = new Event();
			
			while(queue.getNextEvent(event)){
				
				Component comp = event.getComponent();
				if (comp.isAnalog()){
					Identifier componentIdentifier = comp.getIdentifier();					
					float value = event.getValue();
					if (componentIdentifier == Component.Identifier.Axis.X){
		            	Zaxis = (float) Math.floor(10000 * value + 0.5) / 10000;
		            }
		            else if (componentIdentifier == Component.Identifier.Axis.Y){
		            	Power = (float) -Math.floor(10000 * value + 0.5) / 10000;
		            }
		            else if (componentIdentifier == Component.Identifier.Axis.Z){
		            	Xaxis = (float) Math.floor(10000 * value + 0.5) / 10000;
		            }
		            else if (componentIdentifier == Component.Identifier.Axis.RZ){
		            	Yaxis = (float) -Math.floor(10000 * value + 0.5) / 10000;
		            }					
				}
				else {
					Identifier componentIdentifier = comp.getIdentifier();
					float value = event.getValue();
					if(componentIdentifier.getName().equals("4")){ // L1
							if(value == 1.0f){
								sensibilityLeft = sensitivityMax;
							}
							else{
								sensibilityLeft = 1f;
							}
					}
					else if(componentIdentifier.getName().equals("6")){ //L2
							if(value == 1.0f){
								sensibilityLeft = sensitivityMin;
							}
							else{
								sensibilityLeft = 1f;
							}
					}
					else if(componentIdentifier.getName().equals("5")){ //R1
						if(value == 1.0f){
							sensibilityRight = sensitivityMax;
						}
						else{
							sensibilityRight = 1f;
						}
					}
					else if(componentIdentifier.getName().equals("7")){ //R2
						if(value == 1.0f){
							sensibilityRight = sensitivityMin;
						}
						else{
							sensibilityRight = 1f;
						}
					}					
				}
			}
			System.out.println("[DEBUG] send data");
			this.sendAxisValues();
			
			try {
	            Thread.sleep(deltaT);
	         } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	         }
			
		}
		
	}

	
	public void sendAxisValues(){
		String forward = Float.toString(Yaxis * sensibilityRight);
		String side = Float.toString(Xaxis * sensibilityRight); 
		String rotation = Float.toString(Zaxis * sensibilityLeft); 
		String throttle = Float.toString(Power * sensibilityLeft);		
		String msg = "* " + forward + " " + side + " " + rotation + " " + throttle + " *";		
		client.sendTo(remotePeer, msg);		
	}

	
	
	
}
