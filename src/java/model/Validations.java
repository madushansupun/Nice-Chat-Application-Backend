
package model;

public class Validations {
    
    public static boolean isMobileValid(String mobile){
      return mobile.matches("^07[01245678]{1}[0-9]{7}$");
    }
    
    
    public static boolean  isPasswordValid(String password){
      
       return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()])[a-zA-Z0-9!@#&()]{8,20}$");
    
    }
    
    
   
    
}
