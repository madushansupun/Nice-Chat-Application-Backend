
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import entity.User_Status;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * Nice Chat
 */
@MultipartConfig
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        JsonObject requestJson =  gson.fromJson(request.getReader(), JsonObject.class);
        String mobile = requestJson.get("mobile").getAsString();
        String password = requestJson.get("password").getAsString();
        

        if (mobile.isEmpty()) {
            // Mobile Number Empty
            responseJson.addProperty("message", "Please Enter Your Mobile Number");

        } else if (!Validations.isMobileValid(mobile)) {
            // Mobile Number Not Valid
            responseJson.addProperty("message", "Invalid Mobile Number");

        } else if (password.isEmpty()) {
            // Passwords Empty
            responseJson.addProperty("message", "Please Enter Your Password");

        } else if (!Validations.isPasswordValid(password)) {
            // Invalid Password
            responseJson.addProperty("message", "Password Must Include at least Uppercase Letter,Special Character and be at least eight Character long. ");

        } else {
            // data validation

            Session session = HibernateUtil.getSessionFactory().openSession();

            // session mobile number
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("mobile", mobile));
            criteria1.add(Restrictions.eq("password", password));

            if (!criteria1.list().isEmpty()) {
                // User Found
             User user = (User) criteria1.uniqueResult();
           
              responseJson.addProperty("success", true);
             responseJson.addProperty("message", "SignIn Success.");
             
             responseJson.add("user", gson.toJsonTree(user));
             
             
            } else {
              // User not Found
              
                responseJson.addProperty("message", "Invalid Credentials!");

            }

            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));

    }

}
