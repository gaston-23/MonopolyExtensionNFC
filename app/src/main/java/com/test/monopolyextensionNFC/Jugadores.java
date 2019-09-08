package com.test.monopolyextensionNFC;

import android.media.Image;

public class Jugadores {
    private long dinero,id;
    private String nombreJuga;
    private int perfil;


    public Jugadores(){
        this.id = 0;
        this.dinero =1500;
        this.nombreJuga="anon";
    }
    public Jugadores (long id,String nom, long dinero,int perfil){
        this.id = id;
        this.dinero =dinero;
        this.nombreJuga=nom;
        this.perfil = perfil;
    }

    public String getNombreJuga() {
        return nombreJuga;
    }

    public void setNombreJuga(String nombreJuga) {
        this.nombreJuga = nombreJuga;
    }

    public long getId() {
        return id;
    }

    public int getPerfil() {
        return perfil;
    }

    public void setPerfil(int perfil) {
        this.perfil = perfil;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDinero() {
        return dinero;
    }

    public void setDinero(long dinero) {
        this.dinero = dinero;
    }

    public void resta(long dinero){
        this.dinero-=dinero;
    }
    public void suma(long dinero){
        this.dinero+=dinero;
    }
    @Override
    public String toString() {
        return "Lead{" +
                "ID='" + id + '\'' +
                ", Nombre='" + nombreJuga + '\'' +
                ", Dinero='" + dinero + '\'' +
                '}';
    }
}
