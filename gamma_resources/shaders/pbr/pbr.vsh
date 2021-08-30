#version 330 core

in vec3 input_Position;
in vec2 input_TexCoords;
in vec3 input_Normal;
in vec3 input_Tangent;
in vec3 input_biTangent;

out vec2 TexCoords;
out vec3 Normal;
out vec3 WorldPos;
out vec3 FragPos;
out vec3 ViewPos;

out mat3 TBN;	

uniform mat4 transform_Projection;
uniform mat4 transform_Model;
uniform mat4 transform_View;

void main()
{
    WorldPos = vec3(transform_Model * vec4(input_Position, 1.0));
	
	TBN = mat3(
		normalize(vec3(transform_Model * vec4(input_Tangent, 0.0))),
		normalize(vec3(transform_Model * vec4(input_biTangent, 0.0))),
		normalize(vec3(transform_Model * vec4(input_Normal, 0.0)))
	);
		
   	FragPos = TBN * WorldPos;
	ViewPos = (inverse(transform_View)[3]).xyz;
   
    TexCoords = input_TexCoords;
    Normal = mat3(transform_Model) * input_Normal;

    gl_Position =  transform_Projection * transform_View * vec4(WorldPos, 1.0);
}