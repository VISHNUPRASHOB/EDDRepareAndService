/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helper;

/**
 *
 * @author vishnuprashob
 */
public class TypeValidationHelper {

    public static String emailValidator(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ("Invalid email address format");
        }
         if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty";
        }
        return null;
    }

    public static String phoneNumberValidator(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone number is required";
        }
        if (!phone.matches("^\\d{10,15}$")) {
            return "Phone must be 10-15 digits";
        }
        return null;
    }
    public static String passwordValidator(String password){
     
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty";
        }
       return null;
    }
}
