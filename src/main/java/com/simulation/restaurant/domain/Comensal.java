package com.simulation.restaurant.domain;

public class Comensal {
    private final int id;
    private boolean atendido;
    private Orden orden;
    private int mesaId;

    public Comensal(int id) {
        this.id = id;
        this.atendido = false;
    }

    public int getId() {
        return id;
    }

    public int getMesaId(){
        return mesaId;
    }

    public void setMesaId(int mesaId){
        this.mesaId = mesaId;
    }

    public boolean isAtendido() {
        return atendido;
    }

    public void setAtendido(boolean atendido) {
        this.atendido = atendido;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    @Override
    public String toString() {
        return "Comensal{id=" + id + ", atendido=" + atendido + "}";
    }
}
