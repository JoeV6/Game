package org.lpc.render.pipeline.models;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RawModel {
    private int vaoID;
    private int vertexCount;

    public RawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }
}
