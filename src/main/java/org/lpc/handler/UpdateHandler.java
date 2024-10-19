package org.lpc.handler;

import lombok.Setter;
import org.joml.Vector3f;
import org.lpc.Game;
import org.lpc.render.pipeline.models.FullModel;
import org.lpc.world.World;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.chunk.Chunk;
import org.lpc.world.entity.entities.PlayerEntity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateHandler {
    private final Game game;
    private final World world;
    private final PlayerEntity player;
    private final ExecutorService executor;
    private volatile boolean modelsReady = false;

    public UpdateHandler() {
        game = Game.getInstance();
        world = game.getWorld();
        player = game.getPlayer();

        executor = Executors.newSingleThreadExecutor();
    }

    public void update() {
        player.update();
        updateChunks();
    }

    private void updateChunks(){
        Vector3f position = player.getPosition();

        if (world.updateChunks((int) position.x, (int) position.z, Game.RENDER_DISTANCE)) {
            // Offload model updates to the background thread
            executor.submit(this::updateRenderModels);
        }

        if (modelsReady) {
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

    public void loadChunk(int chunkX, int chunkZ){
        Chunk chunk = world.getChunk(chunkX, chunkZ);
        List<FullModel> nextModels = game.getNextModels();
        nextModels.clear();
        loadChunkModels(chunk, nextModels);

        modelsReady = true;
    }


    public void stopThreads(){
        executor.shutdown();
    }
}

