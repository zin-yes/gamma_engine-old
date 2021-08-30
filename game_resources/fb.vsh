#version 330

in vec2 Position;
in vec2 TexCoords;

out vec2 texCoords;

void main()
{
	gl_Position = vec4(Position, 0, 1.0);
	
	texCoords = vec2(Position.x/2.0 + 0.5, Position.y/2.0 + 0.5) * 4;
}