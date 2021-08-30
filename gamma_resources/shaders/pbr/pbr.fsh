#version 430 core

struct Material
{
	sampler2D albedo, normal, height, roughness, metallic, ambient_occlusion;
	float height_scale;
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
in vec3 WorldPos;
in vec3 ViewPos;
in vec3 ViewDir;

in mat3 TBN;

out vec4 Color;

uniform Material pbr_Material;

uniform DirectionalLight[4] input_DirectionalLights;
uniform PointLight[32] input_PointLights;
uniform SpotLight[32] input_SpotLights;

uniform samplerCube skybox_SamplerCube;
uniform int selected;

const float PI = 3.14159265359;

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

vec3 Normal(vec2 parallaxTexCoords)
{ 
    vec3 normal = texture(pbr_Material.normal, parallaxTexCoords).xyz;
    normal = normalize((255.0 / 128.0) * normal - 1.0);  
    
	return normal;
}

vec2 ParallaxMapping(vec2 texCoords, vec3 viewDir)
{ 
    // number of depth layers
	const float minLayers = 8.0;
	const float maxLayers = 32.0;
	float numLayers = mix(maxLayers, minLayers, max(dot(vec3(0.0, 0.0, 1.0), viewDir), 0.0));  
    // calculate the size of each layer
    float layerDepth = 1.0 / numLayers;
    // depth of current layer
    float currentLayerDepth = 0.0;
    // the amount to shift the texture coordinates per layer (from vector P)
    vec2 P = vec2(viewDir.x, -viewDir.y) * pbr_Material.height_scale; 
    vec2 deltaTexCoords = P / numLayers;
    
    // get initial values
	vec2  currentTexCoords     = texCoords;
	float currentDepthMapValue = 1 - texture(pbr_Material.height, currentTexCoords).r;
	  
	while(currentLayerDepth < currentDepthMapValue)
	{
	    // shift texture coordinates along direction of P
	    currentTexCoords -= deltaTexCoords;
	    // get depthmap value at current texture coordinates
	    currentDepthMapValue = 1 - texture(pbr_Material.height, currentTexCoords).r;  
	    // get depth of next layer
	    currentLayerDepth += layerDepth;  
	}
	
	// get texture coordinates before collision (reverse operations)
	vec2 prevTexCoords = currentTexCoords + deltaTexCoords;
	
	// get depth after and before collision for linear interpolation
	float afterDepth  = currentDepthMapValue - currentLayerDepth;
	float beforeDepth = 1 - texture(pbr_Material.height, currentTexCoords).r - currentLayerDepth + layerDepth;
	 
	// interpolation of texture coordinates
	float weight = afterDepth / (afterDepth - beforeDepth);
	vec2 finalTexCoords = prevTexCoords * weight + currentTexCoords * (1.0 - weight);
	
	return finalTexCoords; 
	
	return currentTexCoords;
} 

void main()
{	
    vec3 V = TBN * normalize(ViewPos - WorldPos);
    
    vec2 ParallaxTexCoords = ParallaxMapping(TexCoords, V);
	    
    vec3 albedo = pow(texture(pbr_Material.albedo, ParallaxTexCoords).rgb, vec3(2.2));
    
    vec3 N = Normal(ParallaxTexCoords);
    vec3 R = reflect(-V, N); 
    
    float metallic = texture(pbr_Material.metallic, ParallaxTexCoords).r;
    float roughness = texture(pbr_Material.roughness, ParallaxTexCoords).r;
    float ao = texture(pbr_Material.ambient_occlusion, ParallaxTexCoords).r;

    vec3 F0 = vec3(0.04); 
    F0 = mix(F0, albedo, metallic);

    vec3 Lo = vec3(0.0);
    for(int i = 0; i < 32; ++i) 
    {
        vec3 L = TBN * normalize(input_PointLights[i].position - WorldPos);
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
    
    // Will be added later.
    // vec3 irradiance = texture(skybox_SamplerCube, N).rgb;
	// vec3 diffuse = irradiance * albedo;
	// vec3 ambient = (kD * diffuse) * ao; 
    
	vec3 ambient = vec3(0.00001);
	
	vec3 color;
	
	if(selected == 0)
	{
    	color = Lo + ambient;
	} else
	{
    	color = N;
	}
    

    color = color / (color + vec3(1.0));
    Color = vec4(pow(color, vec3(1.0/2.2)), 1.0);
}
