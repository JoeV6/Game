#version 330 core

in vec3 FragPos;     // From the vertex shader
in vec3 Normal;      // From the vertex shader
in vec2 TexCoord;    // From the vertex shader

out vec4 FragColor;  // Output color

uniform vec3 lightPos;    // Light position in world space
uniform vec3 viewPos;     // Camera position (eye position)
uniform sampler2D texture1; // Diffuse texture

// Simple Phong lighting model (diffuse + specular + ambient)
void main() {
    // Ambient lighting
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * vec3(1.0); // Assume white ambient light

    // Diffuse lighting
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * vec3(1.0); // Assume white diffuse light

    // Specular lighting
    float specularStrength = 0.5;
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = specularStrength * spec * vec3(1.0); // Assume white specular light

    // Combine the lighting components
    vec3 lighting = (ambient + diffuse + specular);

    // Apply lighting to the texture color
    vec4 texColor = texture(texture1, TexCoord);
    FragColor = vec4(lighting, 1.0) * texColor;
}
