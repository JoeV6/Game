# Simple Minecraft Clone

### A basic Minecraft-inspired voxel world engine using OpenGL (via LWJGL) for rendering. The world is procedurally generated using Perlin noise and consists of simple block types such as grass and dirt. The terrain is rendered in real-time with 3D graphics.

---

## Features

- **Chunk-Based World Generation**: 
  - The world is divided into **16x16** chunks, with each chunk being **16 blocks wide** and **64 blocks tall**.
  
- **Perlin Noise Terrain**: 
  - Procedural terrain generation based on **Perlin noise** creates smooth and natural-looking landscapes.

- **Basic Blocks**:
  - **Grass Block**: Top layer of terrain.
  - **Dirt Block**: Found below grass blocks.

---

## Rendering

This project uses **OpenGL** through **LWJGL (Lightweight Java Game Library)** to render the 3D voxel terrain. The blocks are drawn as textured cubes, and the terrain is dynamically updated chunk by chunk.

### Rendering Details:  
- **Voxel Rendering**: 
  - [x] Each block (such as grass and dirt) is rendered as a cube in the 3D space.
  - [x] The terrain is a collection of these blocks, and the graphics engine renders them efficiently in chunks.
  
- **Texturing**: 
  - [x] Simple textures are applied to different block types.
  
- **Camera and Controls**: 
  - [x] The player can explore the world using basic **WASD** controls for movement, along with mouse input for looking around.
  - [ ] Player can interact with the map

---

## How It Works

### Terrain Generation:
- [x] The terrain is generated for each chunk using **Perlin noise**, which defines the heightmap of the terrain.
- [x] Terrain is stored in chunks, only 100 chunks are ever cached at the same time, the other chunks are stored to disk.

### Rendering Pipeline (Needs Improvement)  
- **Efficient Chunk Rendering**:
  - [ ] **Backface Culling**: Only visible blocks are rendered.
  - [x] **Instanced Rendering**: 1 draw call for all blocks.
  - [ ] **GPU Optimization**: More calculations are moved to the GPU for faster rendering.

---

This simple Minecraft clone provides a basic introduction to voxel-based terrain generation and rendering using OpenGL and Java. The project demonstrates core concepts such as chunked world generation, Perlin noise terrain, and efficient real-time rendering.
