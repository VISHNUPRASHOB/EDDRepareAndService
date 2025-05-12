/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import Interface.ICsvConvertor;

/**
 *
 * @author vishnuprashob
 */
public class Customer extends User implements ICsvConvertor {

    private String phone;
    private boolean isRegistered;
    private boolean isFlagged;

    public Customer(String id, String name, String email, String phone, boolean isRegistered, boolean isFlagged) {
        super(id, name, email);
        this.phone = phone;
        this.isRegistered = isRegistered;
        this.isFlagged = isFlagged;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public String getFlagged() {
        return isFlagged ? "True" : "False";
    }

    @Override
    public String toString() {
        return super.toString() + "," + phone + "," + isRegistered + "," + isFlagged;
    }

    @Override
    public String toCSVRow() {
        return super.toString() + "," + phone + "," + isRegistered + "," + isFlagged;
    }

    public static String getCsvHeader() {
        return "customerId,name,email,phone,isRegistered,isFlagged";
    }

    public void setName(String name) {
        this.name = name;

    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFlagged(boolean flagged) {
        this.isFlagged = flagged;
    }
}
