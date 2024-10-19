#version 330 core

layout(location = 0) in vec3 position;         // Vertex position
layout(location = 1) in vec2 texCoord;         // Texture coordinate
layout(location = 2) in vec3 instanceOffset;   // Instance offset from VBO

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec2 fragTexCoord;         // Output to fragment shader

void main() {
    mat4 modelMatrix = mat4(1.0);
    modelMatrix[3].xyz = instanceOffset; // Set translation from instance offset

    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
    fragTexCoord = texCoord;
}




