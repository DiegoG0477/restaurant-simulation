package com.simulation.restaurant.application.services;

import com.simulation.restaurant.domain.*;

import java.util.List;
import java.util.Queue;

public class MeseroService {
    private final List<Mesero> meseros;
    private final Queue<Orden> bufferOrdenes;
    private final Queue<Comida> bufferComidas;
    private final Queue<Comensal> comensalesEnMesas;
    private final ComensalService comensalService;

    public MeseroService(List<Mesero> meseros, Queue<Orden> bufferOrdenes, Queue<Comensal> comensalesEnMesas, Queue<Comida> bufferComidas, ComensalService comensalService) {
        this.meseros = meseros;
        this.bufferOrdenes = bufferOrdenes;
        this.bufferComidas = bufferComidas;
        this.comensalesEnMesas = comensalesEnMesas;
        this.comensalService = comensalService;
    }

    public void iniciarMeseros() {
        for (Mesero mesero : meseros) {
            new Thread(() -> {
                while (true) {
                    atenderComensal(mesero);
                    esperarComidaYServir(mesero);
                }
            }).start();
        }
    }
    
    private void atenderComensal(Mesero mesero) {
        synchronized (comensalesEnMesas) {
            while (comensalesEnMesas.isEmpty() || mesero.isOcupado()) {
                try {
                    comensalesEnMesas.wait(); // Esperar si no hay comensales o si el mesero está ocupado
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
    
            // Solo atender si no está ocupado
            if (!mesero.isOcupado()) {
                Comensal comensal = comensalesEnMesas.poll(); // Obtener el próximo comensal
                System.out.println("Mesero " + mesero.getId() + " atendiendo al comensal " + comensal.getId() + " (1 seg)");
                mesero.atenderComensal(comensal);
                Orden orden = mesero.generarOrden(comensal.getMesaId());
    
                agregarOrdenAlBuffer(orden);
            }
        }
    }
    
    private void agregarOrdenAlBuffer(Orden orden) {
        synchronized (bufferOrdenes) {
            bufferOrdenes.add(orden);
            System.out.println("Nueva orden para el cocinero: Orden " + orden.getId());
            bufferOrdenes.notifyAll(); // Notificar que hay una nueva orden
        }
    }
    
    private void esperarComidaYServir(Mesero mesero) {
        while (mesero.isOcupado()) { // Mientras el mesero esté ocupado con un comensal
            synchronized (bufferComidas) {
                while (bufferComidas.isEmpty()) {
                    try {
                        bufferComidas.wait(); // Esperar si no hay comida lista
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
    
                // Verificar si hay comida lista para el comensal actual
                Comida comidaLista = obtenerComidaLista(mesero);
    
                if (comidaLista != null) {
                    bufferComidas.remove(comidaLista); // Eliminar la comida del buffer
                    servirComidaAlComensal(mesero, comidaLista);
                }
            }
        }
    }
    
    private Comida obtenerComidaLista(Mesero mesero) {
        for (Comida comida : bufferComidas) {
            if (comida.getOrden().getId() == mesero.getComensalActual().getOrden().getId()) {
                return comida;
            }
        }
        return null;
    }
    
    private void servirComidaAlComensal(Mesero mesero, Comida comidaLista) {
        Comensal comensal = mesero.getComensalActual();
        System.out.println("!!! Mesero " + mesero.getId() + " sirvió la comida al comensal " + comensal.getId());
    
        mesero.servirComida(); // Marcar al mesero como desocupado
        comensalService.iniciarCicloDeComida(comensal);
    
        synchronized (comensalesEnMesas) {
            comensalesEnMesas.notifyAll(); // Notificar que el mesero está libre
        }
    }    
}
