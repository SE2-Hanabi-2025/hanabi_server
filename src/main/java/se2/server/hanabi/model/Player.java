package se2.server.hanabi.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Player {


    public enum ConnectionStatus{
        CONNECTED,
        DISCONNECTED
    }

    private static final AtomicInteger id_generator = new AtomicInteger(1);

    private ConnectionStatus status;
    private String name;
    private final int id;
    private static int nextId = 0;

    private int avatarResID = 0;
 
    public Player(String name, int avatarResID) {
        this.name = name;
        this.id = id_generator.getAndIncrement();
        this.status = ConnectionStatus.CONNECTED;

        this.avatarResID = avatarResID;

    }

    public Player(String name) {
        this(name,0);
    }

    public int getId() {
        return id;
    }

    public ConnectionStatus getStatus(){
        return status;
    }

    public void setStatus(ConnectionStatus status){
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public int getAvatarResID() {
        return avatarResID;
    }

    public void setName(String name) {
        this.name = name;
    }


}
