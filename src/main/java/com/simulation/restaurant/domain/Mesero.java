package main.java.com.simulation.restaurant.domain;

import java.util.Queue;

public class Mesero {
    private final int id;
    private boolean ocupado;
    private Comensal comensalActual;
    private final Queue<Orden> bufferOrdenes;

    public Mesero(int id, Queue<Orden> bufferOrdenes) {
        this.id = id;
        this.ocupado = false;
        this.bufferOrdenes = bufferOrdenes;
    }

    public int getId() {
        return id;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void atenderComensal(Comensal comensal) {
        this.comensalActual = comensal;
        this.ocupado = true;
    }

    public void generarOrden() {
        if (comensalActual != null) {
            Orden orden = new Orden(comensalActual);
            bufferOrdenes.add(orden);
            comensalActual.setOrden(orden);
            this.ocupado = false;
        }
    }

    @Override
    public String toString() {
        return "Mesero{id=" + id + ", ocupado=" + ocupado + "}";
    }
}
