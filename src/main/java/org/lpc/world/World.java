package org.lpc.world;

import lombok.Getter;
import lombok.Setter;
import org.lpc.Game;
import org.lpc.utils.SystemUtils;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.chunk.Chunk;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.lpc.utils.SystemUtils.retryTask;

@Getter @Setter
public class World {
    private final ArrayList<Chunk> loadedChunks;
    private final Map<String, Chunk> chunkCache;

    public World() {
        loadedChunks = new ArrayList<>();
        chunkCache= new ChunkCache<>(); // Extends linked hash map to limit the cache size

        createDiskCache();

        init();
    }

    private void init() {
        updateChunks(0, 0, 4);
    }

    public boolean updateChunks(int playerX, int playerZ, int renderDistance) {
        boolean farChunks;
        boolean loadChunksAround;

        // Synchronize on the chunk cache to avoid multiple threads accessing it at the same time
        synchronized (chunkCache) {
            farChunks = unloadFarChunks(playerX, playerZ, renderDistance);
            loadChunksAround = loadChunksAround(playerX, playerZ, renderDistance);
        }

        return farChunks || loadChunksAround;
    }

    private boolean unloadFarChunks(int playerX, int playerZ, int renderDistance) {
        int playerChunkX = Math.floorDiv(playerX, Chunk.CHUNK_SIZE);
        int playerChunkZ = Math.floorDiv(playerZ, Chunk.CHUNK_SIZE);

        return loadedChunks.removeIf(chunk -> {
            int dx = Math.abs(chunk.getChunkX() - playerChunkX);
            int dz = Math.abs(chunk.getChunkZ() - playerChunkZ);
            return dx > renderDistance || dz > renderDistance;
        });
    }

    private boolean loadChunksAround(int playerX, int playerZ, int renderDistance) {
        int playerChunkX = Math.floorDiv(playerX, Chunk.CHUNK_SIZE);
        int playerChunkZ = Math.floorDiv(playerZ, Chunk.CHUNK_SIZE);

        boolean change = false;

        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                int chunkX = playerChunkX + x;
                int chunkZ = playerChunkZ + z;

                Chunk chunk = getChunk(chunkX, chunkZ);

                if (chunk != null && loadedChunks.contains(chunk)) continue;

                if (chunk == null) {
                    chunk = new Chunk(chunkX, chunkZ);
                    chunkCache.put(getChunkKey(chunkX, chunkZ), chunk);
                }

                loadedChunks.add(chunk);
                change = true;
            }
        }
        return change;
    }

    public Chunk getChunk(int chunkX, int chunkZ) {
        synchronized (chunkCache) {
            String chunkKey = getChunkKey(chunkX, chunkZ);

            if (chunkCache.containsKey(chunkKey)) {
                return chunkCache.get(chunkKey);
            }

            Chunk chunk = loadChunkFromDisk(chunkKey);
            if (chunk != null) {
                addChunk(chunkKey, chunk);
            }
            return chunk;
        }
    }

    private String getChunkKey(int x, int z) {
        return x + ":" + z;
    }

    public void addChunk(String chunkKey, Chunk chunk) {
        synchronized (chunkCache) {
            chunkCache.put(chunkKey, chunk);
        }
    }

    public AbstractBlock getBlockAt(float x, float y, float z) {
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

    public void saveChunkToDisk(Chunk chunk, String chunkKey) {
        String[] split = chunkKey.split(":");
        int x = Integer.parseInt(split[0]);
        int z = Integer.parseInt(split[1]);

        String fileName = "chunks/[" + x + ", " + z + "].dat.gz";

        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
             ObjectOutputStream out = new ObjectOutputStream(gzipOut)) {

            out.writeObject(chunk);
            //System.out.println("Saved chunk to disk: " + chunkKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Chunk loadChunkFromDisk(String chunkKey) {
        String[] split = chunkKey.split(":");
        int x = Integer.parseInt(split[0]);
        int z = Integer.parseInt(split[1]);

        String fileName = "chunks/[" + x + ", " + z + "].dat.gz";

        try (FileInputStream fileIn = new FileInputStream(fileName);
             GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
             ObjectInputStream in = new ObjectInputStream(gzipIn)) {

            //System.out.println("Loaded chunk from disk: " + chunkKey);
            return (Chunk) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            //Do nothing, the chunk is just not in the cache
        }
        return null;
    }

    public void deleteChunksFromDisk() {
        System.out.print(SystemUtils.RED_TEXT);

        System.out.println("\n!!! Deleting disk cache !!!");

        File directory = new File("chunks/");
        if (!directory.exists()) {
            System.out.println("Nothing to delete.");
            return;
        }

        List<String> deletedFiles = new ArrayList<>();

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            boolean success = retryTask(3, 2000, () -> {
                if (!file.delete()) {
                    throw new RuntimeException("File deletion failed");
                }
            });

            if (!success) {
                throw new RuntimeException("Failed to delete file after multiple attempts: " + file.getName());
            } else {
                deletedFiles.add(file.getName());
            }
        }

        if(deletedFiles.isEmpty()){
            System.out.println("Nothing to delete.");
        } else {
            System.out.println("Deleted " + deletedFiles.size() + " files:");
            System.out.println(deletedFiles);
        }

        System.out.println(SystemUtils.NORMAL_TEXT);
    }


    private void createDiskCache() {
        File directory = new File("chunks/");
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Created chunks/ directory.");
            } else {
                throw new RuntimeException("Failed to create chunks/ directory.");
            }
        }
    }

    static class ChunkCache<K, V> extends LinkedHashMap<K,V> {
        private static final int MAX_CACHE_SIZE = 100;

        public ChunkCache() {
            super(MAX_CACHE_SIZE, 0.75f, true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            if (size() > MAX_CACHE_SIZE) {
                Game.getInstance().getWorld().saveChunkToDisk((Chunk) eldest.getValue(), (String) eldest.getKey());
                return true;
            }
            return false;
        }
    }
}