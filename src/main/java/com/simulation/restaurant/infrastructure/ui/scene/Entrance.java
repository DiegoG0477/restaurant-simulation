package com.simulation.restaurant.infrastructure.ui.scene;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Entrance {
    public static Entity create() {

        Entity Entrance = FXGL.entityBuilder()
                .at(180, 580)
                .view(new Rectangle(100, 20, Color.BROWN))
                .build();

        return Entrance;
    }
}