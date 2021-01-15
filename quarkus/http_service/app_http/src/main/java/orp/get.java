package orp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;

// //qute
// import io.quarkus.qute.TemplateInstance;
// import io.quarkus.qute.Template;
// import io.quarkus.qute.api.ResourcePath;
// import javax.inject.Inject;
// import javax.ws.rs.GET;

//keyclock
//import io.quarkus.security.Authenticated;

//web
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

@Path("/get")
//@Authenticated
public class get{

		public String getContent(String query,String service_url)  throws IOException {
			 //Instantiating the URL class
      //URL url = new URL("http://www.google.com/search?q="+query);
      //URL url = new URL("http://0.0.0.0:8080/log?search="+query);
      //URL url = new URL("http://0.0.0.0:8080/query?search="+query);
        URL url = new URL(service_url+query);//

      //Retrieving the contents of the specified page
      Scanner sc = new Scanner(url.openStream());
      //Instantiating the StringBuffer class to hold the result
      StringBuffer sb = new StringBuffer();
      while(sc.hasNext()) {
         sb.append(sc.next());
         //System.out.println(sc.next());
      }
      //Retrieving the String from the String Buffer object
      String result = sb.toString();
      return result;


      //System.out.println("Contents of the web page: "+result);

		}

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getQuery(@QueryParam("search") String query,@QueryParam("url") String service_url) throws IOException{
       return getContent(query,service_url);
       //return "<iframe  src='https://"+query+"' class='col-12' height='900px' title='Iframe Example'></iframe>";
       //return query;
    }


}

