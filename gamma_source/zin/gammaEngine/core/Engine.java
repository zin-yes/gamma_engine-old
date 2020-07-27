package zin.gammaEngine.core;

import static org.lwjgl.glfw.GLFW.glfwGetVersionString;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import zin.gammaEngine.core.utils.Logger;
import zin.gammaEngine.graphics.Display;

public class Engine
{

	private static final String ENGINE_VERSION = "0.0.2-SNAPSHOT";

	private boolean running = false;

	private int frameRate, updateRate;

	private double frameTime;

	private Game game;

	public Engine(Game game)
	{
		this.game = game;

		frameTime = 1.0 / game.getFrameCap();
	}

	public void start()
	{
		running = true;

		Display.create(game);
		run();
	}

	public void run()
	{
		int updates = 0;
		int frames = 0;
		double frameCounter = 0;

		Logger.log("Engine version: " + getEngineVersion());
		Logger.log("Engine starting.");
		Logger.log("Graphics Card Manufacturer: " + GL11.glGetString(GL11.GL_VENDOR));
		Logger.log("Graphics Card Name: " + GL11.glGetString(GL11.GL_RENDERER));
		Logger.log("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
		Logger.log("GLSL Version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		Logger.log("GLFW Version: " + glfwGetVersionString());
		Logger.log("LWJGL Version: " + Version.getVersion());

		game.init();
		game.getRootObject().init();

		double lastTime = (double) System.nanoTime() / 1000000000L;
		double unprocessedTime = 0;

		while (running)
		{
			boolean render = false;

			double startTime = (double) System.nanoTime() / 1000000000L;
			double passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime;
			frameCounter += passedTime;

			while (unprocessedTime > frameTime)
			{
				render = true;

				unprocessedTime -= frameTime;

				if (game.shouldClose() || Display.shouldClose())
				{
					running = false;
					exit();
					break;
				}

				game.loop();
				
				game.getRootObject().input();

				updates++;
				game.getRootObject().update();

				if (frameCounter >= 1.0)
				{
					Logger.log("Game FPS: " + frames + ", " + " Game UPS: " + updates);
					frameRate = frames;
					updateRate = updates;
					frames = 0;
					updates = 0;
					frameCounter = 0;
				}
			}
			if (render)
			{
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
				game.getRootObject().render();
				Display.update();
				frames++;
			} else
			{
				try
				{
					Thread.sleep(1);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void exit()
	{
		game.getRootObject().destroy();
		Display.destroy();
	}

	public Game getGame()
	{
		return game;
	}

	public String getEngineVersion()
	{
		return ENGINE_VERSION;
	}

	public int getFrameRate()
	{
		return frameRate;
	}

	public int getUpdateRate()
	{
		return updateRate;
	}

}
