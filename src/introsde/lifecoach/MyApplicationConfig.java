package introsde.lifecoach;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("sdelab")
public class MyApplicationConfig extends ResourceConfig {
    public MyApplicationConfig () {
        packages("introsde.lifecoach");
    }
}
