#version 330 core

in vec3 Position;

out vec3 texCoords;

uniform mat4 transform_Projection;
uniform mat4 transform_View;

void main()
{
	gl_Position = transform_Projection * vec4(mat3(transform_View) * Position, 1.0);
	texCoords = Position;
}