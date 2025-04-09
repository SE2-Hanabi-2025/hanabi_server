package se2.server.hanabi;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SSHConnectorTest {


    // rein für verify()
    @Test
    public void testConnect_MocksEverything() throws Exception {
        // Arrange
        JSch mockJSch = mock(JSch.class);
        Session mockSession = mock(Session.class);

        when(mockJSch.getSession(anyString(), anyString(), anyInt()))
                .thenReturn(mockSession);

        SSHConnector connector = new SSHConnector("testuser", "localhost", 22, "dummykey", mockJSch);

        // Act
        Session session = connector.connect();

        // Assert
        verify(mockJSch).addIdentity("dummykey");
        verify(mockSession).setConfig("StrictHostKeyChecking", "no");
        verify(mockSession).connect();
    }

    // Test ohne JSch
    @Test
    public void testDefaultConstructorCoverage() {
        SSHConnector connector = new SSHConnector("u", "h", 22, "key");
        assertNotNull(connector); // keine echte Verbindung
    }


}
