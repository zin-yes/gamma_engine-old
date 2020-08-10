package zin.exampleGame.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import zin.gammaEngine.core.componentSystem.GameObject;
import zin.gammaEngine.core.utils.Logger;
import zin.gammaEngine.graphics.PBRShader;
import zin.gammaEngine.graphics.components.ModelComponent;
import zin.gammaEngine.graphics.components.TextureComponent;
import zin.gammaEngine.graphics.utils.TextureType;

public class Blueprint extends GameObject
{

	private String fileName;
	private BlueprintType type;

	private String content = "";

	public Blueprint(String fileName, BlueprintType type)
	{
		this.fileName = fileName;
		this.type = type;

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String line;

			while ((line = reader.readLine()) != null)
			{
				if (!line.startsWith("#"))
					content += line + "\n";
			}

			reader.close();
		} catch (IOException e)
		{
			Logger.error("Failed to load blueprint file \"" + fileName + "\".");
			return;
		}
	}

	public GameObject constructObject()
	{
		GameObject object = new GameObject();

		if (type == BlueprintType.GAMMA_MODEL_FILE)
		{
			String[] lines = content.split("\n");

			ModelComponent model = null;

			for (String line : lines)
			{
				String[] tokens = line.split(": ");

				if (tokens[0].equalsIgnoreCase("model_file"))
				{
					model = new ModelComponent(tokens[1]);
				}
			}

			if (model == null)
			{
				Logger.error("Failed to load model blueprint \"" + fileName + "\".");
			}

			for (String line : lines)
			{
				String[] tokens = line.split(": ");

				if (tokens[0].equalsIgnoreCase("shader"))
				{
					if (tokens[1].equalsIgnoreCase("pbr"))
					{
						object.addComponent(new PBRShader());
					}
				} else if (tokens[0].equalsIgnoreCase("albedo_file"))
				{
					if (tokens[1].equalsIgnoreCase("default"))
					{
						model.addSubComponent(new TextureComponent("", TextureType.ALBEDO));
					} else
					{
					}
				} else if (tokens[0].equalsIgnoreCase("normal_file"))
				{
					if (tokens[1].equalsIgnoreCase("default"))
					{
						model.addSubComponent(new TextureComponent("", TextureType.NORMAL));
					} else
					{

					}
				} else if (tokens[0].equalsIgnoreCase("height_file"))
				{
					if (tokens[1].equalsIgnoreCase("default"))
					{
						model.addSubComponent(new TextureComponent("", TextureType.HEIGHT));
					} else
					{

					}
				} else if (tokens[0].equalsIgnoreCase("metallic_file"))
				{
					if (tokens[1].equalsIgnoreCase("default"))
					{
						model.addSubComponent(new TextureComponent("", TextureType.METALLIC));
					} else
					{

					}
				} else if (tokens[0].equalsIgnoreCase("roughness_file"))
				{
					if (tokens[1].equalsIgnoreCase("default"))
					{
						model.addSubComponent(new TextureComponent("", TextureType.ROUGHNESS));
					} else
					{

					}
				} else if (tokens[0].equalsIgnoreCase("ao_file"))
				{
					if (tokens[1].equalsIgnoreCase("default"))
					{
						model.addSubComponent(new TextureComponent("", TextureType.AMBIENT_OCCLUSION));
					} else
					{

					}
				}
			}
		} else
		{
			Logger.error("No blueprint type found for file \"" + fileName + "\".");
		}

		return object;
	}

	public String getContent()
	{
		return content;
	}

	public String getFileName()
	{
		return fileName;
	}

	public BlueprintType getType()
	{
		return type;
	}

}
