package zin.gammaEngine.experimental.postProcessing;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import zin.gammaEngine.core.Game;
import zin.gammaEngine.graphics.Display;

public class Manager
{

	private List<FrameBufferObject> fbos = new ArrayList<>();

	private Game game;

	public Manager(Game game)
	{
		this.game = game;
		game.getEngine().manager = this;
	}

	public void init()
	{
		for (FrameBufferObject fbo : fbos)
		{
			fbo.init();
		}
	}

	public void render()
	{
		FrameBufferObject previous = null;

		for (int i = 0; i < fbos.size(); i++)
		{
			FrameBufferObject fbo = fbos.get(i);
			if (previous == null)
			{
				fbo.preRender();

				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

				if (fbo.doRender())
				{
					game.getRootObject().preRender();
					game.getRootObject().priorityRender();
					game.getRootObject().render();
					game.getRootObject().postRender();
				}
				fbo.render();

				if (fbos.size() > 1)
				{
					fbos.get(i + 1).preRender();
					System.out.println("first");
					fbo.postRender();
				} else
				{
					glBindFramebuffer(GL_FRAMEBUFFER, 0);
					fbo.postRender();
				}

				previous = fbo;
			} else
			{
				System.out.println("second");
				if (i + 1 < fbos.size())
				{
					fbos.get(i + 1).preRender();
					fbo.render();
					fbo.postRender();
				} else
				{
					fbo.render();
					glBindFramebuffer(GL_FRAMEBUFFER, 0);
					fbo.postRender();
				}
			}
		}

		Display.update();
	}

	public void addFrameBufferObject(FrameBufferObject fbo)
	{
		fbos.add(fbo);
	}

	public Game getGame()
	{
		return game;
	}

	public void setGame(Game game)
	{
		this.game = game;
	}

}
