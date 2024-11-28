package com.simulation.restaurant.domain;

public class Cocinero {
    private final int id;
    private boolean ocupado;
    private int tiempoCoccion;

    public Cocinero(int id) {
        this.id = id;
        this.ocupado = false;
    }

    public int getTiempoCoccion(){
        return tiempoCoccion;
    }

    public void setTiempoCoccion(int tiempoCoccion){
        this.tiempoCoccion = tiempoCoccion;
    }

    public int getId() {
        return id;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void prepararOrden() {
        ocupado = true;
        System.out.println("Cocinero " + id + " cocinando por " + (tiempoCoccion / 1000) + " segundos");
        try {
            Thread.sleep(tiempoCoccion);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt(); // Propagate the interrupt
        }
        ocupado = false;
        tiempoCoccion = 0;
    }
    @Override
    public String toString() {
        return "Cocinero{id=" + id + ", ocupado=" + ocupado + "}";
    }
}
