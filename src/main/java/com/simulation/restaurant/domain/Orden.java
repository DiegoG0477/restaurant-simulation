package main.java.com.simulation.restaurant.domain;

public class Orden {
    private final int id;
    private final Comensal comensal;
    private EstadoOrden estado;

    private static int idCounter = 0;

    public Orden(Comensal comensal) {
        this.id = ++idCounter;
        this.comensal = comensal;
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

    @Override
    public String toString() {
        return "Orden{id=" + id + ", estado=" + estado + "}";
    }
}
