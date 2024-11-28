package com.simulation.restaurant.domain;

public class Comida {
    private int id;
    private Orden orden;

    public Comida(Orden orden){
        this.orden = orden;
    }
}