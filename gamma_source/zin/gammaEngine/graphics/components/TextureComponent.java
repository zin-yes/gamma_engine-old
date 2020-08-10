package zin.gammaEngine.graphics.components;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.core.utils.Logger;
import zin.gammaEngine.graphics.utils.TextureType;

public class TextureComponent extends GameComponent
{

	private Map<String, Integer> cachedTextures = new HashMap<>();

	public int identifier;
	public int width, height, nrChannels;
	public String fileName;

	private boolean gammaCorrection = false;
	private TextureType type;

	private String[] textures = new String[6];

	public TextureComponent(String[] textures)
	{
		this.textures = textures;
		type = TextureType.CUBE_MAP;
		fileName = textures[0] + " " + textures[1] + " " + textures[2] + " " + textures[3] + " " + textures[4] + " "
				+ textures[5] + " ";

		if (cachedTextures.containsKey(fileName))
		{
			Logger.warn("You are trying to load a texture that has already been loaded, caching.");
			identifier = cachedTextures.get(fileName);
			return;
		}
	}

	public TextureComponent(String fileName, TextureType type)
	{
		this.fileName = fileName;
		this.type = type;

		textures[0] = "not a cubemap";

		if (cachedTextures.containsKey(fileName))
		{
			Logger.warn("You are trying to load a texture that has already been loaded, caching.");
			identifier = cachedTextures.get(fileName);
			return;
		}

		if (type == TextureType.ALBEDO)
			gammaCorrection = true;
	}

	@Override
	public boolean init()
	{
		if (textures[0] != "not a cubemap")
		{
			identifier = glGenTextures();
			glBindTexture(GL_TEXTURE_CUBE_MAP, identifier);

			int width, height;
			ByteBuffer image = null;
			for (int i = 0; i < textures.length; i++)
			{
				IntBuffer w = BufferUtils.createIntBuffer(1);
				IntBuffer h = BufferUtils.createIntBuffer(1);
				IntBuffer comp = BufferUtils.createIntBuffer(1);

				STBImage.stbi_set_flip_vertically_on_load(false);
				image = STBImage.stbi_load(textures[i], w, h, comp, 0);

				if (image == null)
				{
					Logger.error("Failed to load cubemap texture: \"" + textures[i] + "\"");
					return false;
				}

				width = w.get();
				height = h.get();

				glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
				glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
				glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

				glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL30.GL_SRGB, width, height, 0, GL_RGB,
						GL_UNSIGNED_BYTE, image);
			}

			MemoryUtil.memFree(image);

			return true;
		} else
		{
			ByteBuffer image;
			try (MemoryStack stack = MemoryStack.stackPush())
			{
				IntBuffer w = stack.mallocInt(1);
				IntBuffer h = stack.mallocInt(1);
				IntBuffer comp = stack.mallocInt(1);

				STBImage.stbi_set_flip_vertically_on_load(true);
				image = STBImage.stbi_load(fileName, w, h, comp, STBImage.STBI_rgb_alpha);

				if (image == null)
				{
					Logger.error("Failed to load texture file \"" + fileName + "\".");

					return false;
				}

				width = w.get();
				height = h.get();
				nrChannels = comp.get();
			}

			identifier = GL11.glGenTextures();
			bindAuto();

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_REPEAT);

			if (getParent().getGame().getMipmapping())
			{
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			} else
			{
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			}
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

			if (!gammaCorrection)
			{
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA,
						GL11.GL_UNSIGNED_BYTE, image);
			} else
			{
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_SRGB_ALPHA, width, height, 0, GL11.GL_RGBA,
						GL11.GL_UNSIGNED_BYTE, image);
			}

			if (getParent().getGame().getMipmapping())
			{
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			}

			if (getParent().getGame().getAnisotropicFiltering())
			{
				if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic)
				{
					float amount = Math.min(getParent().getGame().getAnisotropicFilteringAmount(),
							GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
					GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
							amount);
				} else
				{
					Logger.warn("Anisotropic filtering is not supported on your device.");
				}
			}

			cachedTextures.put(fileName, identifier);

			return true;
		}
	}

	@Override
	public void preRender()
	{
		bindAuto();
	}

	@Override
	public void postRender()
	{
		unbind();
	}

	public void bindAuto()
	{
		switch (type)
		{
		case ALBEDO:
			bind(0);
			break;
		case NORMAL:
			bind(1);
			break;
		case HEIGHT:
			bind(2);
			break;
		case ROUGHNESS:
			bind(3);
			break;
		case METALLIC:
			bind(4);
			break;
		case AMBIENT_OCCLUSION:
			bind(5);
			break;

		default:
			break;
		}
	}

	public void bind(int bank)
	{
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + bank);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, identifier);
	}

	public void unbind()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	@Override
	public void destroy()
	{
		GL11.glDeleteTextures(identifier);
	}

	public int getIdentifier()
	{
		return identifier;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getNrChannels()
	{
		return nrChannels;
	}

}