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
- **OpenGL and LWJGL**: 
  - The game uses **OpenGL** for rendering the 3D world, and **LWJGL** is the framework that integrates OpenGL with Java.
  
- **Voxel Rendering**: 
  - Each block (such as grass and dirt) is rendered as a cube in the 3D space.
  - The terrain is a collection of these blocks, and the graphics engine renders them efficiently in chunks.
  
- **Texturing**: 
  - Simple textures are applied to different block types.
  - The **grass block** has a grass texture on the top and a dirt texture on the sides and bottom.
  
- **Camera and Controls**: 
  - A **first-person camera** is used to navigate the world.
  - You can move around the terrain to explore the procedurally generated landscape using **WASD** keys for movement and the mouse for looking around.

---

## How It Works

### Terrain Generation:
- The terrain is generated for each chunk using **Perlin noise**, which defines the heightmap of the terrain.

### Rendering Pipeline (Needs Improvement)  
- **Efficient Chunk Rendering**:
  - [ ] **Backface Culling**: Only visible blocks are rendered.
  - [ ] **Batched Rendering**: Chunks are rendered together for efficiency.
  - [ ] **GPU Optimization**: More calculations are moved to the GPU for faster rendering.

- **Camera Movement**: 
  - [x] The player can explore the world using basic **WASD** controls for movement, along with mouse input for looking around.

---

This simple Minecraft clone provides a basic introduction to voxel-based terrain generation and rendering using OpenGL and Java. The project demonstrates core concepts such as chunked world generation, Perlin noise terrain, and efficient real-time rendering.
