package com.simulation.restaurant.domain;

public class Orden {
    private final int id;
    private int idCounter = 0;
    private final Comensal comensal;
    private final int idMesa; // Identificador de la mesa
    private EstadoOrden estado;

    public Orden(Comensal comensal, int idMesa) {
        this.id = this.idCounter++;
        this.comensal = comensal;
        this.idMesa = idMesa;
        this.estado = EstadoOrden.PENDIENTE;
    }

    public int getId() {
        return id;
    }

    public Comensal getComensal() {
        return comensal;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }


    public int getIdMesa() {
        return idMesa;
    }

    @Override
    public String toString() {
        return "Orden{id=" + id + ", estado=" + estado + "}";
    }
}
