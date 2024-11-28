package com.simulation.restaurant.application.services;

import com.simulation.restaurant.domain.*;

import java.util.List;
import java.util.Queue;

public class MeseroService {
    private final List<Mesero> meseros;
    private final Queue<Orden> bufferOrdenes;
    private final Queue<Comensal> comensalesEnMesas;

    public MeseroService(List<Mesero> meseros, Queue<Orden> bufferOrdenes, Queue<Comensal> comensalesEnMesas) {
        this.meseros = meseros;
        this.bufferOrdenes = bufferOrdenes;
        this.comensalesEnMesas = comensalesEnMesas;
    }

    public void iniciarMeseros() {
        for (Mesero mesero : meseros) {
            new Thread(() -> {
                while (true) {
                    synchronized (comensalesEnMesas) {
                        while (comensalesEnMesas.isEmpty()) {
                            try {
                                comensalesEnMesas.wait(); // Esperar si no hay comensales en mesas
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        Comensal comensal = comensalesEnMesas.poll(); // Obtener el próximo comensal
                        Orden orden = mesero.generarOrden(comensal.getMesaId());
                        synchronized (bufferOrdenes) {
                            bufferOrdenes.add(orden);
                            bufferOrdenes.notifyAll(); // Notificar que hay una nueva orden
                        }
                    }
                }
            }).start();
        }
    }
}
