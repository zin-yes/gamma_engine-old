package zin.exampleGame.core;

import zin.gammaEngine.core.Game;

public class Main
{

	private static Game game;

	public static void main(String[] args)
	{
		game = new ExampleGame();
	}

	public static Game getGame()
	{
		return game;
	}

}
