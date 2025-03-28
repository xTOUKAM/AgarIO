package iut.gon.serveur;

import iut.gon.agario.gameEngine.GameEngine;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final int ID;
    private final Socket socket;
    private final BufferedReader clientOutput;
    private final GameServer gameServer;
    private final GameEngine gameEngine;

    public ClientHandler(Socket socket, GameServer gameServer, int ID, GameEngine gameEngine) {
        this.socket = socket;
        this.gameServer = gameServer;
        this.ID = ID;
        this.gameEngine = gameEngine;
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
                        gameEngine.addPlayer(this.ID);
                        gameEngine.updateCursor(this.ID, gameEngine.players.get(ID).getX(), gameEngine.players.get(ID).getY());

                    }
                    case CLIENT_MOVEMENT -> {
                        String movementData = clientOutput.readLine();
                        //System.out.println("SERVER | Position reçue de " + ID + " : " + movementData);

                        String x = movementData.split(",")[0];
                        String y = movementData.split(",")[1];

                        double dX = Double.parseDouble(x);
                        double dY = Double.parseDouble(y);
                        System.out.println("SERVER | Position reçue de " + ID + " : X = "+dX+"  | Y = "+dY);

                        gameEngine.updateCursor(ID, dX, dY);

                        //gameServer.sendToAllClient(messageType, movementData, true);
                    }
                    case CLIENT_SPLIT -> {
                        String spaceBar = clientOutput.readLine();
                        System.out.println("SPLIT");

                        gameEngine.players.get(ID).split();

                        //gameServer.sendToAllClient(messageType, movementData, true);
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
                e.printStackTrace();
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