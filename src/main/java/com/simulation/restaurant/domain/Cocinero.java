package main.java.com.simulation.restaurant.domain;

import java.util.Queue;

public class Cocinero {
    private final int id;
    private boolean ocupado;
    private final Queue<Orden> bufferOrdenes;
    private final Queue<Comida> bufferComidas;

    public Cocinero(int id, Queue<Orden> bufferOrdenes, Queue<Comida> bufferComidas) {
        this.id = id;
        this.ocupado = false;
        this.bufferOrdenes = bufferOrdenes;
        this.bufferComidas = bufferComidas;
    }

    public int getId() {
        return id;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void prepararOrden() {
        if (!bufferOrdenes.isEmpty()) {
            ocupado = true;
            Orden orden = bufferOrdenes.poll();
            // Simulación de la preparación
            try {
                Thread.sleep(1000); // Simulación del tiempo de cocción
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            bufferComidas.add(new Comida(orden));
            ocupado = false;
        }
    }

    @Override
    public String toString() {
        return "Cocinero{id=" + id + ", ocupado=" + ocupado + "}";
    }
}
