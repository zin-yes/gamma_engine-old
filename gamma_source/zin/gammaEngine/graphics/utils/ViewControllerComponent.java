package zin.gammaEngine.graphics.utils;

import org.joml.Matrix4f;

import zin.gammaEngine.core.componentSystem.GameComponent;

public abstract class ViewControllerComponent extends GameComponent
{

	public abstract Matrix4f getTransformView();

}
