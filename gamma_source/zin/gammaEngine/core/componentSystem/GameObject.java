package zin.gammaEngine.core.componentSystem;

import java.util.ArrayList;
import java.util.List;

import zin.gammaEngine.core.utils.Transform;

public class GameObject extends Transform
{

	private GameObject parent;

	private List<GameObject> children;
	private List<GameComponent> components;

	public GameObject()
	{
		super();
		children = new ArrayList<>();
		components = new ArrayList<>();
	}

	public GameObject addChild(GameObject child)
	{
		getChildren().add(child);
		return this;
	}

	public GameObject addComponent(GameComponent component)
	{
		getComponents().add(component);
		component.setParent(this);
		return this;
	}

	public void init()
	{
		for (GameComponent component : getComponents())
			component.init();
		
		for (GameObject child : getChildren())
			child.init();
	}

	public void input()
	{
		for (GameComponent component : getComponents())
			component.input();
		
		for (GameObject child : getChildren())
			child.input();
	}

	public void update()
	{
		for (int i = 0; i < getComponents().size(); i++)
			getComponents().get(i).update();
		
		for (int i = 0; i < getChildren().size(); i++)
			getChildren().get(i).update();
	}

	public void render()
	{
		for (int i = 0; i < getComponents().size(); i++)
			getComponents().get(i).render();
		
		for (int i = 0; i < getChildren().size(); i++)
			getChildren().get(i).render();
	}

	public void destroy()
	{
		for (GameComponent component : getComponents())
			component.destroy();
		
		for (GameObject child : getChildren())
			child.destroy();
	}

	public List<GameObject> getChildren()
	{
		return children;
	}

	public void setChildren(List<GameObject> children)
	{
		this.children = children;
	}

	public List<GameComponent> getComponents()
	{
		return components;
	}

	public void setComponents(List<GameComponent> components)
	{
		this.components = components;
	}

	public void remove()
	{
		if (parent == null)
			throw new IllegalStateException("You cannot remove an object which does not belong to a parent object.");

		parent.getChildren().remove(this);
		
		destroy();
	}

}