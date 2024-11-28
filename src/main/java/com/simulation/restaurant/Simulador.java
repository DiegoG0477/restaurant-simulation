package com.simulation.restaurant;

public class Simulador {
    public static void main(String[] args) {
        Restaurante restaurante = new Restaurante(10, 2, 3); // Capacidad, meseros, cocineros
        restaurante.simular();
    }
}
