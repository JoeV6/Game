package org.lpc.handler;

import org.joml.Vector3f;
import org.lpc.Game;
import org.lpc.render.pipeline.models.CubeModel;
import org.lpc.world.World;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.block.BlockType;
import org.lpc.world.chunk.Chunk;
import org.lpc.world.entity.entities.PlayerEntity;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateHandler {
    public boolean renderAll = false;

    private final Game game;
    private final World world;
    private final PlayerEntity player;
    private final ExecutorService executor;
    private volatile boolean modelsReady = false;

    public UpdateHandler() {
        this.game = Game.getInstance();
        this.world = game.getWorld();
        this.player = game.getPlayer();
        int threadCount = Math.min((int) (Runtime.getRuntime().availableProcessors() / 1.2), 5);
        System.out.println("Using " + threadCount + " threads for model updates");
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    public void update() {
        player.update();
        updateChunks();
    }

    private void updateChunks() {
        executor.submit(() -> {
            Vector3f position = player.getPosition();
            boolean chunksUpdated = world.updateChunks((int) position.x, (int) position.z, Game.RENDER_DISTANCE);

            if (chunksUpdated) {
                updateRenderModels();
            }
        });

        if (modelsReady) {
            swapModelLists();
            modelsReady = false;
        }
    }

    private void swapModelLists() {
        synchronized (game) {
            CopyOnWriteArrayList<CubeModel> temp = (CopyOnWriteArrayList<CubeModel>) game.getRenderModels();
            game.setRenderModels(game.getNextModels());
            game.setNextModels(temp);
        }
    }

    private void updateRenderModels() {
        List<CubeModel> nextModels = game.getNextModels();
        nextModels.clear();

        world.getLoadedChunks().forEach(chunk -> loadChunkModels(chunk, nextModels));

        synchronized (this) {
            modelsReady = true;
        }
    }

    private void loadChunkModels(Chunk chunk, List<CubeModel> nextModels) {
        AbstractBlock[][][] blocks = chunk.getBlocks();

        for (AbstractBlock[][] chunkLayer : blocks) {
            if (chunkLayer == null) continue;

            for (AbstractBlock[] chunkColumn : chunkLayer) {
                if (chunkColumn == null) continue;

                addVisibleBlockModels(chunkColumn, nextModels);
            }
        }
    }

    private void addVisibleBlockModels(AbstractBlock[] chunkColumn, List<CubeModel> nextModels) {
        for (AbstractBlock block : chunkColumn) {
            if (isRenderable(block)) {
                nextModels.add(block.getCubeModel());
            }
        }
    }

    private boolean isRenderable(AbstractBlock block) {
        if (block == null || block.getBlockType().equals(BlockType.GAS)) return false;

        return renderAll || Arrays.stream(block.getNeighbouringBlocks())
                .anyMatch(neighbour -> neighbour == null || neighbour.getBlockType().equals(BlockType.GAS));
    }

    public void toggleRenderAll() {
        renderAll = !renderAll;
        System.out.println(renderAll ? "Rendering all blocks" : "Rendering only visible blocks");
    }

    public void cleanUp() {
        executor.shutdown();
    }
}
