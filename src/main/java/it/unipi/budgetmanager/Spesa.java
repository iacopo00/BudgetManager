/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.budgetmanager;

import java.io.Serializable;
import java.sql.Date;


/**
 *
 * @author iacopomassei
 */
public class Spesa implements Serializable {

    public int id;
    public String causale;
    public Date data;
    public float costo;
    public String username;

    public Spesa() {
    }

    public Spesa(int id, String causale, Date data, float costo, String username) {
        this.id = id;
        this.causale = causale;
        this.data = data;
        this.costo = costo;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getCausale() {
        return causale;
    }

    public Date getData() {
        return data;
    }

    public float getCosto() {
        return costo;
    }

    public String getUsername() {
        return username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCausale(String causale) {
        this.causale = causale;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
