package zin.gammaEngine.graphics.components;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

import zin.gammaEngine.core.componentSystem.GameComponent;

public class MeshComponent extends GameComponent
{

	private int index;
	private int[] identifiers = new int[6];
	private int vertexArrayPointer;

	private float[][] data = new float[5][];
	private int[] indices;
	private int vertexCount;

	public MeshComponent(MeshComponent mesh)
	{
		identifiers = mesh.identifiers;
		vertexArrayPointer = mesh.vertexArrayPointer;
		data = mesh.data;
		vertexCount = mesh.vertexCount;
		indices = mesh.indices;
	}

	public MeshComponent(float[] vertices, float[] texCoords, float[] normals, float[] tangents, float[] bitangents,
			int[] indices)
	{
		data[0] = vertices;
		data[1] = texCoords;
		data[2] = normals;
		data[3] = tangents;
		data[4] = bitangents;

		this.indices = indices;
	}

	@Override
	public boolean init()
	{
		vertexCount = indices.length;

		IntBuffer indicesBuffer = null;

		vertexArrayPointer = glGenVertexArrays();
		glBindVertexArray(vertexArrayPointer);

		addBuffer(data[0], 3);
		addBuffer(data[1], 2);
		addBuffer(data[2], 3);
		addBuffer(data[3], 3);
		addBuffer(data[4], 3);

		identifiers[index] = glGenBuffers();
		indicesBuffer = MemoryUtil.memAllocInt(indices.length);
		indicesBuffer.put(indices).flip();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, identifiers[index]);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

		glBindVertexArray(0);

		return true;
	}

	public void addBuffer(float[] data, int elementLength)
	{
		identifiers[index] = glGenBuffers();

		FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
		buffer.put(data).flip();
		glBindBuffer(GL_ARRAY_BUFFER, identifiers[index]);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(index, elementLength, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		if (buffer != null)
			MemoryUtil.memFree(buffer);

		index++;
	}

	@Override
	public void render()
	{
		glBindVertexArray(vertexArrayPointer);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);

		glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glDisableVertexAttribArray(4);

		glBindVertexArray(0);
	}

	@Override
	public void destroy()
	{
		glDeleteVertexArrays(vertexArrayPointer);
		glDeleteBuffers(identifiers);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	public int getVertexArrayPointer()
	{
		return vertexArrayPointer;
	}

	public int getVertexCount()
	{
		return vertexCount;
	}

}