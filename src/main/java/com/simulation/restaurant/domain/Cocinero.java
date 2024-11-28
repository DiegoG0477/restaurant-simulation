package com.simulation.restaurant.domain;

import java.util.Random;

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
        Random random = new Random();
        int tiempoCoccion = random.nextInt(3001) + 3000; // Genera un número entre 3000 y 6000
        System.out.println("Cocinero " + id + " cocinando por " + (tiempoCoccion / 1000) + " segundos");
        try {
            Thread.sleep(tiempoCoccion);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt(); // Propagate the interrupt
        }
        ocupado = false;
    }
    @Override
    public String toString() {
        return "Cocinero{id=" + id + ", ocupado=" + ocupado + "}";
    }
}
