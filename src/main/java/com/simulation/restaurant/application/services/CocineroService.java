package com.simulation.restaurant.application.services;

import com.simulation.restaurant.domain.*;

import java.util.Queue;
import java.util.Random;
import java.util.List;

public class CocineroService {
    private final List<Cocinero> cocineros;
    private final Queue<Orden> bufferOrdenes;
    private final Queue<Comida> bufferComidas;
    private EventBus eventBus;

    public CocineroService(List<Cocinero> cocineros, Queue<Orden> bufferOrdenes, Queue<Comida> bufferComidas, EventBus eventBus) {
        this.cocineros = cocineros;
        this.bufferOrdenes = bufferOrdenes;
        this.bufferComidas = bufferComidas;
        this.eventBus = eventBus;
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
                       
                        if (!cocinero.isOcupado()){
                            Orden orden = bufferOrdenes.poll(); // Tomar la orden

                            Random random = new Random();
                            int tiempoCoccion = random.nextInt(3001) + 3000; // Genera un número entre 3000 y 6000

                            cocinero.setTiempoCoccion(tiempoCoccion);

                            eventBus.notifyObservers("CHEF_COOKING", cocinero);

                            cocinero.prepararOrden();

                            synchronized (bufferComidas) {
                                bufferComidas.add(new Comida(orden));
                                System.out.println("Platillo de la orden " + orden.getId() + " listo para la mesa " + orden.getIdMesa());
                                bufferComidas.notifyAll(); // Notificar que hay comida lista
                            }
                        }
                    }
                }
            }).start();
        }
    }
}
