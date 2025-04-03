package se2.server.hanabi;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Autowired;

public class SSHConnector {
    private String username;
    private  String hostname;
    private int port;
    private String privateKey;

    //für tests
    private final JSch jsch;

    public SSHConnector(String username, String hostname, int port, String privateKey) {
        this(username, hostname, port, privateKey, new JSch());
    }


    //auch nur für tests
    public SSHConnector(String username, String hostname, int port, String privateKey, JSch jsch) {
        this.username = username;
        this.hostname = hostname;
        this.port = port;
        this.privateKey = privateKey;
        this.jsch = jsch;
    }

    public Session connect() throws Exception {
        jsch.addIdentity(privateKey);
        Session session = jsch.getSession(username, hostname, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        return session;
    }
}
