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
public class SparePart implements ICsvConvertor {

    private String partId;
    private String name;
    private double cost;
    private String location;

    public SparePart(String partId, String name, double cost, String location) {
        this.partId = partId;
        this.name = name;
        this.cost = cost;
        this.location = location;
    }

    public String getPartId() {
        return partId;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return partId + "," + name + "," + cost + "," + location;
    }

    @Override
    public String toCSVRow() {
        return toString();
    }
}
