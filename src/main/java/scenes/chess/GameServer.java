package scenes.chess;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import network.KryoRegister;
import network.requests.JoinRequest;
import network.requests.KryoRequest;
import network.requests.Probe;
import network.responses.ProbeResponse;
import util.MathUtils;

public class GameServer {

    Server server = new Server();
    public ArrayList<String> messages = new ArrayList<String>();

    private static int gameID = 0;

    /*
     * Starts the server.
     */
    public void start () {
        // Generate a random game ID
        gameID = MathUtils.randomInt(100000, 999999);
        
        // Start the server on UDP and TCP ports 54553 and 54777 respectively.
        server.start();
        try {
            server.bind(54553, 54777);
        } catch (IOException e) {
            System.out.println("[SERVER] Failed to bind to ports 54553 and/or 54777. Check to make sure they are not in use by another program or instance of this program.");
            e.printStackTrace();
            System.exit(1);
        }

        KryoRegister.register(server);

        server.addListener(new Listener() {
            public void received(Connection connection, Object req) {
                // If the request doesn't extend KryoRequest, ignore it.
                if (!(req instanceof KryoRequest)) return;

                if (req instanceof Probe) {
                    probe(connection, (Probe) req);
                }

                if (req instanceof JoinRequest) {
                    addClient(connection, (JoinRequest) req);
                }

            }
        });        
    }

    /*
     * Adds a client to the server.
     */
    private void addClient (Connection connection, JoinRequest request) {
        System.out.println("[SERVER] Client " + request.name + " has joined the server.");
        messages.add(request.name + " has joined the server.");


    }

    /*
     * Responds to a probe request.
     */
    private void probe (Connection connection, Probe probe) {
        ProbeResponse response = new ProbeResponse();
        response.gameID = gameID;
        response.open = true;

        System.out.println("[SERVER] Responding to probe with ID: " + response.gameID);

        connection.sendTCP(response);
    }

    /*
     * Returns the IP of the computer running this program.
     */
    public static String getIp () {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            String ip = socket.getLocalAddress().getHostAddress();
            socket.close();
            return ip;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error Finding IP";
        }
    }
}
