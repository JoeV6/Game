package org.lpc.handler;

import org.joml.Vector3f;
import org.lpc.Game;
import org.lpc.render.Camera;
import org.lpc.render.pipeline.models.FullModel;
import org.lpc.world.World;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

public class UpdateHandler {
    private final Game game;
    private final World world;
    private final Camera camera;

    public UpdateHandler() {
        game = Game.getInstance();
        world = game.getWorld();
        camera = game.getCamera();
    }

    public void update() {
        Vector3f position = game.getCamera().getPosition();
        if (world.updateChunks((int) position.x, (int) position.z, 1))
            updateRenderModels();
    }

    private void updateRenderModels() {
        List<FullModel> renderModels = game.getRenderModels();

        renderModels.clear();

        for (Chunk chunk : world.getLoadedChunks()) {
            AbstractBlock[][][] blocks = chunk.getBlocks();

            for (AbstractBlock[][] chunkLayer : blocks) {
                if (chunkLayer == null) continue;

                for (AbstractBlock[] chunkRow : chunkLayer) {
                    if (chunkRow == null) continue;

                    for (AbstractBlock block : chunkRow) {
                        if (block != null) {
                            FullModel newModel = block.getCubeModel().getModel();
                            renderModels.add(newModel);
                        }
                    }
                }
            }
        }
    }
}
