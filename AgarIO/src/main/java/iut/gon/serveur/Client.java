package iut.gon.serveur;

import java.io.PrintWriter;

public class Client {

    public int ID;
    public PrintWriter printWriter;
    public boolean online;

    public Client(int ID, PrintWriter printWriter){
        this.ID = ID;
        this.printWriter = printWriter;
        this.online = true;
    }
}
