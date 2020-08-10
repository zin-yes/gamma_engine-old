#version 330 core

in vec3 texCoords;

out vec4 Color;

uniform samplerCube skybox_SamplerCube;

void main()
{
	Color = texture(skybox_SamplerCube, texCoords);
}