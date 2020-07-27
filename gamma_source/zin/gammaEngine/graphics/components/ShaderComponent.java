package zin.gammaEngine.graphics.components;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.core.utils.Logger;

public class ShaderComponent extends GameComponent
{

	public static Map<String, Integer> cachedShaders = new HashMap<>();

	public int identifier;
	public Map<String, Integer> uniforms = new HashMap<>();
	private String vertexPath, fragmentPath;

	public ShaderComponent(String vertexPath, String fragmentPath)
	{
		this.vertexPath = vertexPath;
		this.fragmentPath = fragmentPath;
	}

	public void setAttribute(int index, String name)
	{
		glBindAttribLocation(identifier, index, name);
	}

	public boolean exists(String name)
	{
		return uniforms.get(name) != null;
	}

	public void addUniform(String name)
	{
		uniforms.put(name, glGetUniformLocation(identifier, name));
	}

	public void setUniform(String name, Vector2f value)
	{
		if (!exists(name))
			addUniform(name);

		bind();
		glUniform2f(getUniform(name), value.x, value.y);
	}

	public void setUniform(String name, Vector3f value)
	{
		if (!exists(name))
			addUniform(name);

		bind();
		glUniform3f(getUniform(name), value.x, value.y, value.z);
	}

	public void setUniform(String name, Matrix4f value)
	{
		if (!exists(name))
			addUniform(name);

		bind();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		value.get(buffer);
		glUniformMatrix4fv(getUniform(name), false, buffer);
	}

	public void setUniform(String name, float value)
	{
		if (!exists(name))
			addUniform(name);

		bind();
		glUniform1f(getUniform(name), value);
	}

	public void setUniform(String name, int value)
	{
		if (!exists(name))
			addUniform(name);

		bind();
		glUniform1i(getUniform(name), value);
	}

	public int getUniform(String name)
	{
		return uniforms.get(name);
	}

	public void bind()
	{
		glUseProgram(identifier);
	}

	public static void unbind()
	{
		glUseProgram(0);
	}

	@Override
	public void init()
	{
		if (cachedShaders.containsKey(vertexPath + fragmentPath))
		{
			identifier = cachedShaders.get(vertexPath + fragmentPath);
			return;
		}

		int vertexShader = loadShader(vertexPath, GL_VERTEX_SHADER);
		int fragmentShader = loadShader(fragmentPath, GL_FRAGMENT_SHADER);

		identifier = glCreateProgram();
		glAttachShader(identifier, vertexShader);
		glAttachShader(identifier, fragmentShader);
		glLinkProgram(identifier);

		glDetachShader(identifier, vertexShader);
		glDetachShader(identifier, fragmentShader);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);

		cachedShaders.put(vertexPath + fragmentPath, identifier);

		setAttribute(0, "Position");
	}

	private int loadShader(String path, int shaderType)
	{
		StringBuilder source = new StringBuilder();
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String line;
			while ((line = reader.readLine()) != null)
			{
				source.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e)
		{
			Logger.error("Failed to load fragment shader \"" + fragmentPath + "\".");
			return 0;
		}

		int identifier = glCreateShader(shaderType);
		glShaderSource(identifier, source);
		glCompileShader(identifier);
		if (glGetShaderi(identifier, GL_COMPILE_STATUS) == GL_FALSE)
		{
			System.out.println(glGetShaderInfoLog(identifier, 1024));
			Logger.error("Failed to compile fragment shader \"" + fragmentPath + "\".");
			return 0;
		}

		return identifier;
	}

	@Override
	public void preRender()
	{
		bind();
	}

	@Override
	public void postRender()
	{
		unbind();
	}

	@Override
	public void destroy()
	{
		unbind();
		glDeleteProgram(identifier);
	}

	public String getVertexPath()
	{
		return vertexPath;
	}

	public String getFragmentPath()
	{
		return fragmentPath;
	}

	public int getIdentifier()
	{
		return identifier;
	}

}