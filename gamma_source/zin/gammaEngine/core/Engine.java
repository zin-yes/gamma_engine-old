package zin.gammaEngine.core;

import zin.gammaEngine.graphics.Display;

public class Engine
{

	private static final String ENGINE_VERSION = "0.0.1-SNAPSHOT";

	private boolean running = false;

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

		game.init();

		if (true)
		{
			System.out.println("Engine version: " + getEngineVersion());
			System.out.println("Engine loop starting.");
		}

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

				game.getRootObject().input();

				updates++;
				game.getRootObject().update();

				game.loop();

				if (frameCounter >= 1.0)
				{
					System.out.println("Game FPS: " + frames + ", " + " Game UPS: " + updates);
					updates = 0;
					frames = 0;
					frameCounter = 0;
				}
			}
			if (render)
			{
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
		Display.destroy();
		game.getRootObject().destroy();
	}

	public Game getGame()
	{
		return game;
	}

	public String getEngineVersion()
	{
		return ENGINE_VERSION;
	}

}
