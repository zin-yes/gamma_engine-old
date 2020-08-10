package zin.gammaEngine.graphics.components;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.graphics.utils.ShaderComponent;

public class SkyboxComponent extends GameComponent
{

	private int vaoID, vboID, vertexCount;

	private static final float SIZE = 500;
	private static final float[] VERTICES =
	{ -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE,
			-SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE,
			-SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE,
			-SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE,
			-SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE,
			-SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE,
			-SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE };

	private TextureComponent cubeMap;
	private ShaderComponent shader;

	public SkyboxComponent(String[] textures)
	{
		super();
		cubeMap = new TextureComponent(textures);
		shader = new ShaderComponent("gamma_resources/shaders/skybox/skybox.vsh",
				"gamma_resources/shaders/skybox/skybox.fsh")
		{
		};

		addSubComponent(shader);
		addSubComponent(cubeMap);
	}

	@Override
	public boolean init()
	{
		vertexCount = VERTICES.length / 3;

		FloatBuffer verticesBuffer = null;

		try
		{
			vaoID = glGenVertexArrays();
			glBindVertexArray(vaoID);

			vboID = glGenBuffers();
			verticesBuffer = MemoryUtil.memAllocFloat(VERTICES.length);
			verticesBuffer.put(VERTICES).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboID);
			glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		} finally
		{
			if (verticesBuffer != null)
				MemoryUtil.memFree(verticesBuffer);
		}

		return super.init();
	}

	@Override
	public void priorityRender()
	{
		shader.bind();
		GL13.glActiveTexture(GL13.GL_TEXTURE16);
		GL13.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubeMap.getIdentifier());
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		ShaderComponent.unbind();
	}

	@Override
	public void destroy()
	{
		GL20.glDeleteBuffers(vboID);
		GL30.glDeleteVertexArrays(vaoID);
		super.destroy();
	}

}
