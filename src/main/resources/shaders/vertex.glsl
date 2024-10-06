#version 330 core

layout (location = 0) in vec2 inPosition;  // Vertex position (x, y)
layout (location = 1) in vec2 inTexCoord;  // Texture coordinates

out vec2 fragTexCoord;  // Pass texture coordinates to fragment shader

uniform mat4 modelMatrix;       // The model matrix (per tile)
uniform mat4 viewMatrix;        // The view matrix (camera)
uniform mat4 projectionMatrix;  // The projection matrix

void main()
{
    // Combine matrices to transform vertex position to clip space
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(inPosition, 0.0, 1.0);

    // Pass the texture coordinates to the fragment shader
    fragTexCoord = inTexCoord;
}


