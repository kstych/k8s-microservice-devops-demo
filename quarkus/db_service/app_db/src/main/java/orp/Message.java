package orp;
import java.util.List;
import javax.persistence.Entity;
import java.util.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Message extends PanacheEntity {
    public String message;

    public Message(){}
    public Message(String message){
        this.message = message;

    }
    public static List<Message> getAll(){
      return listAll();
    }
}
