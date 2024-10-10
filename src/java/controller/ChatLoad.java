package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.Chat_Status;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @Nice Chat Load
 */
@WebServlet(name = "ChatLoad", urlPatterns = {"/ChatLoad"})
public class ChatLoad extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        String log_user_id = request.getParameter("log_user_id");
        String other_user_Id = request.getParameter("other_user_Id");

        // get User
        User log_user = (User) session.get(User.class, Integer.parseInt(log_user_id));

        // get other user
        User other_user = (User) session.get(User.class, Integer.parseInt(other_user_Id));

        Criteria criteria1 = session.createCriteria(Chat.class);
        criteria1.add(Restrictions.or(
                Restrictions.and(Restrictions.eq("from_user_id", log_user), Restrictions.eq("to_user_id", other_user)),
                Restrictions.and(Restrictions.eq("from_user_id", other_user), Restrictions.eq("to_user_id", log_user))
        )
        );

        // sort chat
        criteria1.addOrder(Order.asc("date_time"));

        // get chat list
        List<Chat> chat_list = criteria1.list();

        // get chat status 1= seen
        Chat_Status chat_Status = (Chat_Status) session.get(Chat_Status.class, 1);

        // chat Array
        JsonArray chatArray = new JsonArray();
        
        // date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM,dd hh:mm a");
        
        // get chat only from other user
        for (Chat chat : chat_list) {
            
          
              // create chat Object
            JsonObject chatObject = new JsonObject();
            chatObject.addProperty("message", chat.getMessage());
            chatObject.addProperty("datetime", dateFormat.format(chat.getDate_time()));
            
            
            
            if (chat.getFrom_user_id().getId() == other_user.getId()) {
                
                // add side to chat object
                chatObject.addProperty("side", "left");

                // get only updates chat_status_id = 2 
                if (chat.getChat_Status().getId() == 2) {

                    // update chat status-> seen
                    chat.setChat_Status(chat_Status);
                    session.save(chat);
                }

            }else{
                // get chat from log user
                 chatObject.addProperty("side", "right");
                 chatObject.addProperty("status", chat.getChat_Status().getId()); // 1 = seen , 2= unseen
            
            }
            // add chat object into chat Array
            chatArray.add(chatObject);
        }
        // update db
        session.beginTransaction().commit();
        
        // send response
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(chatArray));
        

    }

}
