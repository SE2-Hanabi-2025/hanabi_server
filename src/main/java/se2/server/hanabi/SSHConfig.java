package se2.server.hanabi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SSHConfig {

    @Bean
    public  SSHConnector sshConnector() {
        return new SSHConnector(
                "grp-14",
                "se2-demo.aau.at",
                53218,
                System.getProperty("user.home") + "/.ssh/id_rsa"
        );
    }

}
