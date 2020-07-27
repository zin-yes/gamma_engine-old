package zin.exampleGame.core;

import org.lwjgl.glfw.GLFW;

import zin.gammaEngine.core.Game;
import zin.gammaEngine.graphics.Display;
import zin.gammaEngine.graphics.utils.DisplayState;

public class ExampleGame extends Game
{

	public ExampleGame()
	{
		super("Example Game", 1280, 720, DisplayState.WINDOWED, 5000);
	}

	@Override
	public void init()
	{

	}

	@Override
	public void loop()
	{
		if (GLFW.glfwGetKey(Display.getIdentifier(), GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_TRUE)
			requestClose();
	}

}
