#version 330 core

in vec2 fragTexCoord;  // Texture coordinates from vertex shader
out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
    fragColor = texture(textureSampler, fragTexCoord);  // Sample the texture
}

