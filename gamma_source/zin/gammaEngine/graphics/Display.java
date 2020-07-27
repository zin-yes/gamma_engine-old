package zin.gammaEngine.graphics;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import zin.gammaEngine.core.Game;

public class Display
{

	private static long identifier;

	public static void create(Game game)
	{
		if (!glfwInit())
			throw new IllegalStateException("GLFW failed to initialize.");

		glfwWindowHint(GLFW_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_VERSION_MINOR, 3);

		if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0)
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

		switch (game.getState())
		{
		case WINDOWED:
			identifier = glfwCreateWindow(game.getWidth(), game.getHeight(), game.getDisplayTitle(), 0, 0);
			break;
		case BORDERLESS:
			glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
			identifier = glfwCreateWindow(game.getWidth(), game.getHeight(), game.getDisplayTitle(), 0, 0);
			break;
		case FULLSCREEN:
			identifier = glfwCreateWindow(game.getWidth(), game.getHeight(), game.getDisplayTitle(),
					glfwGetPrimaryMonitor(), 0);
			break;

		default:
			break;
		}

		glfwMakeContextCurrent(identifier);
		GL.createCapabilities();

//		Logger.info("Graphics Card Manufacturer: " + GL11.glGetString(GL11.GL_VENDOR));
//		Logger.info("Graphics Card Name: " + GL11.glGetString(GL11.GL_RENDERER));
//		Logger.info("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
//		Logger.info("GLSL Version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
//		Logger.info("GLFW Version: " + GLFW.glfwGetVersionString());
//		Logger.info("LWJGL Version: " + Version.getVersion());
//		System.out.println("\n");

		GL11.glEnable(GL30.GL_FRAMEBUFFER_SRGB);
		GL11.glEnable(GL30.GL_MULTISAMPLE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

		// callbacks
		// monitor info
		// transparency
		// focus
		// input
	}

	public static void update()
	{
		glfwPollEvents();
	}

	public static void destroy()
	{
		glfwDestroyWindow(identifier);
	}

	public static boolean shouldClose()
	{
		return glfwWindowShouldClose(identifier);
	}

	public static long getIdentifier()
	{
		return identifier;
	}

}
