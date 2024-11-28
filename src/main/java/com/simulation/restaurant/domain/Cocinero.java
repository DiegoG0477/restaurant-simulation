package com.simulation.restaurant.domain;

public class Cocinero {
    private final int id;
    private boolean ocupado;

    public Cocinero(int id) {
        this.id = id;
        this.ocupado = false;
    }

    public int getId() {
        return id;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void prepararOrden() {

        ocupado = true;
        //Orden orden = bufferOrdenes.poll();
        // Simulación de la preparación
        try {
            Thread.sleep(1000); // Simulación del tiempo de cocción
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        //bufferComidas.add(new Comida(orden));
        ocupado = false;
    }

    @Override
    public String toString() {
        return "Cocinero{id=" + id + ", ocupado=" + ocupado + "}";
    }
}
