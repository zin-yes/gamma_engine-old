package zin.gammaEngine.graphics;

import java.util.ArrayList;
import java.util.List;

import zin.gammaEngine.graphics.utils.PointLight;
import zin.gammaEngine.graphics.utils.ShaderComponent;

public class PBRShader extends ShaderComponent
{

	private static List<PointLight> pointLights = new ArrayList<>();

	public PBRShader()
	{
		super("gamma_resources/shaders/pbr/pbr.vsh", "gamma_resources/shaders/pbr/pbr.fsh");
	}

	@Override
	public boolean init()
	{
		boolean success = super.init();

		setUniform("pbr_Material.albedo", 0);
		setUniform("pbr_Material.normal", 1);
		setUniform("pbr_Material.height", 2);
		setUniform("pbr_Material.roughness", 3);
		setUniform("pbr_Material.metallic", 4);
		setUniform("pbr_Material.ambient_occlusion", 5);
		
		setUniform("skybox_SamplerCube", 16);
		
		return success;
	}

	@Override
	public void preRender()
	{
		super.preRender();

		for (int i = 0; i < pointLights.size(); i++)
		{
			PointLight light = pointLights.get(i);
			setUniform("input_PointLights[" + i + "].position", light.getPosition());
			setUniform("input_PointLights[" + i + "].color", light.getColor());
			setUniform("input_PointLights[" + i + "].attenuation", light.getAttenuation());
		}
	}

	@Override
	public void postRender()
	{
		unbind();
	}

	public static void addPointLight(PointLight pointLight)
	{
		pointLights.add(pointLight);
	}

}
