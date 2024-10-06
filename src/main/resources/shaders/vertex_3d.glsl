#version 330 core

layout(location = 0) in vec3 position;  // 3D position of the vertex
layout(location = 1) in vec3 normal;    // Normal vector for lighting
layout(location = 2) in vec2 texCoord;  // Texture coordinates

uniform mat4 model;       // Model matrix
uniform mat4 view;        // View (camera) matrix
uniform mat4 projection;  // Projection matrix

out vec3 FragPos;         // Pass position to fragment shader for lighting
out vec3 Normal;          // Pass normal vector for lighting
out vec2 TexCoord;        // Pass texture coordinates

void main() {
    // Calculate the position in world space
    FragPos = vec3(model * vec4(position, 1.0));

    // Pass the transformed normal to the fragment shader
    Normal = mat3(transpose(inverse(model))) * normal;

    // Pass texture coordinates
    TexCoord = texCoord;

    // Apply model, view, and projection transformations to vertex position
    gl_Position = projection * view * vec4(FragPos, 1.0);
}
