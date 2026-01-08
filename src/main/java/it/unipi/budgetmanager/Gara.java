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
public class Gara implements Serializable {

    public int n_gara;
    public Date data;
    public String campionato;
    public String luogo;
    public float rimborso;
    public String username;

    public Gara(int n_gara, Date data, String campionato, String luogo, float rimborso, String username) {
        this.n_gara = n_gara;
        this.data = data;
        this.campionato = campionato;
        this.luogo = luogo;
        this.rimborso = rimborso;
        this.username = username;
    }

    public Gara() {

    }

    public int getN_gara() {
        return n_gara;
    }

    public Date getData() {
        return data;
    }

    public String getCampionato() {
        return campionato;
    }

    public String getLuogo() {
        return luogo;
    }

    public float getRimborso() {
        return rimborso;
    }

    public String getUsername() {
        return username;
    }

    public void setN_gara(int n_gara) {
        this.n_gara = n_gara;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setCampionato(String campionato) {
        this.campionato = campionato;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public void setRimborso(float rimborso) {
        this.rimborso = rimborso;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
