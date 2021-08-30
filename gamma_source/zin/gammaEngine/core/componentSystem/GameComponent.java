package zin.gammaEngine.core.componentSystem;

import java.util.ArrayList;
import java.util.List;

import zin.gammaEngine.core.utils.Logger;

public abstract class GameComponent
{

	private GameObject parent;
	private List<GameComponent> subComponents = new ArrayList<>();

	public boolean init()
	{
		for (int i = 0; i < subComponents.size(); i++)
		{
			if (!subComponents.get(i).init())
				subComponents.get(i).remove();
		}

		return true;
	}

	public void input()
	{
		for (GameComponent component : subComponents)
			component.input();
	}

	public void update()
	{
		for (GameComponent component : subComponents)
			component.update();
	}

	public void preRender()
	{
		for (GameComponent component : subComponents)
			component.preRender();
	}

	public void priorityRender()
	{
		for (GameComponent component : subComponents)
			component.priorityRender();
	}

	public void render()
	{
		for (GameComponent component : subComponents)
			component.render();
	}

	public void postRender()
	{
		for (GameComponent component : subComponents)
			component.postRender();
	}

	public void destroy()
	{
		for (GameComponent component : subComponents)
			component.destroy();
	}

	public void addSubComponent(GameComponent component)
	{
		subComponents.add(component);
	}

	public void removeSubComponent(GameComponent component)
	{
		if (!subComponents.contains(component))
		{
			Logger.error("You cannot remove a component that is not in the sub-component list.");
			return;
		}

		subComponents.remove(component);
	}

	public List<GameComponent> getSubComponents()
	{
		return subComponents;
	}

	public void setSubComponents(List<GameComponent> subComponents)
	{
		this.subComponents = subComponents;
	}

	public GameObject getParent()
	{
		return parent;
	}

	public void setParent(GameObject parent)
	{
		for (GameComponent component : subComponents)
		{
			component.setParent(parent);
		}

		this.parent = parent;
	}

	public void remove()
	{
		if (parent == null)
			throw new IllegalStateException("You cannot remove a component which does not belong to a parent object.");

		parent.getComponents().remove(this);

		destroy();
	}

}