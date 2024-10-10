
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "chat")
public class Chat implements Serializable{

    public Chat() {
    }
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
   
    @ManyToOne
   @JoinColumn(name = "from_user_id")         
   private User from_user_id;
    
     @ManyToOne
   @JoinColumn(name = "to_user_id")  
   private User to_user_id;
   
     @Column(name = "message",nullable = false)
   private String message;
      @Column(name = "date_time",nullable = false)
   private Date date_time;

     @ManyToOne
   @JoinColumn(name = "chat_status_id")  
   private Chat_Status Chat_Status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(User from_user_id) {
        this.from_user_id = from_user_id;
    }

    public User getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(User to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate_time() {
        return date_time;
    }

    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }

    public Chat_Status getChat_Status() {
        return Chat_Status;
    }

    public void setChat_Status(Chat_Status Chat_Status) {
        this.Chat_Status = Chat_Status;
    }

   

    

    

   

   
    
    
    
    
}
