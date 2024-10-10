
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.Chat_Status;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @send chat Nice chat
 */

@WebServlet(name = "ChatSend", urlPatterns = {"/ChatSend"})

public class ChatSend extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // /ChatSend?log_user_id=1&other_user_Id=2&message="Hi"
        
        
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        
        
         Session session = HibernateUtil.getSessionFactory().openSession();
        
        
          String log_user_id = request.getParameter("log_user_id");
        String other_user_Id = request.getParameter("other_user_Id");
        String message = request.getParameter("message");
        
         // get User
        User log_user = (User) session.get(User.class, Integer.parseInt(log_user_id));

        // get other user
        User other_user = (User) session.get(User.class, Integer.parseInt(other_user_Id));
        
        
        Chat chat = new Chat();
        
        // get chat status 2 = unseen
        Chat_Status chat_Status = (Chat_Status) session.get(Chat_Status.class, 2);
        
        chat.setChat_Status(chat_Status);
        chat.setDate_time(new Date());
        chat.setFrom_user_id(log_user);
        chat.setTo_user_id(other_user);
        chat.setMessage(message);
        
        // save in db
        session.save(chat);
        try {
            session.beginTransaction().commit();
        responseJson.addProperty("success", true);
        
        } catch (Exception e) {
        }
        
        
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
        
        
    }

    
}
