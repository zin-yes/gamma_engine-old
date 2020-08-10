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

	private double frameCap;
	private boolean shouldClose = false, mipmapping = true, anisotropicFiltering = true;
	private float anisotropicAmount = 16, fov = 70, z_near = 0.1f, z_far = 1000.0f;

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

	/**
	 * Add an object to the root object.
	 */
	public void addObject(GameObject object)
	{
		getRootObject().addChild(object);
	}

	/**
	 * Add a component to the root object.
	 */
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

	public double getFrameCap()
	{
		return frameCap;
	}

	public void setFrameCap(double frameCap)
	{
		engine.setFrameTime(1.0 / frameCap);
		this.frameCap = frameCap;
	}

	/**
	 * Request for the entire program to exit.
	 */
	public void requestClose()
	{
		shouldClose = true;
	}

	/**
	 * @return Whether or not there is a request to exit the program (can be
	 *         requested both by user and program itself).
	 */
	public boolean shouldClose()
	{
		return shouldClose;
	}

	/**
	 * @return Whether or not mip-mapping is enabled for texture loading.
	 */
	public boolean getMipmapping()
	{
		return mipmapping;
	}

	/**
	 * Changes whether or not mip-mapping is allowed for future texture loads (set
	 * this setting preferably in the init() method and no later).
	 */
	public void setMipmapping(boolean mipmapping)
	{
		this.mipmapping = mipmapping;
	}

	/**
	 * @return Whether or not anisotropic-filtering is enabled for texture loading.
	 */
	public boolean getAnisotropicFiltering()
	{
		return anisotropicFiltering;
	}

	/**
	 * Changes whether or not anisotropic-filtering is allowed for future texture
	 * loads (set this setting preferably in the init() method and no later).
	 * 
	 * Mip-mapping has also got to be enabled for anisotropic-filtering to work.
	 */
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
