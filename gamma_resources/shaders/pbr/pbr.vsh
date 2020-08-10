#version 330 core

in vec3 input_Position;
in vec2 input_TexCoords;
in vec3 input_Normal;

out vec2 TexCoords;
out vec3 Normal;
out vec3 WorldPos;

uniform mat4 transform_Projection;
uniform mat4 transform_Model;
uniform mat4 transform_View;

void main()
{
    TexCoords = input_TexCoords;
    WorldPos = vec3(transform_Model * vec4(input_Position, 1.0));
    Normal = mat3(transform_Model) * input_Normal;

    gl_Position =  transform_Projection * transform_View * vec4(WorldPos, 1.0);
}