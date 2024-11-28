package com.simulation.restaurant.domain;

public class Orden {
    private int id;
    private final Comensal comensal;
    private final int idMesa; // Identificador de la mesa

    public Orden(Comensal comensal, int idMesa) {
        this.id = comensal.getId();
        this.comensal = comensal;
        this.idMesa = idMesa;
    }

    public int getId() {
        return id;
    }

    public Comensal getComensal() {
        return comensal;
    }

    public int getIdMesa() {
        return idMesa;
    }

    @Override
    public String toString() {
        return "Orden{id=" + id + "}";
    }
}
