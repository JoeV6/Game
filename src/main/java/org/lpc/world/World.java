package org.lpc.world;

import lombok.Getter;
import lombok.Setter;
import org.lpc.Game;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.chunk.Chunk;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Getter @Setter
public class World {
    private static final int MAX_CACHE_SIZE = 100;

    private final ArrayList<Chunk> loadedChunks;
    private final Map<String, Chunk> chunkCache;

    public World() {
        loadedChunks = new ArrayList<>();

        // LinkedHashMap is used to keep the order chunk access order,
        // so that the least recently used chunk can be saved to disk
        chunkCache= new LinkedHashMap<String, Chunk>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                if (size() > MAX_CACHE_SIZE) {
                    saveChunkToDisk((Chunk) eldest.getValue(), (String) eldest.getKey());
                    return true;
                }
                return false;
            }
        };

        deleteDiskCache();
        createDiskCache();

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
        synchronized (loadedChunks) {
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
                        //System.out.println("Created chunk at " + chunkX + ", " + chunkZ);
                    }

                    loadedChunks.add(chunk);
                    //System.out.println("Loaded chunk at " + chunkX + ", " + chunkZ);
                    change = true;
                }
            }
            return change;
        }
    }

    public Chunk getChunk(int chunkX, int chunkZ) {
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

    private String getChunkKey(int x, int z) {
        return x + ":" + z;
    }

    public void addChunk(String chunkKey, Chunk chunk) {
        chunkCache.put(chunkKey, chunk);
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

    private void saveChunkToDisk(Chunk chunk, String chunkKey) {
        String fileName = "chunks/" + Arrays.toString(chunkKey.getBytes()) + ".dat.gz";

        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
             ObjectOutputStream out = new ObjectOutputStream(gzipOut)) {

            out.writeObject(chunk);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Chunk loadChunkFromDisk(String chunkKey) {
        String fileName = "chunks/" + Arrays.toString(chunkKey.getBytes()) + ".dat.gz";

        try (FileInputStream fileIn = new FileInputStream(fileName);
             GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
             ObjectInputStream in = new ObjectInputStream(gzipIn)) {

            return (Chunk) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            //Do nothing, the chunk is just not in the cache
        }
        return null;
    }

    public void deleteDiskCache(){
        System.out.println("\u001B[31m" + "!!! Deleting disk cache !!!" + "\u001B[0m");

        File directory = new File("chunks/");
        if (!directory.exists()) {
            return;
        }

        for(File file : Objects.requireNonNull(directory.listFiles())){
            if (!file.delete())
                throw new RuntimeException("Failed to delete file: " + file.getName());
        }
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
}
