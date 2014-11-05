import java.io.*; 
import java.net.*; 
class eventId{
  String Eid;
  String Vid;
  int distance;
} 

/* Pseudo code 1
//process of broadcast-vehicle-choosing. 
After broadcast an EWM message with an event-id, 
wait for period t to receive any messages from behind;
If (duration<t){ 
	If (received any EWMs from behind){ 
		if (event-id is the same) {
			if (message was received before)  {Ignore message}; 
			Else {
				insert the message into the queue; 
				Calculate the distance ;
			}
		}
		Else {broadcast EWM with this event-id ;}
	} 
	Else {after broadcast an EWM, still wait for a period t ;}
} 

Else {choose the Vehicle and send ACK; stop broadcast ;}  */

/*
// pseudo code 2 
If (A vehicle received any messages) { 
	If (messages==EWM) {
		If (the event-id is the same) {discard ;} 
		Else broadcast ;
	} 
	Else (message==ACK) {
		If (vehicle-id==self ‘vehicle-id) {
			Periodic broadcast EMW; 
			Become a chosen broadcast vehicle ;
		}
		Else stop broadcast ;
	}
}

*/

class testCar { 
  boolean isDangerous = False;
  List<eventId> eventIds = new ArrayList<eventId>();
  
    public void requestSim(String options){
      // options
      // initial == obtain VehicleID, VehiclePOS, VehicleVEL
      // create_EMW == request for unique eventID
      // carReq == request for car info (position)

      // Create socket connection to the simulator

      if options = initial{ // all cars have to call this to join the network 
        send somethin
      }
      if options = create_EWM{ // only dangerous car, need simulator to keep track of all event ids
        // parameter = vehicleID
        // simulator assigns characteristic of an event .....
        set isDangerous for that vehicle to be True
      }
      if options = carReq { // request for car info to calculate distance to it 
        // parameter = vehicle ID
      }

    }

    public void broadcastEWM(){
      // tell simulator it's sending to everyone and simulator will decide who will receive the broadcast?


    }
    public void broadcastACK(){
      calculate the vehicleID thats furthest away 
      and then broadcast(furthestID, eventID)

    }
    public void listenEWM(){

      if is the eventID the one you broadcast?
        calculate the furthest vehicle from you 
        broadcastACK()
      else  
        if EWM comes from a car behind you or traveling in the opposite direction
        drop it 
        else broadcastEWM(eventID)

    }
    public void listenACK(){
      if vehicleID == yours 
        are you broadcasting that eventID?

      return False otherwise
        stop broadcasting that eventID! 
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
