package com.simulation.restaurant.domain;

import java.util.Queue;

public class Mesero {
    private final int id;
    private boolean ocupado;
    private Comensal comensalActual;

    public Mesero(int id, Queue<Orden> bufferOrdenes) {
        this.id = id;
        this.ocupado = false;
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

    public Orden generarOrden(int idMesa) {
        if (comensalActual != null) {
            Orden orden = new Orden(comensalActual, idMesa);
            comensalActual.setOrden(orden);
            this.ocupado = false;

            return orden;
        }

        return null;
    }

    @Override
    public String toString() {
        return "Mesero{id=" + id + ", ocupado=" + ocupado + "}";
    }
}
