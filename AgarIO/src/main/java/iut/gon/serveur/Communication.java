package iut.gon.serveur;

import java.io.PrintWriter;

public class Communication {

    public static void send(PrintWriter stream, MessageType messageType, String data){
        stream.write(messageType.ordinal()+"\n");
        stream.write(data +"\n");
        stream.flush();
    }

}
