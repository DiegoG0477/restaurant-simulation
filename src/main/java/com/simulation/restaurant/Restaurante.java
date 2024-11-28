package com.simulation.restaurant;

import com.simulation.restaurant.domain.*;
import com.simulation.restaurant.application.services.*;
import com.simulation.restaurant.infrastructure.DistribucionPoisson;

import java.util.*;
import java.util.concurrent.*;

public class Restaurante {
    private final int capacidad;
    private final Queue<Comensal> comensalesEnEspera;
    private final Queue<Comensal> comensalesEnMesas;
    private final List<Boolean> mesas;
    private final Queue<Orden> bufferOrdenes;
    private final Queue<Comida> bufferComidas;

    private final ComensalService comensalService;
    private final MeseroService meseroService;
    private final CocineroService cocineroService;

    private final DistribucionPoisson distribucionPoisson;

    private int comensalId;

    public Restaurante(int capacidad, int numMeseros, int numCocineros) {
        this.capacidad = capacidad;
        this.comensalesEnEspera = new LinkedBlockingQueue<>();
        this.comensalesEnMesas = new LinkedBlockingQueue<>();
        this.mesas = new ArrayList<>(Collections.nCopies(capacidad, false)); // Todas las mesas libres
        this.bufferOrdenes = new LinkedBlockingQueue<>();
        this.bufferComidas = new LinkedBlockingQueue<>();
        this.comensalId = 0;

        List<Mesero> meseros = new ArrayList<>();
        for (int i = 0; i < numMeseros; i++) {
            meseros.add(new Mesero(i + 1, bufferOrdenes));
        }

        List<Cocinero> cocineros = new ArrayList<>();
        for (int i = 0; i < numCocineros; i++) {
            cocineros.add(new Cocinero(i + 1));
        }

        this.meseroService = new MeseroService(meseros, bufferOrdenes, comensalesEnMesas);
        this.cocineroService = new CocineroService(cocineros, bufferOrdenes, bufferComidas);
        this.comensalService = new ComensalService(comensalesEnEspera, comensalesEnMesas, mesas);

        this.distribucionPoisson = new DistribucionPoisson(2.0);
    }

    public void simular() {
        System.out.println("Iniciando simulación...");
        meseroService.iniciarMeseros();
        cocineroService.iniciarCocineros();

        ScheduledExecutorService llegadaComensales = Executors.newSingleThreadScheduledExecutor();
        llegadaComensales.scheduleAtFixedRate(() -> {
            Comensal nuevoComensal = new Comensal(this.comensalId);
            this.comensalId++;
            synchronized (comensalesEnEspera) {
                comensalesEnEspera.add(nuevoComensal);
                comensalesEnEspera.notifyAll(); // Notificar a hilos en espera de nuevos comensales
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public synchronized int asignarMesa() {
        for (int i = 0; i < mesas.size(); i++) {
            if (!mesas.get(i)) { // Mesa libre
                mesas.set(i, true);
                return i;
            }
        }
        return -1; // No hay mesas disponibles
    }

    public synchronized void liberarMesa(int idMesa) {
        mesas.set(idMesa, false);
        System.out.println("Mesa " + idMesa + " fue liberada.");
        notifyAll(); // Notificar a hilos en espera de mesas
    }

    public synchronized Comensal obtenerComensalEnMesa() {
        while (comensalesEnMesas.isEmpty()) {
            try {
                wait(); // Bloquear hasta que haya un comensal en una mesa
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return comensalesEnMesas.poll();
    }

    public synchronized Orden obtenerOrden() {
        while (bufferOrdenes.isEmpty()) {
            try {
                wait(); // Bloquear hasta que haya órdenes disponibles
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return bufferOrdenes.poll();
    }

    public synchronized void agregarComida(Comida comida) {
        bufferComidas.add(comida);
        notifyAll(); // Notificar a los meseros que hay comida lista
    }
}