package org.lpc.handler;

import org.joml.Vector3f;
import org.lpc.Game;
import org.lpc.render.pipeline.models.CubeModel;
import org.lpc.world.World;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.chunk.Chunk;
import org.lpc.world.entity.entities.PlayerEntity;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.awt.SystemColor.window;

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

        int nThreads = Runtime.getRuntime().availableProcessors();
        int useThreads = Math.min((int) (nThreads / 1.2), 5);
        System.out.println("Using " + useThreads + " threads for model updates");
        executor = Executors.newFixedThreadPool(useThreads);
    }

    public void update() {
        player.update();
        updateChunks();
    }

    private void updateChunks(){
        Vector3f position = player.getPosition();

        executor.submit(() -> {
            if (world.updateChunks((int) position.x, (int) position.z, Game.RENDER_DISTANCE)) {
                updateRenderModels();
            }
        });


        if (modelsReady) {
            synchronized (game) {
                CopyOnWriteArrayList<CubeModel> temp = (CopyOnWriteArrayList<CubeModel>) game.getRenderModels();
                game.setRenderModels(game.getNextModels());
                game.setNextModels(temp);
            }
            modelsReady = false;
        }
    }

    private void updateRenderModels() {
        List<CubeModel> nextModels = game.getNextModels();
        nextModels.clear();

        for (Chunk chunk : world.getLoadedChunks()) {
            loadChunkModels(chunk, nextModels);
        }

        synchronized (this) {
            modelsReady = true;
        }
    }

    private void loadChunkModels(Chunk chunk, List<CubeModel> nextModels) {
        AbstractBlock[][][] blocks = chunk.getBlocks();

        for (AbstractBlock[][] chunkLayer : blocks) {
            if (chunkLayer == null) continue;

            for (AbstractBlock[] chunkRow : chunkLayer) {
                if (chunkRow == null) continue;

                for (AbstractBlock block : chunkRow) {
                    if (block == null || block.getBlockID() == -1) continue;

                    AbstractBlock[] neighbours = block.getNeighbouringBlocks();

                    for (AbstractBlock neighbour : neighbours) {
                        if (neighbour == null || neighbour.getBlockID() == -1) {
                            nextModels.add(block.getCubeModel());
                            break;
                        }
                    }
                }
            }
        }
    }

    public void cleanUp(){
        executor.shutdown();
    }
}
