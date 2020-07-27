package zin.exampleGame.core;

import org.lwjgl.glfw.GLFW;
import static org.lwjgl.opengl.GL11.*;

import zin.gammaEngine.core.Game;
import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.graphics.Display;
import zin.gammaEngine.graphics.components.ShaderComponent;
import zin.gammaEngine.graphics.utils.DisplayState;

public class ExampleGame extends Game
{

	public ExampleGame()
	{
		super("Example Game", 1280, 720, DisplayState.WINDOWED, 60);
	}

	@Override
	public void init()
	{
		ShaderComponent shader = new ShaderComponent("game_source/v.vsh", "game_source/f.fsh");
		addComponent(shader);
		
		addComponent(new GameComponent()
		{
			@Override
			public void render()
			{
				glLoadIdentity();//load identity matrix
				glBegin(GL_QUADS);// start drawing a line loop
				glVertex3f(-1.0f, 0.0f, 0.0f);// left of window
				glVertex3f(0.0f, -1.0f, 0.0f);// bottom of window
				glVertex3f(1.0f, 0.0f, 0.0f);// right of window
				glVertex3f(0.0f, 1.0f, 0.0f);// top of window
				glEnd();// end drawing of line loop
			}
		});
	}

	@Override
	public void loop()
	{
		if (Display.isKeyReleased(GLFW.GLFW_KEY_ESCAPE))
			requestClose();
	}

}
