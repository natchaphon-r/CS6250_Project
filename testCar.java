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
	String TTL;
	boolean broadcast;
	public event(String eID, int pos, String ttl, boolean broadcast) {
		ID = eID; 
		TTL = ttl;
		broadcast = broadcast;
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
	int senderPosition_x;
	int senderPosition_y;
	int senderVelocity_x;
	int senderVelocity_y;
	String senderVID;
	event e;
	public EWMMessage(int sendPos_x, int sendPos_y, int SendVel_x, int SendVel_y, String Vid, event ev) {
		senderPosition_x = sendPos_x;
		senderPosition_y = sendPos_y;
		senderVelocity_x = SendVel_x;
		senderVelocity_y = SendVel_y;
		senderVID = Vid;
		e = ev;
	}
}
  
class testCar { 
	boolean isDangerous = False;
	eventIDListening = new HashMap<String, eventCreated>(); //Stores all the eventCreated by this car. AKA all the events created by this car
	eventsHeard = new HashMap<String, event>; //Stores all the events that this car has heard from the simulator. Key = eventID
	String vehicleID = "";
	int position_x;
	int position_y;
	int velocity_x;
	int velocity_y;
	
	int delay = 1; //1ms delay for listening to EWM responses 
	int listenedTime = 0;
	int duration = 20;  // keep listening for responses for 20 milliseconds

  
   /* public void requestSim(String options){
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

    } */

	
	public void broadcastEWM(event e){ 
		//This broadcast is simple, there is no time to wait for response, no determining longest distance.
		//This is just simply a broadcast..that's it
		//contains the sender’s position and ID and direction of travel, the ID and location of the event and event timestamp, 
		//and message lifetime.
		EWMmessage message = new EWMmessage(this.position_x, this.position_y, this.velocity_x, this.velocity_y, this.vehicleID, e);
		simulatorEMWBroadcast(message);
	}
	
	
   /* public void primaryBroadcastEWM(event e){
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
			
	}  */
	
    public void broadcastACK(event e){
		//calculate the vehicleID that's furthest away
		if(eventIDListening.containsKey(e.eventID)) {
			eventIDListening.get(e.eventID).broadcast = false; 
			simulatorAWKBroadcast(eventIDListening.get(e.eventID).farthestVid, e);
		}
		else { System.out.println("Attempted to Broadcast an ACK for event ID " + eventID + ". But wasn't found in the event list"); }
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
	
	public void listenMessage(String type, String sender_pos_x, String sender_pos_y, String sender_dir_x, sender_dir_y, String sender_id, 
		String ack_car_id, String event_id, String event_ttl) {
		if(type == 1) {
			//This is an EWM
			/*
			A vehicle ignores an EWM if it comes from behind with respect to its travel direction, but can infer that there is an 
			emergency event ahead when an EWM comes from the front, and immediately decelerates and broadcasts EWM of its own.
		 
			If (the event-id is the same) {discard ;} 
			Else broadcastEWM() ;
			*/
			if(!isBehind(this.position_x, this.position_y, sender_pos_x, sender_pos_y) && movingSameDirection(this.velocity_x, this.velocity_y, sender_dir_x, sender_dir_y) && !eventsHeard.containsKey(event_id)) {
				event Event = new event(event_id, event_ttl, true);
				eventsHeard.put(event_id, Event);
				broadcastEWM(Event);
			}
		}
		else if(type ==2) {
			//This is an ACK
			/* Else (message==ACK) {  ** THIS IS THE LISTENAWK() FUNCTION **
			If (vehicle-id==self ‘vehicle-id) {
				Become a chosen broadcast vehicle aka PRIMARYBROADCASTEWM() ;
			}
			Else stop broadcast ;
			*/
			if(this.vehicleID == ack_car_id) {
				event Event = new event(event_id, event_ttl, true);
				//primaryBroadcastEWM(Event);
			}
			else {
				eventsHeard.get(event_id).broadcast = false;
			}
		}
		
		
	}
    
