package com.simulation.restaurant;

public class App {
    public static void main(String[] args) {
        Restaurante restaurante = new Restaurante(20, 2, 2, 100); // Capacidad, meseros, cocineros, cantidad de comensales a generar
        restaurante.simular();
    }
}