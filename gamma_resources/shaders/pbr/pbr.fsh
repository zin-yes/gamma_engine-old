#version 430 core

struct Material
{
	sampler2D albedo, normal, height, roughness, metallic, ambient_occlusion;
};

struct DirectionalLight
{
	vec3 direction, color;
};

struct PointLight
{
	vec3 position, color, attenuation;
};

struct SpotLight
{
	vec3 position, direction, color, attenuation;
	vec2 cutOff, outerCutOff;
};

////////////////////////////////////////////////////////////////
//  The following PBR calculations are done with the help of  //
//  the wonderful Joey de Vries on http://learnopengl.com/.   //
////////////////////////////////////////////////////////////////

in vec2 TexCoords;
in vec3 Normal;
in vec3 WorldPos;

out vec4 Color;

uniform Material pbr_Material;

// Directional and Spot lights will be added once the lighting
// calculations are properly in place.

uniform DirectionalLight[4] input_DirectionalLights;
uniform PointLight[32] input_PointLights;
uniform SpotLight[32] input_SpotLights;

// Temporary.
uniform mat4 transform_View;

uniform samplerCube skybox_SamplerCube;

const float PI = 3.14159265359;

// This will be replaced by a proper normal mapping system in the coming updates.
vec3 getNormalFromMap()
{
    vec3 tangentNormal = texture(pbr_Material.normal, TexCoords).xyz * 2.0 - 1.0;

    vec3 Q1  = dFdx(WorldPos);
    vec3 Q2  = dFdy(WorldPos);
    vec2 st1 = dFdx(TexCoords);
    vec2 st2 = dFdy(TexCoords);

    vec3 N   = normalize(Normal);
    vec3 T  = normalize(Q1*st2.t - Q2*st1.t);
    vec3 B  = -normalize(cross(N, T));
    mat3 TBN = mat3(T, B, N);

    return normalize(TBN * tangentNormal);
}

float DistributionGGX(vec3 N, vec3 H, float roughness)
{
    float a = roughness*roughness;
    float a2 = a*a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return nom / denom;
}

float GeometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return nom / denom;
}

float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = GeometrySchlickGGX(NdotV, roughness);
    float ggx1 = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}

vec3 FresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
}

void main()
{	
	vec3 camPos = (inverse(transform_View)[3]).xyz;
    vec3 N = getNormalFromMap();
    vec3 V = normalize(camPos - WorldPos);
    vec3 R = reflect(-V, N); 

    vec3 albedo     = pow(texture(pbr_Material.albedo, TexCoords).rgb, vec3(2.2));
    float metallic  = texture(pbr_Material.metallic, TexCoords).r;
    float roughness = texture(pbr_Material.roughness, TexCoords).r;
    float ao        = texture(pbr_Material.ambient_occlusion, TexCoords).r;

    vec3 F0 = vec3(0.04); 
    F0 = mix(F0, albedo, metallic);

    vec3 Lo = vec3(0.0);
    for(int i = 0; i < 32; ++i) 
    {
        vec3 L = normalize(input_PointLights[i].position - WorldPos);
        vec3 H = normalize(V + L);
        float distance = length(input_PointLights[i].position - WorldPos);
        float attenuation = 1.0 / (distance * distance);
        vec3 radiance = input_PointLights[i].color * attenuation;

        float NDF = DistributionGGX(N, H, roughness);   
        float G   = GeometrySmith(N, V, L, roughness);    
        vec3 F    = FresnelSchlick(max(dot(H, V), 0.0), F0);        
        
        vec3 nominator    = NDF * G * F;
        float denominator = 4 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.001;
		vec3 specular = nominator / denominator;
        
        vec3 kS = F;
        vec3 kD = vec3(1.0) - kS;
        kD *= 1.0 - metallic;	                
            
        float NdotL = max(dot(N, L), 0.0);        

        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
	}   
    
    vec3 kS = FresnelSchlick(max(dot(N, V), 0.0), F0);
    vec3 kD = 1.0 - kS;
    kD *= 1.0 - metallic;	  
    vec3 irradiance = texture(skybox_SamplerCube, N).rgb;
    vec3 diffuse = irradiance * albedo;
	vec3 ambient = (kD * diffuse) * ao; 
    
    vec3 color = Lo + vec3(0.001);

    color = color / (color + vec3(1.0));
    Color = vec4(pow(color, vec3(1.0/2.2)), 1.0);
}
