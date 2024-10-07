#version 330 core

in vec3 position;
in vec2 texCoord;

out vec3 colour;
out vec2 fragTexCoord;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
    colour = position;
    fragTexCoord = texCoord;
}


