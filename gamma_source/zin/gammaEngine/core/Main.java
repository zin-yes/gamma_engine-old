
package zin.gammaEngine.core;

import zin.exampleGame.core.ExampleGame;

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
