package zin.gammaEngine.experimental.postProcessing;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import zin.gammaEngine.core.Game;
import zin.gammaEngine.core.componentSystem.GameObject;
import zin.gammaEngine.graphics.Display;
import zin.gammaEngine.graphics.components.ShaderComponent;

public class FrameBufferObject extends GameObject
{
	private int identifier, textureID, renderID;
	private FrameBufferObject previous;

	private ShaderComponent customShader;

	private boolean render = true;

	float[] vertices =
	{ -1, -1, 1, -1, 1, 1, -1, 1 };

	Game game;

	public FrameBufferObject(Game game, ShaderComponent shader)
	{
		customShader = shader;
	}

	@Override
	public boolean init()
	{
		customShader.init();
		ShaderComponent.unbind();

		identifier = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, identifier);

		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Display.getWidth(), Display.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glBindTexture(GL_TEXTURE_2D, 0);

		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureID, 0);

		renderID = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, renderID);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, Display.getWidth(), Display.getHeight());
		glBindRenderbuffer(GL_RENDERBUFFER, 0);

		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderID);

		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		return super.init();
	}

	@Override
	public void preRender()
	{
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		glBindFramebuffer(GL_FRAMEBUFFER, identifier);
	}

	@Override
	public void render()
	{
	}

	@Override
	public void postRender()
	{
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		glClear(GL_COLOR_BUFFER_BIT);
		customShader.bind();

		glDisable(GL_DEPTH_TEST);
		glActiveTexture(GL13.GL_TEXTURE0);
		if (previous != null)
		{
			glBindTexture(GL_TEXTURE_2D, previous.getTextureIdentifier());
		} else
		{
			glBindTexture(GL_TEXTURE_2D, textureID);
		}
		glBegin(GL_QUADS);
		glVertex2f(-1f, -1f);
		glVertex2f(1f, -1f);
		glVertex2f(1, 1f);
		glVertex2f(-1, 1f);
		glEnd();
		ShaderComponent.unbind();
		glEnable(GL_DEPTH_TEST);
	}

	public int getTextureIdentifier()
	{
		return textureID;
	}

	public FrameBufferObject getPrevious()
	{
		return previous;
	}

	public void setPrevious(FrameBufferObject previous)
	{
		this.previous = previous;
	}

	public int getIdentifier()
	{
		return identifier;
	}

	public boolean doRender()
	{
		return render;
	}
}
