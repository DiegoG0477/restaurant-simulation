package com.simulation.restaurant;

import com.simulation.restaurant.domain.*;
import com.simulation.restaurant.application.services.*;
import com.simulation.restaurant.infrastructure.DistribucionPoisson;

import java.util.*;
import java.util.concurrent.*;

public class Restaurante {
    private final int maximoComensales;
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

    public Restaurante(int capacidad, int numMeseros, int numCocineros, int maximoComensales) {
        this.comensalesEnEspera = new LinkedBlockingQueue<>();
        this.comensalesEnMesas = new LinkedBlockingQueue<>();
        this.mesas = new ArrayList<>(Collections.nCopies(capacidad, false)); // Todas las mesas libres
        this.bufferOrdenes = new LinkedBlockingQueue<>();
        this.bufferComidas = new LinkedBlockingQueue<>();
        this.maximoComensales = maximoComensales;
        this.comensalId = 0;

        List<Mesero> meseros = new ArrayList<>();
        for (int i = 0; i < numMeseros; i++) {
            meseros.add(new Mesero(i + 1));
        }

        List<Cocinero> cocineros = new ArrayList<>();
        for (int i = 0; i < numCocineros; i++) {
            cocineros.add(new Cocinero(i + 1));
        }

        this.comensalService = new ComensalService(comensalesEnEspera, comensalesEnMesas, mesas);
        this.cocineroService = new CocineroService(cocineros, bufferOrdenes, bufferComidas);
        this.meseroService = new MeseroService(meseros, bufferOrdenes, comensalesEnMesas, bufferComidas, comensalService);

        this.distribucionPoisson = new DistribucionPoisson(2.0);
    }

    public void simular() {
        System.out.println("Iniciando simulación...");
        meseroService.iniciarMeseros(); // Inicia los meseros
        cocineroService.iniciarCocineros(); // Inicia los cocineros
        comensalService.iniciarRecepcionista();
    
        // Hilo para generar comensales
        new Thread(() -> {
            while (comensalId < maximoComensales) { // Generar hasta n comensales
                int intervalo = distribucionPoisson.generar(); // Genera el intervalo en segundos
                Comensal nuevoComensal = new Comensal(this.comensalId);
                this.comensalId++;
    
                // Agregar comensal a la cola
                synchronized (comensalesEnEspera) {
                    comensalesEnEspera.add(nuevoComensal);
                    System.out.println("Nuevo comensal " + nuevoComensal.getId() + " llegando, " + (comensalesEnEspera.size() - 1) + " en espera");
                    comensalesEnEspera.notifyAll(); // Notifica a los hilos en espera
                }
    
                // Simular el intervalo entre llegadas
                try {
                    Thread.sleep(intervalo * 1000L); // Pausa basada en el intervalo generado
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
    
            System.out.println("Generación de comensales finalizada. Total: " + comensalId);
        }).start();
    }
}