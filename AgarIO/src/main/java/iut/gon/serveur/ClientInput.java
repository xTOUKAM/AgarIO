package iut.gon.serveur;

import java.io.PrintWriter;

public class ClientInput {

    public int ID;
    public PrintWriter printWriter;
    public boolean online;

    public ClientInput(int ID, PrintWriter printWriter){
        this.ID = ID;
        this.printWriter = printWriter;
        this.online = false;
    }
}
