package org.lpc.handler;

import org.lpc.Game;
import org.lpc.render.Renderer;
import org.lwjgl.opengl.GL11;


public class RenderHandler {
    private final Game game;
    private final Renderer renderer;
    private final long window;

    public RenderHandler() {
        game = Game.getInstance();
        window = game.getWindow();
        renderer = new Renderer();
    }

    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear the buffers

        renderer.renderGame(); // Render the game content

        GL11.glLoadIdentity();
        GL11.glFlush();
        GL11.glFinish();
    }

}
