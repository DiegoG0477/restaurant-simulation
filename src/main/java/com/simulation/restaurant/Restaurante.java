package com.simulation.restaurant;

import com.simulation.restaurant.domain.*;
import com.simulation.restaurant.infrastructure.DistribucionPoisson;

import java.util.*;
import java.util.concurrent.*;

public class Restaurante {
    private final int capacidad;
    private final Queue<Comensal> comensalesEnEspera;
    private final List<Mesero> meseros;
    private final List<Cocinero> cocineros;
    private final Queue<Orden> bufferOrdenes;
    private final Queue<Comida> bufferComidas;

    private final ExecutorService ejecutorMeseros;
    private final ExecutorService ejecutorCocineros;

    private final DistribucionPoisson distribucionPoisson;

    public Restaurante(int capacidad, int numMeseros, int numCocineros) {
        this.capacidad = capacidad;
        this.comensalesEnEspera = new LinkedBlockingQueue<>();
        this.meseros = new ArrayList<>();
        this.cocineros = new ArrayList<>();
        this.bufferOrdenes = new LinkedBlockingQueue<>();
        this.bufferComidas = new LinkedBlockingQueue<>();
        this.ejecutorMeseros = Executors.newFixedThreadPool(numMeseros);
        this.ejecutorCocineros = Executors.newFixedThreadPool(numCocineros);

        this.distribucionPoisson = new DistribucionPoisson(2.0); // Lambda = 2.0 para llegadas promedio

        // Inicializar meseros
        for (int i = 0; i < numMeseros; i++) {
            meseros.add(new Mesero(i + 1, bufferOrdenes));
        }

        // Inicializar cocineros
        for (int i = 0; i < numCocineros; i++) {
            cocineros.add(new Cocinero(i + 1, bufferOrdenes, bufferComidas));
        }
    }

    /**
     * Agregar un comensal al restaurante.
     */
    public synchronized void agregarComensal(Comensal comensal) {
        if (comensalesEnEspera.size() < capacidad) {
            System.out.println("Comensal " + comensal.getId() + " ingresó al restaurante.");
            comensalesEnEspera.add(comensal);
        } else {
            System.out.println("Comensal " + comensal.getId() + " espera afuera. Restaurante lleno.");
        }
    }

    /**
     * Simular la operación del restaurante.
     */
    public void simular() {
        System.out.println("Iniciando simulación del restaurante...");

        // Iniciar hilos de meseros
        for (Mesero mesero : meseros) {
            ejecutorMeseros.execute(() -> {
                while (true) {
                    Comensal comensal = obtenerComensal();
                    if (comensal != null) {
                        mesero.atenderComensal(comensal);
                        mesero.generarOrden();
                        System.out.println("Mesero " + mesero.getId() + " atendió al comensal " + comensal.getId());
                    } else {
                        descansar("Mesero", mesero.getId());
                    }
                }
            });
        }

        // Iniciar hilos de cocineros
        for (Cocinero cocinero : cocineros) {
            ejecutorCocineros.execute(() -> {
                while (true) {
                    cocinero.prepararOrden();
                }
            });
        }

        // Simulación de llegada de comensales
        ScheduledExecutorService llegadaComensales = Executors.newSingleThreadScheduledExecutor();
        llegadaComensales.scheduleAtFixedRate(() -> {
            int intervalo = distribucionPoisson.generar();
            Comensal nuevoComensal = new Comensal(new Random().nextInt(1000));
            agregarComensal(nuevoComensal);

            try {
                Thread.sleep(intervalo * 1000L); // Pausar según la distribución de Poisson
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, 0, 1, TimeUnit.SECONDS); // Cada segundo se evalúan llegadas nuevas.
    }

    /**
     * Obtener el próximo comensal en espera.
     */
    private synchronized Comensal obtenerComensal() {
        return comensalesEnEspera.poll();
    }

    /**
     * Mostrar mensaje de descanso.
     */
    private void descansar(String rol, int id) {
        System.out.println(rol + " " + id + " está descansando...");
        try {
            Thread.sleep(500); // Pausa breve
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
