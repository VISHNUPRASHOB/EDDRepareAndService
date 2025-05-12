/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import Interface.ICsvConvertor;
import java.util.Objects;

/**
 *
 * @author vishnuprashob
 */
public class Technician extends User implements ICsvConvertor {

    private String expertise;
    private String password;

    public Technician(String id, String name, String email, String expertise, String password) {
        super(id, name, email);
        this.expertise = expertise;
        this.password = password;
    }

    public String getExpertise() {
        return expertise;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return super.toString() + "," + expertise;
    }

    @Override
    public String toCSVRow() {
        return toString();
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;
//        Technician that = (Technician) o;
//        return Objects.equals(expertise, that.expertise);
//    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expertise);
    }
}
