/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
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
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
         responseJson.addProperty("success", false);


        //JsonObject requestJson =  gson.fromJson(request.getReader(), JsonObject.class);
        String mobile = request.getParameter("mobile");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String password = request.getParameter("password");
        Part iconImage = request.getPart("iconImage");

        if (mobile.isEmpty()) {
            // Mobile Number Empty
            responseJson.addProperty("message", "Please Enter Your Mobile Number");

        } else if (!Validations.isMobileValid(mobile)) {
            // Mobile Number Not Valid
            responseJson.addProperty("message", "Invalid Mobile Number");

        } else if (firstname.isEmpty()) {
            //First Name Empty
            responseJson.addProperty("message", "Please Enter Your First Name");

        } else if (lastname.isEmpty()) {
            // Last Name Empty
            responseJson.addProperty("message", "Please Enter Your Last Name");

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

            if (!criteria1.list().isEmpty()) {
                // mobile nummber Already used
             responseJson.addProperty("message", "Mobile Number Already Used.");
            } else {
              // mobile number not used
                User user = new User();
                user.setFirst_name(firstname);
                user.setLast_name(lastname);
                user.setMobile(mobile);
                user.setPassword(password);
                user.setRegistered_date_time(new Date());

                //get user status offline
                User_Status user_Status = (User_Status) session.get(User_Status.class, 2);
                user.setUser_Status(user_Status);

                session.save(user);
                session.beginTransaction().commit();

                // Check Uploaded Image
                if (iconImage != null) {
                    // image Selected
                    String serverpath = request.getServletContext().getRealPath("");
                    String iconImagepath = serverpath + File.separator + "IconImags" + File.separator + mobile + ".png";
                    System.out.println(iconImagepath);
                    File file = new File(iconImagepath);
                    Files.copy(iconImage.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                }
                responseJson.addProperty("success", true);
                responseJson.addProperty("message", "Registration Complete");

            }

            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));

    }

}
