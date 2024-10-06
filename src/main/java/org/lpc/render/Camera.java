package org.lpc.render;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Camera {
    private double x, y;
    private double height;

    public Camera(double x, double y) {
        this.x = x;
        this.y = y;
        this.height = 0;
    }

    public void move(double x, double y) {
        this.x += x;
        this.y += y;
    }
}
