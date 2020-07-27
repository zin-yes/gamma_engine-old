package zin.gammaEngine.core.componentSystem;

public abstract class GameComponent
{

	private GameObject parent;

	public abstract void init();

	public abstract void update();

	public abstract void input();

	public abstract void render();

	public abstract void destroy();

	public void setParent(GameObject parent)
	{
		this.parent = parent;
	}

	public GameObject getParent()
	{
		return parent;
	}

	public void remove()
	{
		if (parent == null)
			throw new IllegalStateException("You cannot remove a component which does not belong to a parent object.");

		parent.getComponents().remove(this);
		
		destroy();
	}

}