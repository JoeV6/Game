package org.lpc.handler;

import org.joml.Vector3f;
import org.lpc.Game;
import org.lpc.render.Camera;
import org.lpc.render.pipeline.models.FullModel;
import org.lpc.world.World;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.chunk.Chunk;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateHandler {
    private final Game game;
    private final World world;
    private final Camera camera;
    private final ExecutorService executor;
    private volatile boolean modelsReady = false;

    public UpdateHandler() {
        game = Game.getInstance();
        world = game.getWorld();
        camera = game.getCamera();

        // Create a single-threaded executor to handle the background tasks
        executor = Executors.newSingleThreadExecutor();
    }

    public void update() {
        Vector3f position = camera.getPosition();

        if (world.updateChunks((int) position.x, (int) position.z, Game.RENDER_DISTANCE)) {
            // Offload model updates to the background thread
            executor.submit(this::updateRenderModels);
        }

        if (modelsReady) {
            // Swap the models safely
            synchronized (game.getRenderModels()) {
                CopyOnWriteArrayList<FullModel> temp = game.getRenderModels();
                game.setRenderModels(game.getNextModels());
                game.setNextModels(temp);
            }
            modelsReady = false;
        }
    }

    private void updateRenderModels() {
        List<FullModel> nextModels = game.getNextModels();
        nextModels.clear();

        for (Chunk chunk : world.getLoadedChunks()) {
            loadChunkModels(chunk, nextModels);
        }

        modelsReady = true;
    }

    private void loadChunkModels(Chunk chunk, List<FullModel> nextModels) {
        AbstractBlock[][][] blocks = chunk.getBlocks();

        for (AbstractBlock[][] chunkLayer : blocks) {
            if (chunkLayer == null) continue;

            for (AbstractBlock[] chunkRow : chunkLayer) {
                if (chunkRow == null) continue;

                for (AbstractBlock block : chunkRow) {
                    if (block != null) {
                        FullModel newModel = block.getCubeModel().getModel();
                        nextModels.add(newModel);
                    }
                }
            }
        }
    }
}

