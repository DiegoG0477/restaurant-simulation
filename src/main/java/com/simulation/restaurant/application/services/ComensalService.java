package com.simulation.restaurant.application.services;

import com.simulation.restaurant.domain.*;

import java.util.Queue;
import java.util.List;
import java.util.Random;

public class ComensalService {
    private final Queue<Comensal> comensalesEnEspera;
    private final Queue<Comensal> comensalesEnMesas;
    private final List<Boolean> mesas;
    private final EventBus eventBus;

    public ComensalService(Queue<Comensal> comensalesEnEspera, Queue<Comensal> comensalesEnMesas, List<Boolean> mesas, EventBus eventBus) {
        this.comensalesEnEspera = comensalesEnEspera;
        this.comensalesEnMesas = comensalesEnMesas;
        this.mesas = mesas;
        this.eventBus = eventBus;
    }
    /**
     * Procesa la llegada de un comensal al restaurante.
     */
    public void iniciarRecepcionista() {
        new Thread(() -> {
            while (true) {
                synchronized (comensalesEnEspera) {
                    while (comensalesEnEspera.isEmpty()) {
                        try {
                            comensalesEnEspera.wait(); // Esperar si no hay comensales en espera
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    Comensal comensal = comensalesEnEspera.poll(); // Sacar al primer comensal en espera

                    eventBus.notifyObservers("NEW_QUEUE_COMENSAL", comensal);

                    int mesaId = asignarMesa();

                    if (mesaId != -1) { // Si hay una mesa disponible
                        System.out.println("Recepcionista asignó mesa " + mesaId + " al comensal " + comensal.getId());
                        comensal.setMesaId(mesaId);

                        eventBus.notifyObservers("NEW_COMENSAL", comensal);

                        synchronized (comensalesEnMesas) {
                            comensalesEnMesas.add(comensal); // Mover al comensal a la cola de mesas
                            comensalesEnMesas.notifyAll(); // Notificar a los meseros que hay un nuevo comensal
                        }
                    } else {
                        synchronized (comensalesEnEspera) {
                            //System.out.println("No hay mesas disponibles para el comensal " + comensal.getId());
                            comensalesEnEspera.add(comensal); // Regresar al comensal a la cola de espera
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * Asigna una mesa libre a un comensal.
     * @return El ID de la mesa asignada o -1 si no hay mesas disponibles.
     */
    private synchronized int asignarMesa() {
        for (int i = 0; i < mesas.size(); i++) {
            if (!mesas.get(i)) { // Si la mesa está libre
                mesas.set(i, true); // Ocupa la mesa
                return i;
            }
        }
        return -1; // No hay mesas disponibles
    }

    /**
     * Libera una mesa ocupada.
     */
    public synchronized void liberarMesa(int idMesa) {
        mesas.set(idMesa, false); // Marca la mesa como libre
        System.out.println("Mesa " + idMesa + " fue liberada.");
        notifyAll(); // Notificar que hay una mesa disponible
    }

    /**
     * Ciclo de vida de un comensal: comer y liberar la mesa.
     */
    public void iniciarCicloDeComida(Comensal comensal) {
        new Thread(() -> {
            try {
                // Tiempo aleatorio para simular el tiempo de comida
                int tiempoComida = new Random().nextInt(5) + 3; // 3 a 7 segundos
                System.out.println("Comensal " + comensal.getId() + " está comiendo en la mesa " + comensal.getMesaId() +
                        " durante " + tiempoComida + " segundos.");
                Thread.sleep(tiempoComida * 1000L); // Simulación de tiempo de comida
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Liberar la mesa después de comer
            liberarMesa(comensal.getMesaId());
            synchronized (comensalesEnMesas) {
                eventBus.notifyObservers("EXIT_COMENSAL", comensal);
                comensalesEnMesas.remove(comensal); // El comensal deja la mesa
                comensalesEnMesas.notifyAll(); // Notificar a los meseros de que hay menos comensales en mesas
            }
            System.out.println("Comensal " + comensal.getId() + " terminó de comer y dejó la mesa " + comensal.getMesaId());
        }).start();
    }
}
