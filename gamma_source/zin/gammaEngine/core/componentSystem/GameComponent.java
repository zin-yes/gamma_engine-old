package zin.gammaEngine.core.componentSystem;

import java.util.ArrayList;
import java.util.List;

public abstract class GameComponent
{

	private GameObject parent;
	private List<GameComponent> subComponents = new ArrayList<>();

	public void init()
	{
		for (GameComponent component : subComponents)
		{
			component.init();
		}
	}

	public void update()
	{
		for (GameComponent component : subComponents)
		{
			component.update();
		}
	}

	public void input()
	{
		for (GameComponent component : subComponents)
		{
			component.input();
		}
	}

	public void preRender()
	{
		for (GameComponent component : subComponents)
		{
			component.preRender();
		}
	}

	public void priorityRender()
	{
		for (GameComponent component : subComponents)
		{
			component.priorityRender();
		}
	}

	public void render()
	{
		for (GameComponent component : subComponents)
		{
			component.render();
		}
	}

	public void postRender()
	{
		for (GameComponent component : subComponents)
		{
			component.postRender();
		}
	}

	public void destroy()
	{
		for (GameComponent component : subComponents)
		{
			component.destroy();
		}
	}

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