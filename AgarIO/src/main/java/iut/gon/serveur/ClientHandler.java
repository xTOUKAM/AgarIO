package iut.gon.serveur;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final int ID;
    private final Socket socket;
    private final BufferedReader clientOutput;
    private final GameServer gameServer;

    public ClientHandler(Socket socket, GameServer gameServer, int ID) {
        this.socket = socket;
        this.gameServer = gameServer;
        this.ID = ID;
        try {
            clientOutput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        boolean keepListening = true;
        while (keepListening) {
            try {
                // Lire le message du client
                String messageTypeLine = clientOutput.readLine();
                MessageType messageType = MessageType.values()[Integer.parseInt(messageTypeLine)];

                switch (messageType) {
                    case CLIENT_STATUS -> {
                        this.gameServer.updateClientStatus(this.ID, Boolean.parseBoolean(clientOutput.readLine()));
                    }
                    case CLIENT_MOVEMENT -> {
                        String movementData = clientOutput.readLine();
                        // Envoyer les données de mouvement au serveur pour gestion
                        gameServer.sendToAllClient(messageType, movementData, true);
                    }
                    case CLIENT_CHAT_MESSAGE -> {
                        String chatMessage = clientOutput.readLine();
                        // Gérer le chat ici
                    }
                    default -> {
                        keepListening = false;
                        System.out.println("SERVER | Connection to client " + this.ID + " stopped unexpectedly");
                        socket.close();
                    }
                }
            } catch (Exception e) {
                keepListening = false;
                System.out.println("SERVER | Connection to client " + this.ID + " stopped unexpectedly");
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}

