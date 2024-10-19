package org.lpc.world;

import lombok.Getter;
import lombok.Setter;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class World {
    Map<String, Chunk> chunks;
    ArrayList<Chunk> loadedChunks;

    public World() {
        chunks = new HashMap<>();
        loadedChunks = new ArrayList<>();
        init();
    }

    private void init() {
        updateChunks(0, 0, 4);
    }

    public boolean updateChunks(int playerX, int playerZ, int renderDistance) {
        boolean farChunks = unloadFarChunks(playerX, playerZ, renderDistance);
        boolean loadChunksAround = loadChunksAround(playerX, playerZ, renderDistance);

        return farChunks || loadChunksAround;
    }

    public boolean unloadFarChunks(int playerX, int playerZ, int renderDistance) {
        // Convert player world coordinates to chunk coordinates
        int playerChunkX = Math.floorDiv(playerX, Chunk.CHUNK_SIZE);
        int playerChunkZ = Math.floorDiv(playerZ, Chunk.CHUNK_SIZE);

        return loadedChunks.removeIf(chunk -> {
            int dx = Math.abs(chunk.getChunkX() - playerChunkX);
            int dz = Math.abs(chunk.getChunkZ() - playerChunkZ);
            return dx > renderDistance || dz > renderDistance;
        });
    }

    public boolean loadChunksAround(int playerX, int playerZ, int renderDistance) {
        // Convert player world coordinates to chunk coordinates
        int playerChunkX = Math.floorDiv(playerX, Chunk.CHUNK_SIZE);
        int playerChunkZ = Math.floorDiv(playerZ, Chunk.CHUNK_SIZE);

        boolean change = false;

        // Load chunks around the player
        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                int chunkX = playerChunkX + x;
                int chunkZ = playerChunkZ + z;

                Chunk chunk = getChunk(chunkX, chunkZ);

                if(chunk != null && loadedChunks.contains(chunk)) continue;

                if (chunk == null) {
                    chunk = new Chunk(chunkX, chunkZ);
                    chunks.put(getChunkKey(chunkX, chunkZ), chunk);
                    System.out.println("Created chunk at " + chunkX + ", " + chunkZ);
                }

                loadedChunks.add(chunk);
                System.out.println("Loaded chunk at " + chunkX + ", " + chunkZ);
                change = true;

            }
        }

        return  change;
    }

    private String getChunkKey(int x, int z) {
        return x + ":" + z;
    }

    public Chunk getChunk(int chunkX, int chunkZ) {
        return chunks.get(getChunkKey(chunkX, chunkZ));
    }

    public AbstractBlock getBlockWorld(float x, float y, float z) {
        int chunkX = Math.floorDiv((int) x, Chunk.CHUNK_SIZE);
        int chunkZ = Math.floorDiv((int) z, Chunk.CHUNK_SIZE);

        Chunk chunk = getChunk(chunkX, chunkZ);

        if(chunk == null) return null;

        int blockX = Math.floorMod((int) x, Chunk.CHUNK_SIZE);
        int blockY = Math.floorMod((int) y, Chunk.CHUNK_HEIGHT);
        int blockZ = Math.floorMod((int) z, Chunk.CHUNK_SIZE);

        return chunk.getBlock(blockX, blockY, blockZ);
    }

    public void removeBlock(AbstractBlock block){
        Chunk c = getBlockChunk(block);

        if(c == null) return;
            
        int x = Math.floorMod(block.getX(), Chunk.CHUNK_SIZE);
        int y = block.getY();
        int z = Math.floorMod(block.getZ(), Chunk.CHUNK_SIZE);

        c.setBlock(x, y, z, null);
    }

    public Chunk getBlockChunk(AbstractBlock block){
        return getChunk(
                Math.floorDiv(block.getX(), Chunk.CHUNK_SIZE),
                Math.floorDiv(block.getZ(), Chunk.CHUNK_SIZE)
        );
    }

}
