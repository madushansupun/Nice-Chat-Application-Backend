package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.User;
import entity.User_Status;
import java.io.File;
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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author User
 */
@WebServlet(name = "LoadHome", urlPatterns = {"/LoadHome"})
public class LoadHome extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        responseJson.addProperty("message", "Unable to Process your Request");

        try {

            Session session = HibernateUtil.getSessionFactory().openSession();

            // get user id from parameter
            String userId = request.getParameter("id");

            User user = (User) session.get(User.class, Integer.parseInt(userId));

            // get user status online
            User_Status user_Status = (User_Status) session.get(User_Status.class, 2);

            // update user status
            user.setUser_Status(user_Status);

            session.update(user);

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.ne("id", user.getId()));
            List<User> otheruserList = criteria1.list();

            // read other user by one
            JsonArray  JsonChatArray  = new JsonArray();
            for (User otheruser : otheruserList) {

                //get chats
                // get chat conversation
                Criteria criteria2 = session.createCriteria(Chat.class);
                criteria2.add(
                        Restrictions.or(
                                Restrictions.and(
                                        Restrictions.eq("from_user_id", user),
                                        Restrictions.eq("to_user_id", otheruser)
                                ),
                                Restrictions.and(
                                        Restrictions.eq("from_user_id", otheruser),
                                        Restrictions.eq("to_user_id", user)
                                )
                        )
                );
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);
                
                // create chat item send founded data
                JsonObject jsonchatItem =  new JsonObject();
                jsonchatItem.addProperty("other_user_Id", otheruser.getId());
                jsonchatItem.addProperty("other_user_mobile", otheruser.getMobile());
                jsonchatItem.addProperty("other_user_name", otheruser.getFirst_name()+" "+otheruser.getLast_name());
                 jsonchatItem.addProperty("other_user_status", otheruser.getUser_Status().getId());
               
                List<Chat> dbchatList = criteria2.list();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy, MM dd hh:ss a");
                
                if(dbchatList.isEmpty()){
                    // no convrsation
                 jsonchatItem.addProperty("message","Let's Start conversation");
                jsonchatItem.addProperty("dateTime",dateFormat.format(user.getRegistered_date_time()));
                jsonchatItem.addProperty("chat_status_id",1);
                
                // check Avatar image
                 String serverpath = request.getServletContext().getRealPath("");
                 String otheruserIconImagePath = serverpath+File.separator+"IconImags"+File.separator+otheruser.getMobile()+".png";
                File otheruserIconImageFile = new File(otheruserIconImagePath);
                
                if(otheruserIconImageFile.exists()){
                // icon image found
                jsonchatItem.addProperty("icon_Image_found", true);
                
                }else{
                    // icon image not found
                    jsonchatItem.addProperty("icon_Image_found", false);
                 jsonchatItem.addProperty("other_user_icon_letters", otheruser.getFirst_name().charAt(0)+""+otheruser.getLast_name().charAt(0));
                
                }
                
                }else{
                    // found conversation
                     jsonchatItem.addProperty("message",dbchatList.get(0).getMessage());
                     
                     jsonchatItem.addProperty("dateTime",dateFormat.format(dbchatList.get(0).getDate_time()));
                     jsonchatItem.addProperty("chat_status_id",dbchatList.get(0).getChat_Status().getId());
                
                
                }
                
                
                
                // get chats
               JsonChatArray.add(jsonchatItem);
              
            }

            // send users
            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "success");
            responseJson.add("JsonChatArray", gson.toJsonTree(JsonChatArray));

            session.beginTransaction().commit();
            session.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));

    }

}
