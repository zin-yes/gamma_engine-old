#version 330

in vec2 texCoords;

out vec4 Color;

uniform sampler2D color;

void main()
{
	Color = vec4(texture(color, texCoords));
}