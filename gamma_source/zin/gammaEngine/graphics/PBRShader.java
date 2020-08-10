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

		setUniform("material.albedo", 0);
		setUniform("material.normal", 1);
		setUniform("material.height", 2);
		setUniform("material.roughness", 3);
		setUniform("material.metallic", 4);
		setUniform("material.ambient_occlusion", 5);
		
		setUniform("skyblockMap", 16);

		return success;
	}

	@Override
	public void preRender()
	{
		super.preRender();

		for (int i = 0; i < pointLights.size(); i++)
		{
			PointLight light = pointLights.get(i);
			setUniform("pointLights[" + i + "].position", light.getPosition());
			setUniform("pointLights[" + i + "].color", light.getColor());
			setUniform("pointLights[" + i + "].attenuation", light.getAttenuation());
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

	public void setR(float r)
	{
		bind();
		setUniform("roughnes", r);
	}

	public void setM(float m)
	{
		bind();
		setUniform("metalli", m);
	}

}
