import com.onsdomein.proxy.ProxyOnsDomein;

import java.io.IOException;

/**
 * Mock of a HuisCentrale application.
 * This mock is for (Unit)Testing purposes only.
 * This Class will be able to do all HC activities with the server that needs to be tested without
 * any further functionality.
 * Connected Arduino is Mocked and is out of scope for the server to be tested.
 */
public class HuisCentraleMOCK {
    // The mock client_id
    private String client_id = "5678";
    // Proxy that handles server communication
    private ProxyOnsDomein proxyOnsDomein = new ProxyOnsDomein();

    HuisCentraleMOCK() {
        // After first boot of app connection is made, then passes on to listeningForMessage (from server)
        try {
            proxyOnsDomein.connectClientToServer(client_id);
            listenForMessage();
        } catch (IOException e) {
            System.out.println("HC cannot connect to server: " + e);
        }
    }

    /**
     * Method to listen to incoming request from the server to be tested
     */
    private void listenForMessage() {
        // get messages from server
        while (true) {
            String request;
            try {
                request = proxyOnsDomein.receiveRequest();
            } catch (Exception e) {
                System.out.println("Connection with server lost. " + e);
                break;
            }
            System.out.println("received from server: " + request);
            // what you do from here is up to you.
            sendToArduino(request);
        }
    }

    /**
     * A not mocked HC will send messages to a connected Arduino, in this mock an Arduino is not connected, not in
     * scope of the tests and will be left out of functional scope.
     * @param message, the message to be send to an Arduino
     */
    private void sendToArduino(String message) {
        // We will mock a messgage send to an Arduino and we will mock a reaction received from the Arduino
        String reactionFromArduino;
        // You know the protocol so this can't be a surprise.
        String[] messageSplit = message.split(";", 0);
        // only send part 3 and 4 of the used protocol to Arduino or handle this in HC code
        // Server side the protocol is shortend to 3 parts because we will not send the HC client_id
        // Mock sending message to Arduino
        if (messageSplit.length == 3) {
            System.out.println(messageSplit[2] + " send to Arduino");
            // Mock reaction received from Arduino
            reactionFromArduino = "Arduino received message: " + messageSplit[2] + " and says hi!";
//            reactionFromArduino = "setHc;" + client_id + ";" + messageSplit[1] + ";" + messageSplit[2];
        } else {
            reactionFromArduino = "res;Wrong protocol used: No message send to Arduino";
        }
        receivedFromArduino(messageSplit[1], reactionFromArduino);

    }

    /**
     * Handle the mock reaction from Arduino, sending the mock reaction back to the GA via the server
     * @param reactionFor, in the received message by HC the adress of the requester is send, this adres is used to respond
     * @param reactionFromArduino, the response message
     */
    private void receivedFromArduino(String reactionFor, String reactionFromArduino) {
        try {
            //TODO: make sure you always respond, the server will if HC is offline, GA will wait for a reply
//            proxyOnsDomein.sendRequest(client_id, reactionFor, reactionFromArduino);
            proxyOnsDomein.sendResponse("setHc",client_id, reactionFor, reactionFromArduino);
        } catch (Exception e) {
            System.out.println("HC kan geen contact maken met de server. " + e);
        }
    }
}
