#version 330 core

in vec3 input_Position;
in vec2 input_TexCoords;
in vec3 input_Normal;

out vec2 texCoords;
out vec3 normal;
out vec3 worldPos;

uniform mat4 transform_Projection;
uniform mat4 transform_Model;
uniform mat4 transform_View;

void main()
{
    texCoords = input_TexCoords;
    worldPos = vec3(transform_Model * vec4(input_Position, 1.0));
    normal = mat3(transform_Model) * input_Normal;

    gl_Position =  transform_Projection * transform_View * vec4(worldPos, 1.0);
}