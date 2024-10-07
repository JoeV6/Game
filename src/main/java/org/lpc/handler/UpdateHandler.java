package org.lpc.handler;

import org.joml.Vector3f;
import org.lpc.Game;
import org.lpc.render.pipeline.models.FullModel;
import org.lpc.render.pipeline.models.TexturedModel;
import org.lpc.world.World;

public class UpdateHandler {
    private final Game game;
    private final World world;

    public UpdateHandler() {
        game = Game.getInstance();
        world = game.getWorld();
    }

    public void update() {
        
    }
}
