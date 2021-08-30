package zin.gammaEngine.core;

import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.core.componentSystem.GameObject;
import zin.gammaEngine.graphics.utils.DisplayState;

public abstract class Game
{

	private GameObject root;
	private Engine engine;

	private String title;
	private int width, height;
	private DisplayState state;

	private double ups;
	private boolean shouldClose = false, mipmapping = true, anisotropicFiltering = true;
	private float anisotropicAmount = 16, fov = 70, z_near = 0.1f, z_far = 1000.0f;

	public Game(String title, int width, int height, DisplayState state, double ups)
	{
		this.title = title;
		this.width = width;
		this.height = height;
		this.state = state;
		this.ups = ups;

		engine = new Engine(this);

		engine.start();
	}

	public abstract void init();

	public abstract void loop();

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
			root = new GameObject();

		root.setGame(this);

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

	public double getUPS()
	{
		return ups;
	}

	public void setUPS(double ups)
	{
		engine.setFrameTime(1.0 / ups);
		this.ups = ups;
	}

	public void requestClose()
	{
		shouldClose = true;
	}

	public boolean shouldClose()
	{
		return shouldClose;
	}

	public boolean getMipmapping()
	{
		return mipmapping;
	}

	public void setMipmapping(boolean mipmapping)
	{
		this.mipmapping = mipmapping;
	}

	public boolean getAnisotropicFiltering()
	{
		return anisotropicFiltering;
	}

	public void setAnisotropicFiltering(boolean anisotropicFiltering)
	{
		this.anisotropicFiltering = anisotropicFiltering;
	}

	public float getAnisotropicFilteringAmount()
	{
		return anisotropicAmount;
	}

	public void setAnisotropicAmount(float anisotropicAmount)
	{
		this.anisotropicAmount = anisotropicAmount;
	}

	public float getFOV()
	{
		return fov;
	}

	public void setFOV(float fov)
	{
		engine.resetTransformProjection();
		this.fov = fov;
	}

	public float getZFar()
	{
		return z_far;
	}

	public void setZFar(float z_far)
	{
		engine.resetTransformProjection();
		this.z_far = z_far;
	}

	public float getZNear()
	{
		return z_near;
	}

	public void setZNear(float z_near)
	{
		engine.resetTransformProjection();
		this.z_near = z_near;
	}

}
