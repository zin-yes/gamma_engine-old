package zin.exampleGame.core;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import zin.gammaEngine.core.Game;
import zin.gammaEngine.core.componentSystem.GameObject;
import zin.gammaEngine.graphics.Display;
import zin.gammaEngine.graphics.PBRShader;
import zin.gammaEngine.graphics.components.FreeMoveComponent;
import zin.gammaEngine.graphics.components.ModelComponent;
import zin.gammaEngine.graphics.components.SkyboxComponent;
import zin.gammaEngine.graphics.components.TextureComponent;
import zin.gammaEngine.graphics.utils.DisplayState;
import zin.gammaEngine.graphics.utils.PointLight;
import zin.gammaEngine.graphics.utils.TextureType;

public class ExampleGame extends Game
{

	public ExampleGame()
	{
		super("Example Game", 1280, 720, DisplayState.WINDOWED, 5000);
	}

	PBRShader shader;
	PointLight light;

	@Override
	public void init()
	{
		final GameObject camera = new GameObject().addComponent(new FreeMoveComponent());
		addObject(camera);

		shader = new PBRShader();

		String texture = "harshbricks-";
		String textureExt = "png";

		light = new PointLight(new Vector3f(0, 0, 0), new Vector3f(100), new Vector3f());
		PBRShader.addPointLight(light);

		ModelComponent model = new ModelComponent("game_resources/plane.obj");
		model.addSubComponent(
				new TextureComponent("game_resources/" + texture + "albedo." + textureExt, TextureType.ALBEDO));
		model.addSubComponent(
				new TextureComponent("game_resources/" + texture + "normal." + textureExt, TextureType.NORMAL));
		model.addSubComponent(
				new TextureComponent("game_resources/" + texture + "height." + textureExt, TextureType.HEIGHT));
		model.addSubComponent(
				new TextureComponent("game_resources/" + texture + "metallic." + textureExt, TextureType.METALLIC));
		model.addSubComponent(
				new TextureComponent("game_resources/" + texture + "roughness." + textureExt, TextureType.ROUGHNESS));
		model.addSubComponent(
				new TextureComponent("game_resources/" + texture + "ao." + textureExt, TextureType.AMBIENT_OCCLUSION));

		GameObject modelObj = new GameObject();
		addObject(modelObj.addComponent(model).addComponent(shader));

		addComponent(new SkyboxComponent(new String[]
		{ "game_resources/skybox/zpos.jpg", "game_resources/skybox/zneg.jpg", "game_resources/skybox/ypos.jpg",
				"game_resources/skybox/yneg.jpg", "game_resources/skybox/xpos.jpg",
				"game_resources/skybox/xneg.jpg" }));

//		addObject(new Blueprint("game_resources/monkey.gmf", BlueprintType.GAMMA_MODEL_FILE).constructObject());
	}

	float r = 0;
	float m = 0;

	@Override
	public void loop()
	{
		if (Display.isKeyReleased(GLFW.GLFW_KEY_ESCAPE))
			requestClose();

		if (Display.isKeyDown(GLFW.GLFW_KEY_UP))
			light.getPosition().sub(0, 0, 0.001f);
		if (Display.isKeyDown(GLFW.GLFW_KEY_DOWN))
			light.getPosition().add(0, 0, 0.001f);
		if (Display.isKeyDown(GLFW.GLFW_KEY_RIGHT))
			light.getPosition().add(0.001f, 0, 0);
		if (Display.isKeyDown(GLFW.GLFW_KEY_LEFT))
			light.getPosition().sub(0.001f, 0, 0);

		if (Display.isKeyDown(GLFW.GLFW_KEY_PAGE_UP))
			light.getPosition().add(0, 0.001f, 0);
		if (Display.isKeyDown(GLFW.GLFW_KEY_PAGE_DOWN))
			light.getPosition().sub(0, 0.001f, 0);

		if (Display.isKeyDown(GLFW.GLFW_KEY_B))
			light.getColor().add(new Vector3f(0.5f));
		if (Display.isKeyDown(GLFW.GLFW_KEY_V))
			light.getColor().sub(new Vector3f(0.5f));

		if (Display.isKeyReleased(GLFW.GLFW_KEY_LEFT_ALT))
			Display.setCursorGrabbed(!Display.cursorIsGrabbed());
	}

}
