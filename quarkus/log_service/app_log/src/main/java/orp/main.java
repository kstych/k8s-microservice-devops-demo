package orp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class main {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String hello() {
        return "Hello Log Service";
    }
}