package com.simulation.restaurant.infrastructure.ui.scene;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;


public class Reception {
    public static Entity create() {

        Entity reception = FXGL.entityBuilder()
                .at(100, 500)
                .view(FXGL.getAssetLoader().loadTexture("table.png"))
                .build();

        Entity receptionist = FXGL.entityBuilder()
                .at(35, 480)
                .view(FXGL.getAssetLoader().loadTexture("receptionist.png", 50, 80))
                .build();

        Entity person = FXGL.entityBuilder()
                .at(200, 490)
                .view(FXGL.getAssetLoader().loadTexture("girl2.png", 60, 80))
                .build();

        FXGL.getGameWorld().addEntity(receptionist);
        FXGL.getGameWorld().addEntity(person);

        return reception;
    }
}
