package com.simulation.restaurant.application.services;

import com.simulation.restaurant.domain.*;

import java.util.Queue;
import java.util.List;

public class CocineroService {
    private final List<Cocinero> cocineros;
    private final Queue<Orden> bufferOrdenes;
    private final Queue<Comida> bufferComidas;

    public CocineroService(List<Cocinero> cocineros, Queue<Orden> bufferOrdenes, Queue<Comida> bufferComidas) {
        this.cocineros = cocineros;
        this.bufferOrdenes = bufferOrdenes;
        this.bufferComidas = bufferComidas;
    }

    public void iniciarCocineros() {
        for (Cocinero cocinero : cocineros) {
            new Thread(() -> {
                while (true) {
                    synchronized (bufferOrdenes) {
                        while (bufferOrdenes.isEmpty()) {
                            try {
                                bufferOrdenes.wait(); // Esperar si no hay órdenes disponibles
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        Orden orden = bufferOrdenes.poll(); // Tomar la orden
                        cocinero.prepararOrden();
                        synchronized (bufferComidas) {
                            bufferComidas.add(new Comida(orden));
                            bufferComidas.notifyAll(); // Notificar que hay comida lista
                        }
                    }
                }
            }).start();
        }
    }
}
