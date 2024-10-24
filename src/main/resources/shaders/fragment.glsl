#version 330 core

in vec2 fragTexCoord;
in float fragTextureID;
out vec4 color;

uniform sampler2DArray textureArray;

void main() {
    color = texture(textureArray, vec3(fragTexCoord, fragTextureID));
}

