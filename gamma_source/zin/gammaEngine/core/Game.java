package zin.gammaEngine.core;

import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.core.componentSystem.GameObject;
import zin.gammaEngine.graphics.utils.DisplayState;

public abstract class Game
{

	private boolean shouldClose = false;

	private String title;
	private DisplayState state;
	private int width, height;

	private double frameCap;

	private GameObject root;
	private Engine engine;

	public Game(String title, int width, int height, DisplayState state, double frameCap)
	{
		this.title = title;
		this.width = width;
		this.height = height;
		this.state = state;
		this.frameCap = frameCap;

		engine = new Engine(this);

		engine.start();
	}

	public abstract void init();

	public abstract void loop();

	public GameObject getNewObject()
	{
		return new GameObject(this);
	}

	public void addObject(GameObject object)
	{
		getRootObject().addChild(object);
	}

	public void addComponent(GameComponent component)
	{
		getRootObject().addComponent(component);
	}

	public GameObject getRootObject()
	{
		if (root == null)
			root = new GameObject(this);

		return root;
	}

	public Engine getEngine()
	{
		return engine;
	}

	public String getDisplayTitle()
	{
		return title;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public DisplayState getState()
	{
		return state;
	}

	public double getFrameCap()
	{
		return frameCap;
	}

	public void requestClose()
	{
		shouldClose = true;
	}

	public boolean shouldClose()
	{
		return shouldClose;
	}

}
