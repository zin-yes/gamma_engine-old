package zin.gammaEngine.graphics.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import zin.gammaEngine.graphics.Display;
import zin.gammaEngine.graphics.utils.ViewControllerComponent;

public class FreeMoveComponent extends ViewControllerComponent
{

	public Vector3f forward, up, right;
	public float pitch, yaw;

	public FreeMoveComponent()
	{
		forward = new Vector3f(0, 0, 1);
		up = new Vector3f(0, 1, 0);
		right = new Vector3f();
	}

	@Override
	public boolean init()
	{
		getParent().getEngine().setViewController(this);
		return true;
	}

	@Override
	public void input()
	{
		Vector3f front = new Vector3f();
		front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		front.y = (float) (Math.sin(Math.toRadians(pitch)));
		front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		forward = front.normalize();
		right = new Vector3f(forward).cross(new Vector3f(0, 1, 0)).normalize();
		up = new Vector3f(right).cross(new Vector3f(forward)).normalize();

		float cameraSpeed = (float) (10 * getParent().getEngine().getFrameTime());
		yaw += Display.getDeltaMouse().x * 100 * getParent().getEngine().getFrameTime();
		pitch -= Display.getDeltaMouse().y * 100 * getParent().getEngine().getFrameTime();
		pitch = Math.min(Math.max(pitch, -90f), 90f);

		if (yaw > 360.0f)
			yaw = -360.0f;
		if (yaw < -360.0f)
			yaw = 360.0f;

		if (Display.isKeyDown(GLFW.GLFW_KEY_W) || Display.isKeyDown(GLFW.GLFW_KEY_ENTER))
			getParent().getPosition().add(new Vector3f(forward).mul(cameraSpeed));
		if (Display.isKeyDown(GLFW.GLFW_KEY_S))
			getParent().getPosition().sub(new Vector3f(forward).mul(cameraSpeed));
		if (Display.isKeyDown(GLFW.GLFW_KEY_A))
			getParent().getPosition().sub(new Vector3f(right).mul(cameraSpeed));
		if (Display.isKeyDown(GLFW.GLFW_KEY_D))
			getParent().getPosition().add(new Vector3f(right).mul(cameraSpeed));
		if (Display.isKeyDown(GLFW.GLFW_KEY_SPACE))
			getParent().getPosition().add(new Vector3f(up).mul(cameraSpeed * 0.2f));
		if (Display.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			getParent().getPosition().add(new Vector3f(new Vector3f(up).negate()).mul(cameraSpeed * 0.2f));
	}

	public Matrix4f getTransformView()
	{
		return new Matrix4f().lookAt(getParent().getPosition(),
				new Vector3f(forward).add(new Vector3f(getParent().getPosition())), up);
	}

}
