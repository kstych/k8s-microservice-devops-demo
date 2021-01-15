package orp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;

//qute template
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import javax.inject.Inject;
import javax.ws.rs.PathParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//keyclock
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;


@Path("/")
//@Authenticated
public class main {

		@Inject
		Template index;

		@Inject
		Template home;


		@Inject
		SecurityIdentity securityIdentity;

    @GET
    @Path("home")
    @Authenticated
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getHome(){

        return home.data("username",securityIdentity.getPrincipal().getName());
    }

    @GET
    @Path("dbgetall")
    @Produces(MediaType.TEXT_PLAIN)
    public String GetData(){
       StringBuilder output = new StringBuilder();
      try{
      // curl http://db-service/msg/set/message
      // curl http://db-service/msg/getall

        //Run a bat file
        Process process = Runtime.getRuntime().exec(
        "curl http://db-service/msg/getall");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }
      }catch (IOException e) {
            e.printStackTrace();
        }
      return output.toString();
    }


    @GET
    @Path("dbset/{message}")
    @Produces(MediaType.TEXT_PLAIN)
    public String GetData(@PathParam("message") String message){
       StringBuilder output = new StringBuilder();
      try{
      // curl http://db-service/msg/set/message
      // curl http://db-service/msg/getall

        //Run a bat file
        Process process = Runtime.getRuntime().exec(
        "curl http://db-service/msg/set/"+message);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }
      }catch (IOException e) {
            e.printStackTrace();
        }
      return output.toString();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance login(){
    	return index.instance();
    }

}
