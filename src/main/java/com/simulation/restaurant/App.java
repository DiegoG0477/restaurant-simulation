package com.simulation.restaurant;

import java.util.List;
import com.simulation.restaurant.infrastructure.ui.scene.Entrance;
import com.simulation.restaurant.infrastructure.ui.scene.Kitchen;
import com.simulation.restaurant.infrastructure.ui.scene.Reception;
import com.simulation.restaurant.infrastructure.ui.scene.Table;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;

import com.simulation.restaurant.domain.EventBus;

import com.simulation.restaurant.infrastructure.ui.controller.FXGLRestauranteController;


public class App extends GameApplication {

    private List<Entity> tables;
    private Entity kitchen;
    private Entity reception;
    private Entity entrance;
    private EventBus eventBus;
    private FXGLRestauranteController restauranteController;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Restaurant Simulation");
    }

    @Override
    protected void initGame() {
        FXGL.getGameScene().setBackgroundRepeat("floor.png");
    
        // Crear las entidades de la UI
        entrance = Entrance.create();
        reception = Reception.create();
        kitchen = Kitchen.create();
        tables = Table.createTables(5, 4, 350, 50);
    
        // Añadir entidades al mundo del juego
        FXGL.getGameWorld().addEntity(entrance);
        FXGL.getGameWorld().addEntity(reception);
        FXGL.getGameWorld().addEntity(kitchen);
        tables.forEach(FXGL.getGameWorld()::addEntity);
    
        // Inicializar EventBus y Controller
        eventBus = new EventBus();
        restauranteController = new FXGLRestauranteController(tables, entrance, kitchen, reception, eventBus);
    
        // Crear y simular el restaurante
        Restaurante restaurante = new Restaurante(20, 2, 2, 100, eventBus); // Capacidad, meseros, cocineros, comensales a generar
        restaurante.simular();
    }


    public static void main(String[] args) {
        //app.initializeAndSimulateRestaurant(); // Inicializar y simular el restaurante
        launch(args);
    }
}
