package zin.gammaEngine.graphics;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.DoubleBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import zin.gammaEngine.core.Game;
import zin.gammaEngine.core.utils.Logger;

public class Display
{

	private static long identifier;

	private static boolean currentKeys[];
	private static boolean currentButtons[];
	private static Vector2f scrollWheel = new Vector2f();

	private static boolean grabbed, focused;

	private static double newX, newY, prevX, prevY, dx, dy;
	private static boolean rotX, rotY;

	private static int width, height;

	private static float opacity;

	public static void create(Game game)
	{
		if (!glfwInit())
		{
			Logger.error("Failed to initialize GLFW. Exiting.");
			System.exit(-1);
		}

		glfwWindowHint(GLFW_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_VERSION_MINOR, 3);

		if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0)
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

		switch (game.getState())
		{
		case WINDOWED:
			identifier = glfwCreateWindow(game.getWidth(), game.getHeight(), game.getDisplayTitle(), 0, 0);
			glfwSetFramebufferSizeCallback(identifier, new GLFWFramebufferSizeCallback()
			{
				public void invoke(long identifier, int width, int height)
				{
					Display.width = width;
					Display.height = height;
					GL11.glViewport(0, 0, width, height);
					// GraphicsEngine.updateProjectionMatrix();
				}
			});
			break;
		case BORDERLESS:
			glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
			game.setWidth(getMonitorWidth());
			game.setHeight(getMonitorHeight());
			identifier = glfwCreateWindow(width, height, game.getDisplayTitle(), 0, 0);

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

		glfwSwapInterval(0);

		glfwMakeContextCurrent(identifier);
		GL.createCapabilities();

		GL11.glEnable(GL30.GL_FRAMEBUFFER_SRGB);
		GL11.glEnable(GL30.GL_MULTISAMPLE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

		GL11.glViewport(0, 0, game.getWidth(), game.getHeight());

		glfwSetScrollCallback(identifier, new GLFWScrollCallback()
		{
			public void invoke(long identifier, double xOffset, double yOffset)
			{
				Display.scrollWheel.x += (float) xOffset;
				Display.scrollWheel.y += (float) yOffset;
			}
		});

		glfwSetWindowFocusCallback(identifier, new GLFWWindowFocusCallback()
		{
			public void invoke(long identifier, boolean focused)
			{
				Display.focused = focused;
			}
		});

		currentKeys = new boolean[GLFW_KEY_LAST];
		for (int i = 0; i < GLFW_KEY_LAST; i++)
			currentKeys[i] = false;

		currentButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];
		for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++)
			currentButtons[i] = false;

		newX = Display.getWidth() / 2;
		newY = Display.getHeight() / 2;

		prevX = 0;
		prevY = 0;

		rotX = false;
		rotY = false;

		// callbacks
		// monitor info
		// transparency
		// focus
		// input
	}

	private static void updateInput()
	{
		for (int i = 32; i < GLFW_KEY_LAST; i++)
			currentKeys[i] = isKeyDown(i);

		for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++)
			currentButtons[i] = isMouseButtonDown(i);

		if (grabbed)
		{
			DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer y = BufferUtils.createDoubleBuffer(1);

			glfwGetCursorPos(Display.getIdentifier(), x, y);
			x.rewind();
			y.rewind();

			newX = x.get();
			newY = y.get();

			double deltaX = newX - Display.getWidth() / 2;
			double deltaY = newY - Display.getHeight() / 2;

			rotX = newX != prevX;
			rotY = newY != prevY;

			if (rotX)
			{
				dx = deltaX;
			}
			if (rotY)
			{
				dy = deltaY;
			}

			prevX = newX;
			prevY = newY;

			glfwSetCursorPos(Display.getIdentifier(), Display.getWidth() / 2, Display.getHeight() / 2);
		} else
		{
			dy = dx = 0;
		}
	}

	public static void update()
	{
		updateInput();
		glfwPollEvents();
		glfwSwapBuffers(identifier);
	}

	public static void destroy()
	{
		glfwDestroyWindow(identifier);
		glfwTerminate();
	}

	public static int getWidth()
	{
		return width;
	}

	public static int getHeight()
	{
		return height;
	}

	public static int getMonitorWidth()
	{
		return glfwGetVideoMode(glfwGetPrimaryMonitor()).width();
	}

	public static int getMonitorHeight()
	{
		return glfwGetVideoMode(glfwGetPrimaryMonitor()).height();
	}

	public static int getMonitorRefreshRate()
	{
		return glfwGetVideoMode(glfwGetPrimaryMonitor()).refreshRate();
	}

	public static Vector2f getScrollWheel()
	{
		return scrollWheel;
	}

	public static boolean isKeyDown(int keyCode)
	{
		return glfwGetKey(Display.getIdentifier(), keyCode) == 1;
	}

	public static boolean isKeyPressed(int keyCode)
	{
		return (isKeyDown(keyCode) && !currentKeys[keyCode]);
	}

	public static boolean isKeyReleased(int keyCode)
	{
		return (!isKeyDown(keyCode) && currentKeys[keyCode]);
	}

	public static boolean isMouseButtonDown(int keyCode)
	{
		return glfwGetMouseButton(Display.getIdentifier(), keyCode) == 1;
	}

	public static boolean isMouseButtonPressed(int keyCode)
	{
		return (isMouseButtonDown(keyCode) && !currentButtons[keyCode]);
	}

	public static boolean isMouseButtonReleased(int keyCode)
	{
		return (!isMouseButtonDown(keyCode) && currentButtons[keyCode]);
	}

	public static boolean cursorIsGrabbed()
	{
		return grabbed;
	}

	public static void setCursorGrabbed(boolean grabbed)
	{
		Display.grabbed = grabbed;

		if (grabbed)
		{
			glfwSetInputMode(Display.getIdentifier(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		} else
		{
			glfwSetInputMode(Display.getIdentifier(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		}
		glfwSetCursorPos(Display.getIdentifier(), Display.getWidth() / 2, Display.getHeight() / 2);
	}

	public static boolean isFocused()
	{
		return focused;
	}

	public static float getOpacity()
	{
		return opacity;
	}

	public static void setOpactiy(float value)
	{
		glfwSetWindowOpacity(identifier, value);
		opacity = value;
	}

	public static Vector2f getDeltaMouse()
	{
		return new Vector2f((float) dx, (float) dy);
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
