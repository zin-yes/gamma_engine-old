package zin.gammaEngine.core.componentSystem;

import java.util.ArrayList;
import java.util.List;

import zin.gammaEngine.core.Engine;
import zin.gammaEngine.core.Game;
import zin.gammaEngine.core.utils.Transform;

public class GameObject extends Transform
{

	private Game game;

	private GameObject parent;

	private List<GameObject> children;
	private List<GameComponent> components;

	public GameObject()
	{
		super();
		children = new ArrayList<>();
		components = new ArrayList<>();
	}

	public GameObject(GameObject src)
	{
		super();
		children = src.getChildren();
		components = src.getComponents();

		game = src.getGame();
	}

	public GameObject addChild(GameObject child)
	{
		getChildren().add(child);
		child.setParent(this);
		return this;
	}

	public GameObject addComponent(GameComponent component)
	{
		getComponents().add(component);
		component.setParent(this);
		return this;
	}

	public boolean init()
	{
		for (int i = 0; i < components.size(); i++)
		{
			if (!components.get(i).init())
				components.get(i).remove();
		}

		for (int i = 0; i < children.size(); i++)
		{
			if (!children.get(i).init())
				children.get(i).remove();
		}

		return true;
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

	public void preRender()
	{
		for (GameObject child : getChildren())
			child.preRender();
	}

	public void priorityRender()
	{
		for (GameObject child : getChildren())
			child.priorityRender();
	}

	public void render()
	{
		for (GameComponent component : getComponents())
			component.preRender();

		for (GameComponent component : getComponents())
			component.priorityRender();

		for (GameComponent component : getComponents())
			component.render();

		for (GameObject child : getChildren())
			child.render();

		for (GameComponent component : getComponents())
			component.postRender();
	}

	public void postRender()
	{
		for (GameObject child : getChildren())
			child.postRender();
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

	public GameObject getParent()
	{
		return parent;
	}

	private void setParent(GameObject parent)
	{
		game = parent.game;
		this.parent = parent;
	}

	public Game getGame()
	{
		return game;
	}

	public void setGame(Game game)
	{
		this.game = game;
	}

	public Engine getEngine()
	{
		return game.getEngine();
	}

	public void remove()
	{
		if (parent == null)
			throw new IllegalStateException("You cannot remove an object which does not belong to a parent object.");

		parent.getChildren().remove(this);

		destroy();
	}

}