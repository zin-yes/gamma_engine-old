package zin.exampleGame.core;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import zin.gammaEngine.core.Game;
import zin.gammaEngine.core.componentSystem.GameObject;
import zin.gammaEngine.experimental.blueprint.Blueprint;
import zin.gammaEngine.experimental.blueprint.BlueprintType;
import zin.gammaEngine.experimental.postProcessing.FrameBufferObject;
import zin.gammaEngine.experimental.postProcessing.Manager;
import zin.gammaEngine.graphics.Display;
import zin.gammaEngine.graphics.PBRShader;
import zin.gammaEngine.graphics.components.FreeMoveComponent;
import zin.gammaEngine.graphics.components.ShaderComponent;
import zin.gammaEngine.graphics.components.SkyboxComponent;
import zin.gammaEngine.graphics.utils.DisplayState;
import zin.gammaEngine.graphics.utils.PointLight;

public class ExampleGame extends Game
{

	public ExampleGame()
	{
		super("Example Game", 1280, 720, DisplayState.WINDOWED, 240);
		setFOV(120);
	}

	PBRShader shader;
	PointLight light;
	GameObject obj;
	ShaderComponent fboshader;

	@Override
	public void init()
	{
		final GameObject camera = new GameObject().addComponent(new FreeMoveComponent());
		addObject(camera);
		shader = new PBRShader();

		light = new PointLight(new Vector3f(0, 2, 0), new Vector3f(100), new Vector3f());
		PBRShader.addPointLight(light);

		addComponent(new SkyboxComponent(new String[]
		{ "game_resources/skybox/zpos.jpg", "game_resources/skybox/zneg.jpg", "game_resources/skybox/ypos.jpg",
				"game_resources/skybox/yneg.jpg", "game_resources/skybox/xpos.jpg",
				"game_resources/skybox/xneg.jpg" }));

		obj = new Blueprint("game_resources/monkey.gmf", BlueprintType.GAMMA_MODEL_FILE).constructObject();

		addObject(obj);

		Manager m = new Manager(this);

		fboshader = new ShaderComponent("game_resources/fb.vsh", "game_resources/fb.fsh")
		{
			@Override
			public boolean init()
			{
				bind();
				setUniform("color", 0);
				return super.init();
			}
		};

		FrameBufferObject fbo = new FrameBufferObject(this, fboshader);

		m.addFrameBufferObject(fbo);
	}

	@Override
	public void loop()
	{
//		obj.getRotation().rotateX((float) Math.toRadians(.1f));
		
		if (Display.isKeyDown(GLFW.GLFW_KEY_R))
			fboshader.reload();
		if (Display.isKeyDown(GLFW.GLFW_KEY_HOME))
			shader.setHeightScale(shader.getHeightScale() + 0.001f);
		if (Display.isKeyDown(GLFW.GLFW_KEY_END))
			shader.setHeightScale(shader.getHeightScale() - 0.001f);
		if (Display.isKeyReleased(GLFW.GLFW_KEY_ESCAPE))
			requestClose();

		if (Display.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT))
			shader.setSelected(!shader.isSelected());

		if (Display.isKeyDown(GLFW.GLFW_KEY_UP))
			light.getPosition().sub(0, 0, 0.01f);
		if (Display.isKeyDown(GLFW.GLFW_KEY_DOWN))
			light.getPosition().add(0, 0, 0.01f);
		if (Display.isKeyDown(GLFW.GLFW_KEY_RIGHT))
			light.getPosition().add(0.01f, 0, 0);
		if (Display.isKeyDown(GLFW.GLFW_KEY_LEFT))
			light.getPosition().sub(0.01f, 0, 0);

		if (Display.isKeyDown(GLFW.GLFW_KEY_PAGE_UP))
			light.getPosition().add(0, 0.01f, 0);
		if (Display.isKeyDown(GLFW.GLFW_KEY_PAGE_DOWN))
			light.getPosition().sub(0, 0.01f, 0);

		if (Display.isKeyReleased(GLFW.GLFW_KEY_LEFT_ALT))
			Display.setCursorGrabbed(!Display.cursorIsGrabbed());
	}

}
