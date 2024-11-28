package com.simulation.restaurant.application.services;

import com.simulation.restaurant.domain.*;

import java.util.Queue;
import java.util.List;
import java.util.Random;

public class ComensalService {
    private final Queue<Comensal> comensalesEnEspera;
    private final Queue<Comensal> comensalesEnMesas;
    private final List<Boolean> mesas;

    public ComensalService(Queue<Comensal> comensalesEnEspera, Queue<Comensal> comensalesEnMesas, List<Boolean> mesas) {
        this.comensalesEnEspera = comensalesEnEspera;
        this.comensalesEnMesas = comensalesEnMesas;
        this.mesas = mesas;
    }

    /**
     * Procesa la llegada de un comensal al restaurante.
     */
    public void procesarLlegadaComensales(Comensal comensal) {
        new Thread(() -> {
            synchronized (mesas) {
                int idMesa = asignarMesa();
                if (idMesa != -1) {
                    System.out.println("Recepcionista asignó mesa " + idMesa + " al comensal " + comensal.getId());
                    comensal.setMesaId(idMesa);
                    synchronized (comensalesEnMesas) {
                        comensalesEnMesas.add(comensal); // Comensal entra a una mesa
                        comensalesEnMesas.notifyAll(); // Notificar a los meseros que hay un comensal en una mesa
                    }
                    iniciarCicloDeComida(comensal);
                } else {
                    System.out.println("No hay mesas disponibles para el comensal " + comensal.getId());
                    synchronized (comensalesEnEspera) {
                        comensalesEnEspera.add(comensal); // Comensal entra en espera
                        comensalesEnEspera.notifyAll(); // Notificar que hay un comensal en espera
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
    private void iniciarCicloDeComida(Comensal comensal) {
        new Thread(() -> {
            try {
                // Tiempo aleatorio para simular el tiempo de comida
                int tiempoComida = new Random().nextInt(5) + 3; // 3 a 5 segundos
                System.out.println("Comensal " + comensal.getId() + " está comiendo en la mesa " + comensal.getMesaId() +
                        " durante " + tiempoComida + " segundos.");
                Thread.sleep(tiempoComida * 1000L); // Simulación de tiempo de comida
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Liberar la mesa después de comer
            liberarMesa(comensal.getMesaId());
            synchronized (comensalesEnMesas) {
                comensalesEnMesas.remove(comensal); // El comensal deja la mesa
                comensalesEnMesas.notifyAll(); // Notificar a los meseros de que hay menos comensales en mesas
            }
            System.out.println("Comensal " + comensal.getId() + " terminó de comer y dejó la mesa " + comensal.getMesaId());
        }).start();
    }
}
