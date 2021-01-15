package orp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//web
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.*;


// http://localhost:8081/msg/getall [GET]
// http://localhost:8081/msg/set/message [GET]

@Path("/msg")
public class query{

    @GET
    @Transactional
    @Path("set/{message}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessage(@PathParam("message") String message){
        Message msg = new Message(message);
        if (msg != null){

            msg.persist();
            return Response.ok(msg).build();

        }
        return Response.noContent().build();
    }

    @GET
    @Transactional
    @Path("getall")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMessage(){
        List<Message>  msg = Message.getAll();
        return Response.ok(msg).build();
        }

}





