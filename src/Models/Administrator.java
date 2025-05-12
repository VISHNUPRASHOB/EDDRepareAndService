package Models;

import Interface.ICsvConvertor;

public class Administrator extends User implements ICsvConvertor {

    private String password;

    public Administrator(String id, String name, String email, String password) {
        super(id, name, email);
        this.password = password;
    }

    // Add getter for password
    public String getPassword() {
        return this.password;
    }

    @Override
    public String toString() {
        return super.toString(); // No extra fields
    }

    @Override
    public String toCSVRow() {
        return id + "," + name + "," + email + "," + password;
    }

    public static String getCsvHeader() {
        return "id,name,email,password";
    }
}
