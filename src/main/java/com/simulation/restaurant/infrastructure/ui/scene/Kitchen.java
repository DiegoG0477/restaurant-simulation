package com.simulation.restaurant.infrastructure.ui.scene;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;

public class Kitchen {
    public static Entity create() {

        Entity kitchen = FXGL.entityBuilder()
                .at(40,50)
                .view(FXGL.getAssetLoader().loadTexture("kitchen.png", 250, 200))
                .build();

        Entity chef = FXGL.entityBuilder()
                .at(15,135)
                .view(FXGL.getAssetLoader().loadTexture("chef.png", 50, 80))
                .build();

        Entity chef2 = FXGL.entityBuilder()
                .at(175,135)
                .view(FXGL.getAssetLoader().loadTexture("chef.png", 50, 80))
                .build();

        FXGL.getGameWorld().addEntity(kitchen);
        FXGL.getGameWorld().addEntity(chef);
        FXGL.getGameWorld().addEntity(chef2);

        return kitchen;
    }
}
