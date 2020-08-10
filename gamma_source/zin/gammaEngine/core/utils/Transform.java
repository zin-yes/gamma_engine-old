package zin.gammaEngine.core.utils;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform
{

	private Vector3f position, scale;
	private Quaternionf rotation;

	public Transform(Vector3f postion, Quaternionf rotation, Vector3f scale)
	{
		this.position = postion;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Transform(Transform src)
	{
		position = src.position;
		rotation = src.rotation;
		scale = src.scale;
	}

	public Transform()
	{
		position = new Vector3f();
		rotation = new Quaternionf();
		scale = new Vector3f(1);
	}

	public Vector3f getPosition()
	{
		return position;
	}

	public void setPosition(Vector3f position)
	{
		this.position = position;
	}

	public void setPosition(float x, float y, float z)
	{
		this.position = new Vector3f(x, y, z);
	}

	public Quaternionf getRotation()
	{
		return rotation;
	}

	public void setRotation(Quaternionf rotation)
	{
		this.rotation = rotation;
	}

	public void setRotation(float x, float y, float z, float w)
	{
		this.rotation = new Quaternionf(x, y, z, w);
	}

	public Vector3f getScale()
	{
		return scale;
	}

	public void setScale(Vector3f scale)
	{
		this.scale = scale;
	}

	public void setScale(float x, float y, float z)
	{
		this.scale = new Vector3f(x, y, z);
	}

	/**
	 * @return The position multiplied by the rotation multiplied by the scale.
	 */
	public Matrix4f getTransformModel()
	{
		Matrix4f position = new Matrix4f().translate(this.position);
		Matrix4f rotation = new Matrix4f().rotate(this.rotation);
		Matrix4f scale = new Matrix4f().scale(this.scale);

		return position.mul(rotation).mul(scale);
	}

}
