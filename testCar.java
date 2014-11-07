/*
EWM is used for security
warning, which contains the sender’s position and ID and direction of travel, the ID 
and location of the event and event timestamp, and message lifetime.
*/
/* Pseudo code 1 comments
//process of broadcast-vehicle-choosing. 
After broadcast an EWM message with an event-id, 
wait for period t to receive any messages from behind;
** THIS IS THE PRIMARYBROADCASTEWM() FUNCTION**
If (duration<t){ 
	If (received any EWMs from behind  ** THIS IS THE LISTENEWM_RESPONSE() FUNCTION**){   
		if (event-id is the same) {
			if (Vid was received before)  {Ignore message}; 
			Else {
				record the Vid into the queue and calculate the distance. If distance > prev greatest distance then update the Vid and distance;
			}
		}
		Else {broadcastEWM() with this event-id ;}
	} 
	Else {after broadcast an EWM, still wait for a period t ;}
} 

Else {choose the Vehicle and send ACK; stop broadcast ;  ** THIS IS THE BROADCASTAWK() FUNCTION **  }  */

/*
// pseudo code 2 comments
If (A vehicle received any messages) { 
	If (messages==EWM) {  ** THIS IS THE LISTENEWM() FUNCTION **  
		If (the event-id is the same) {discard ;} 
		Else broadcastEWM() ;
	} 
	Else (message==ACK) {  ** THIS IS THE LISTENAWK() FUNCTION **
		If (vehicle-id==self ‘vehicle-id) {
			Become a chosen broadcast vehicle aka PRIMARYBROADCASTEWM() ;
		}
		Else stop broadcast ;
	}
}

*/


import java.io.*; 
import java.net.*; 

class event {
	String ID;
	int position;
	String timeStamp;
	boolean broadcast;
	
	public event(String eID, int pos, String stamp, boolean broadcastIt) {
		ID = eID; 
		position = pos;
		timeStamp = stamp;
		broadcast = broadcastIt;
	}
	
}

class eventCreated{
	String farthestVID = "";
	int longestDistance = 0;
	ArrayList<String> vIDsHeard = new ArrayList<String>();
	event eventDetails;
	public eventCreated(event e) {
		eventDetails = e;
	} 
} 

class EWMmessage {
	int senderPosition;
	int senderDirection;
	String senderVID;
	event e;
	public EWMMessage(int sendPos, int SendDir, String Vid, event ev) {
		senderPosition = sendPos;
		senderDirection = SendDir;
		senderVID = Vid;
		e = ev;
	}
}
  
class testCar { 
	boolean isDangerous = False;
	eventIDListening = new HashMap<String, eventCreated>(); //Stores all the eventCreated by this car. AKA all the events created by this car
	eventsHeard = new HashMap<String, event>; //Stores all the events that this car has heard from the simulator. Key = eventID
	String vehicleID = "";
	int position; //prob won't be an int
	int direction; //prob won't be an int
	
	int delay = 1; //1ms delay for listening to EWM responses 
	int listenedTime = 0;
	int duration = 20;  // keep listening for responses for 20 milliseconds

  
    public void requestSim(String options){
      // options
      // initial == obtain VehicleID, VehiclePOS, VehicleVEL
      // create_EMW == request for unique eventID
      // carReq == request for car info (position)

      // Create socket connection to the simulator

      if options = initial{ // all cars have to call this to join the network 
        //send somethin
      }
      if options = create_EWM{ // only dangerous car, need simulator to keep track of all event ids
        // parameter = vehicleID
        // simulator assigns characteristic of an event (ID, position of event, timestamp)
        //set isDangerous to true
		//Add the event infto to the vehicle's eventIDs HasMap and set eventID.broadcast to true for the given eventID
      }
      if options = carReq { // request for car info to calculate distance to it 
        // parameter = this.vehicleID
      }

    }

	
	public void broadcastEWM(event e){ 
		//This broadcast is simple, there is no time to wait for response, no determining longest distance.
		//This is just simply a broadcast..that's it
		//contains the sender’s position and ID and direction of travel, the ID and location of the event and event timestamp, 
		//and message lifetime.
		EWMmessage message = new EWMmessage(this.position, this.direction, this.vehicleID, e);
		simulatorEMWBroadcast(message);
	}
	
	
    public void primaryBroadcastEWM(event e){
		EWMmessage message = new EWMmessage(this.position, this.direction, this.vehicleID, e);
		simulatorEMWBroadcast(message);
		//First we create the new action listener object. 
		ActionListener actionToPerform = new ActionListener() {  
			//Then we create the method that is called each time the action listener is called.
			public void actionPerformed(ActionEvent evt) { 
				//...Perform your task here, whatever it may be.
					if(listenedTime < duration){
						listenedTime++;
						listenEWM_Response(e);
					}
			}
		};
		new Timer(delay, actionToPreform).start();
			
	} 
	
    public void broadcastACK(event e){
		//calculate the vehicleID that's furthest away
		if(eventIDListening.containsKey(e.eventID)) {
			eventIDListening.get(e.eventID).broadcast = false; 
			simulatorAWKBroadcast(eventIDListening.get(e.eventID).farthestVid, e);
		}
		else { System.out.println("Attempted to Broadcast an ACK for event ID " + eventID + ". But wasn't found in the event list"); }
	}
	  
    public void listenEWM(){
		/*
		A vehicle ignores an EWM if it comes from behind with respect to its travel direction, but can infer that there is an 
		emergency event ahead when an EWM comes from the front, and immediately decelerates and broadcasts EWM of its own.
		 

			If (the event-id is the same) {discard ;} 
			Else broadcastEWM() ;
		*/
		// Assume that the simulator will return an arrayList with EWM messages called RecievedEWM
		for(EWMmessage : RecievedEWM) {
			if(!isBehind(this.position, EWMmessage.senderPosition) && movingSameDirection(this.direction, EWMmessage.senderDirection) && !eventsHeard.containsKey(EWMmessage.e.eventID)) {
				eventsHeard.put(EWMmessage.e.eID, EWMmessage.e);
				broadcastEWM(EWMmessage.e);
			}
		}
    }
    
	public void listenEWM_Response(event e) {
	/*
	If (received any EWMs from behind  **THIS IS THE LISTENEWM_RESPONSE() FUNCTION   ){     
		if (event-id is the same) {
			if (Vid was received before)  {Ignore message}; 
			Else {
				record the Vid into the queue and calculate the distance. If distance > prev greatest distance then update the Vid and distance;
			}
		}
		Else {  broadcastEWM(this new heard event-id)  ;} //this is taken care of by the listenEWM() function
	}  */
	// assume that we can get an array of EWM messages from the simulator called RecievedEWM
		for(EWMmessage : RecievedEWM) {
			if(isBehind(this.position, EWMmessage.senderPosition) && eventIDListening.contains(EWMmessage.e.eID))  {
				if(!eventIDListening.get(EWMmessage.e.eID).vIDsHeard.contains(EWMmessage.senderVid)) {
					eventIDListening.get(EWMmessage.e.eID).vIDsHeard.put(EWMmessage.senderVid);
					int distance = getDistance(EWMmessage.senderPosition, this.position);
					if(distance > eventIDListening.get(EWMmessage.e.eID).longestDistance) {
						eventIDListening.get(EWMmessage.e.eID).longestDistance = distance;
						eventIDListening.get(EWMmessage.e.eID).farthestVid = EWMmessage.senderVid;
					}
				}
			}
		}
	}
	
	public void listenACK(){
	//All cars should be listening for an ACK...always
	
		/* Else (message==ACK) {  ** THIS IS THE LISTENAWK() FUNCTION **
		If (vehicle-id==self ‘vehicle-id) {
			Become a chosen broadcast vehicle aka PRIMARYBROADCASTEWM() ;
		}
		Else stop broadcast ;
	 */
		if(vehicleID == heardID) {
			primaryBroadcastEWM(heardEvent);
		}
		else {
			eventHeard.get(heardEvent.eID).broadcast = false;
		}
	}
}



    public static void main(String args[]) throws Exception 
    { 

     try {
        String serverHostname = new String ("127.0.0.1");

        if (args.length > 0)
           serverHostname = args[0];
  
      BufferedReader inFromUser = 
        new BufferedReader(new InputStreamReader(System.in)); 
  
      DatagramSocket clientSocket = new DatagramSocket(); 
  
      InetAddress IPAddress = InetAddress.getByName(serverHostname); 
      System.out.println ("Attemping to connect to " + IPAddress + 
                          ") via UDP port 50000");
  
      byte[] sendData = new byte[1024]; 
      byte[] receiveData = new byte[1024]; 
  
      System.out.print("Enter Message: ");
      String sentence = inFromUser.readLine(); 
      sendData = sentence.getBytes();         

      System.out.println ("Sending data to " + sendData.length + 
                          " bytes to server.");
      DatagramPacket sendPacket = 
         new DatagramPacket(sendData, sendData.length, IPAddress, 50000); 
  
      clientSocket.send(sendPacket); 
  
      DatagramPacket receivePacket = 
         new DatagramPacket(receiveData, receiveData.length); 
  
      System.out.println ("Waiting for return packet");
      clientSocket.setSoTimeout(10000);

      try {
           clientSocket.receive(receivePacket); 
           String modifiedSentence = 
               new String(receivePacket.getData()); 
  
           InetAddress returnIPAddress = receivePacket.getAddress();
     
           int port = receivePacket.getPort();

           System.out.println ("From server at: " + returnIPAddress + 
                               ":" + port);
           System.out.println("Message: " + modifiedSentence); 

          }
      catch (SocketTimeoutException ste)
          {
           System.out.println ("Timeout Occurred: Packet assumed lost");
      }
  
      clientSocket.close(); 
     }
   catch (UnknownHostException ex) { 
     System.err.println(ex);
    }
   catch (IOException ex) {
     System.err.println(ex);
    }
  } 
} 
