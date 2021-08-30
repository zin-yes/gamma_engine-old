package zin.gammaEngine.core;

import static org.lwjgl.glfw.GLFW.glfwGetVersionString;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import zin.gammaEngine.core.utils.Logger;
import zin.gammaEngine.experimental.postProcessing.Manager;
import zin.gammaEngine.graphics.Display;
import zin.gammaEngine.graphics.utils.ViewControllerComponent;

public class Engine
{

	private static final String GAMMA_VERSION = "0.0.20-SNAPSHOT";

	private Game game;

	private Matrix4f transform_ProjectionMatrix;
	private ViewControllerComponent viewController;

	private int frameRate, updateRate;
	private double frameTime, passedTime;

	private boolean running = false;

	private List<Integer> fRates = new ArrayList<>();
	private List<Integer> uRates = new ArrayList<>();

	public Manager manager;

	public Engine(Game game)
	{
		this.game = game;

		frameTime = 1.0 / game.getUPS();
	}

	public void start()
	{
		running = true;

		Display.create(game);
		resetTransformProjection();
		run();
	}

	public void run()
	{
		Logger.info("Gamma version: " + getGammaVersion());
		Logger.info("Engine starting.");
		Logger.info("Graphics Card Manufacturer: " + GL11.glGetString(GL11.GL_VENDOR));
		Logger.info("Graphics Card Name: " + GL11.glGetString(GL11.GL_RENDERER));
		Logger.info("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
		Logger.info("GLSL Version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		Logger.info("GLFW Version: " + glfwGetVersionString());
		Logger.info("LWJGL Version: " + Version.getVersion());

		game.init();
		game.getRootObject().init();

		if (manager != null)
			manager.init();

		Display.show();

		double lastTime = (double) System.nanoTime() / 1000000000L;
		double unprocessedTime = 0, frameCounter = 0;
		int updates = 0, frames = 0;

		while (running)
		{
			boolean render = false;

			double startTime = (double) System.nanoTime() / 1000000000L;
			passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime;
			frameCounter += passedTime;

			if (unprocessedTime > frameTime)
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
					Logger.info("Game FPS: " + frames + ", " + " Game UPS: " + updates + ".");
					fRates.add(frames);
					uRates.add(updates);
					frameRate = frames;
					updateRate = updates;
					frames = 0;
					updates = 0;
					frameCounter = 0;
				}
			}
			if (render)
			{
				if (manager == null)
				{
					render();
					frames++;
				} else
				{
					manager.render();
					frames++;
				}
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

	private void render()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		game.getRootObject().preRender();
		game.getRootObject().priorityRender();
		game.getRootObject().render();
		game.getRootObject().postRender();

		Display.update();
	}

	public void exit()
	{
		Display.hide();
		game.getRootObject().destroy();
		Display.destroy();

		float avgFPS = 0, avgUPS = 0;

		for (int value : fRates)
		{
			avgFPS += value;
		}

		for (int value : uRates)
		{
			avgUPS += value;
		}

		Logger.info(
				"Avg. FPS: " + (int) (avgFPS / fRates.size()) + ",  Avg. UPS: " + (int) (avgUPS / uRates.size()) + ".");
	}

	public Matrix4f getTransformProjection()
	{
		return transform_ProjectionMatrix;
	}

	public Matrix4f getTransformView()
	{
		if (viewController == null)
		{
			Logger.warn("No view controller found.");
			return new Matrix4f().identity();
		}

		return viewController.getTransformView();
	}

	public ViewControllerComponent getViewController()
	{
		return viewController;
	}

	public void setViewController(ViewControllerComponent viewController)
	{
		this.viewController = viewController;
	}

	public double getFrameTime()
	{
		return frameTime;
	}

	public void setFrameTime(double frameTime)
	{
		this.frameTime = frameTime;
	}

	public void resetTransformProjection()
	{
		transform_ProjectionMatrix = new Matrix4f().perspective((float) Math.toRadians(game.getFOV()),
				Display.getAspectRatio(), game.getZNear(), game.getZFar());
	}

	public Game getGame()
	{
		return game;
	}

	public String getGammaVersion()
	{
		return GAMMA_VERSION;
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