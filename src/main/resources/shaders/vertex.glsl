#version 330 core

layout(location = 0) in vec3 position;         // Vertex position
layout(location = 1) in vec2 texCoord;         // Texture coordinate
layout(location = 2) in vec3 instanceOffset;   // Instance offset from VBO
layout(location = 3) in int textureID;         // Instance input for texture ID

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec2 fragTexCoord;
out float fragTextureID;

void main() {
    mat4 modelMatrix = mat4(1.0);
    modelMatrix[3].xyz = instanceOffset; // Set translation from instance offset

    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
    fragTexCoord = texCoord;
    fragTextureID = textureID;
}




