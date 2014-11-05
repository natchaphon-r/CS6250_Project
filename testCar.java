/* Pseudo code 1
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
// pseudo code 2 
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
class eventId{
  String farthestVid;
  int longestDistance;
  String vIdsHeard = [];
  boolean broadcast;
} 
  
class testCar { 
  boolean isDangerous = False;
  eventIds = new HashMap<String, eventId>();
  String vehicleID = "";
  
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
        // simulator assigns characteristic of an event .....
        set isDangerous to true and eventId.broadcast to true for the given eventID
      }
      if options = carReq { // request for car info to calculate distance to it 
        // parameter = vehicle ID
      }

    }

	
	public void broadcastEWM(String eventId){ 
		//This broadcast is simple, there is no time to wait for response, no determining longest distance.
		//This is just simply a broadcast..that's it
		simulatorBroadcast("EMW", eventId, vehicleId);
	}
	
	
    public void primaryBroadcastEWM(){
		// tell simulator it's sending to everyone and simulator will decide who will receive the broadcast
/*
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



    }
	
    public void broadcastACK(String eventId){
      //calculate the vehicleID that's furthest away
	  if(eventIds.containsKey(eventId)) {
		eventIds.get(eventId).broadcast = false; 
		simulatorBroadcast("AWK", eventIds.get(eventId).farthestVid, eventId);
	  }
	  else { System.out.println("Attempted to Broadcast an ACK for event ID " + eventID + ". But wasn't found in the event list"); }
	}
	  
    public void listenEWM(){
	/* //Every car should always be listening for EWMs...always
	
	If (messages==EWM) {   
		If (the event-id is the same) {discard ;} 
		Else broadcastEWM() ;
	} 
	*/

    }
    
	public void listenEWM_Response() {
	
/* If (duration<t){ 
	If (received any EWMs from behind  **THIS IS THE LISTENEWM_RESPONSE() FUNCTION   ){     
		if (event-id is the same) {
			if (Vid was received before)  {Ignore message}; 
			Else {
				record the Vid into the queue and calculate the distance. If distance > prev greatest distance then update the Vid and distance;
			}
		}
		Else {  broadcastEWM(this new heard event-id)  ;}
	}  */
	
	     if EWM comes from a car behind you or traveling in the opposite direction
        drop it 
        else broadcastEWM(eventID)
		
	}
	
	public void listenACK(){
	//All cars should be listening for an ACK...always
	
		/* Else (message==ACK) {  ** THIS IS THE LISTENAWK() FUNCTION **
		If (vehicle-id==self ‘vehicle-id) {
			Become a chosen broadcast vehicle aka PRIMARYBROADCASTEWM() ;
		}
		Else stop broadcast ;
	 */
		if(vehicleId == heardId) {
			PrimaryBroadcastEWM(heard_eventId);
		}
		else {
			eventIds[heard_eventId].broadcast = false;
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
