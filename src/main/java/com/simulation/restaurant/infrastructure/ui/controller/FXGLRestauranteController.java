package com.simulation.restaurant.infrastructure.ui.controller;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.application.Platform;
import com.simulation.restaurant.domain.Comensal;
import com.simulation.restaurant.domain.EventBus;
import com.simulation.restaurant.domain.Cocinero;
import com.simulation.restaurant.domain.Mesero;
import com.simulation.restaurant.domain.interfaces.Observer;
import java.util.Random;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class FXGLRestauranteController implements Observer {

    private final List<Entity> mesas;
    private final Entity entrance;
    private final Entity kitchen;
    private final Entity reception;
    private final Queue<Comensal> queueComensales = new LinkedList<>();
    private final Map<Comensal, Entity> comensalesEntities = new HashMap<>();


    public FXGLRestauranteController(List<Entity> mesas, Entity entrance, Entity kitchen, Entity reception, EventBus eventBus) {
        this.mesas = mesas;
        this.entrance = entrance;
        this.kitchen = kitchen;
        this.reception = reception;
        eventBus.subscribe(this);
    }

    @Override
    public void update(String event, Object data) {
        switch (event) {
            case "NEW_COMENSAL":
                Comensal comensal = (Comensal) data;
                addComensalToMesa(comensal);
                break;

            case "NEW_QUEUE_COMENSAL":
                Comensal queueComensal = (Comensal) data;
                addComensalToQueue(queueComensal);
                break;

            case "EXIT_COMENSAL":
                Comensal exitingComensal = (Comensal) data;
                liberarMesa(exitingComensal);
                break;

            case "ATTEND_CLIENT":
                Mesero mesero = (Mesero) data;
                moveMeseroToClient(mesero);
                break;

            case "SERVE_DISH":
                Mesero servingMesero = (Mesero) data;
                servePlato(servingMesero);
                break;

            case "CHEF_COOKING":
                Cocinero cocinero = (Cocinero) data;
                setChefCooking(cocinero);
                break;

            default:
                System.out.println("Evento desconocido: " + event);
                break;
        }
    }

    private void addComensalToMesa(Comensal comensal) {
        //la cola sigue funcionando mal
        Platform.runLater(() -> {
            if (queueComensales.isEmpty()) {
                System.out.println("No hay comensales en la fila para asignar a una mesa.");
                return;
            }
    
            // Extraemos al siguiente comensal de la fila
            Comensal comensalEnFila = queueComensales.poll();
    
            int mesaId = comensalEnFila.getMesaId();
            if (mesaId >= 0 && mesaId < mesas.size()) {
                Entity mesa = mesas.get(mesaId);
    
                Random random = new Random();
                int skin = random.nextInt(3); // Elegir un skin aleatorio
    
                String[] boys = {"boy.png", "boy2.png", "boy3.png"};
    
                // Crear la entidad gráfica del comensal
                Entity comensalEntity = FXGL.entityBuilder()
                        .at(mesa.getX() + 10, mesa.getY() + 10) // Ajuste de posición cerca de la mesa
                        .view(FXGL.getAssetLoader().loadTexture(boys[skin], 50, 70))
                        .build();
                FXGL.getGameWorld().addEntity(comensalEntity);
    
                // Registrar el comensal y su entidad gráfica en el mapa
                comensalesEntities.put(comensalEnFila, comensalEntity);
    
                System.out.println("Comensal " + comensalEnFila.getId() + " añadido a la mesa " + mesaId);
            }
        });
    }
    
    
    
    private void addComensalToQueue(Comensal comensal) {
        Platform.runLater(() -> {
            queueComensales.add(comensal);
    
            Random random = new Random();
            int skin = random.nextInt(3);
    
            String[] boys = {"boy.png", "boy2.png", "boy3.png"};
    
            // Crear la representación gráfica del comensal en la fila
            Entity comensalEntity = FXGL.entityBuilder()
                    .at(entrance.getX() + 10, entrance.getY() - 50 * comensal.getId()) // Fila vertical en la entrada
                    .view(FXGL.getAssetLoader().loadTexture(boys[skin], 50, 70))
                    .build();
            FXGL.getGameWorld().addEntity(comensalEntity);
    
            System.out.println("Comensal " + comensal.getId() + " añadido a la fila.");
        });
    }
    
    
    
    private void liberarMesa(Comensal comensal) {
        Platform.runLater(() -> {
            // Obtener la entidad gráfica asociada al comensal
            Entity comensalEntity = comensalesEntities.get(comensal);
    
            if (comensalEntity != null) {
                // Eliminar la entidad gráfica del mundo
                comensalEntity.removeFromWorld();
    
                // Eliminar la entrada del HashMap
                comensalesEntities.remove(comensal);
    
                System.out.println("Mesa " + comensal.getMesaId() + " liberada por comensal " + comensal.getId());
    
                // Intentar asignar un nuevo comensal desde la fila
                if (!queueComensales.isEmpty()) {
                    addComensalToMesa(queueComensales.poll());
                }
            } else {
                System.out.println("No se encontró entidad gráfica para el comensal " + comensal.getId());
            }
        });
    }
    
    private void moveMeseroToClient(Mesero mesero) {
        Comensal comensal = mesero.getComensalActual();
        if (comensal != null) {
            System.out.println("Mesero " + mesero.getId() + " moviéndose hacia el comensal " + comensal.getId());
        }
    }

    private void servePlato(Mesero mesero) {
        System.out.println("Mesero " + mesero.getId() + " sirviendo plato");
    }

    private void setChefCooking(Cocinero cocinero) {
        System.out.println("Cocinero " + cocinero.getId() + " está cocinando");
    }
}