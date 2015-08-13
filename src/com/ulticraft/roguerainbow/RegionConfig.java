package com.ulticraft.roguerainbow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class RegionConfig
{
	private File file;
	private FileConfiguration fc;

	private Location point1;
	private Location point2;
	private Location spawnPoint;
	private World world;
	private Region region;
	private boolean defined;
	
	private RogueRainbow pl;

	public RegionConfig(RogueRainbow pl)
	{
		this.pl = pl;
		final File pfolder = new File(pl.getDataFolder() + File.separator);
		if(!pfolder.exists())
		{
			pfolder.mkdirs();
		}

		file = new File(pl.getDataFolder() + File.separator + "regions.yml");

		fc = new YamlConfiguration();

		if(!file.exists())
		{
			pl.verbose("Creating Configuration file");
			defined = false;

			try
			{
				fc.set("world", null);

				fc.set("point1.x", null);
				fc.set("point1.y", null);
				fc.set("point1.z", null);

				fc.set("point2.x", null);
				fc.set("point2.y", null);
				fc.set("point2.z", null);

				fc.set("spawn.x", null);
				fc.set("spawn.y", null);
				fc.set("spawn.z", null);

				file.createNewFile();
				fc.save(file);
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		else
		{
			try
			{
				pl.verbose("Loading Configuration");
				fc.load(file);

				defined = true;
				
				if(fc.getString("world") == null)
				{
					defined = false;
				}

				if(defined)
				{
					pl.verbose("Loading Config Data");
					world = pl.getServer().getWorld(fc.getString("world"));
					point1 = new Location(world, fc.getInt("point1.x"), fc.getInt("point1.y"), fc.getInt("point1.z"));
					point2 = new Location(world, fc.getInt("point2.x"), fc.getInt("point2.y"), fc.getInt("point2.z"));
					spawnPoint = new Location(world, fc.getDouble("spawn.x"), fc.getDouble("spawn.y"), fc.getDouble("spawn.z"));

					try
					{
						pl.verbose("Creating Region");
						region = new Region(point1, point2, spawnPoint, this);
					}

					catch(MultiDimensionalException e)
					{
						pl.verbose(e.getMessage());
					}

					catch(RegionlessSpawnException e)
					{
						pl.verbose(e.getMessage());
					}

					catch(NoBarrierException e)
					{
						pl.verbose(e.getMessage());
					}
					
					catch(NoSpectationLayer e)
					{
						pl.verbose(e.getMessage());
					}

					if(region == null)
					{
						defined = false;

						try
						{
							fc.set("world", null);

							fc.set("point1.x", null);
							fc.set("point1.y", null);
							fc.set("point1.z", null);

							fc.set("point2.x", null);
							fc.set("point2.y", null);
							fc.set("point2.z", null);

							fc.set("spawn.x", null);
							fc.set("spawn.y", null);
							fc.set("spawn.z", null);

							file.createNewFile();
							fc.save(file);
						}

						catch(IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}

			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}

			catch(InvalidConfigurationException e)
			{
				e.printStackTrace();
			}
		}
	}

	public Location getPoint1()
	{
		return point1;
	}

	public Location getPoint2()
	{
		return point2;
	}

	public Location getSpawnPoint()
	{
		return spawnPoint;
	}

	public World getWorld()
	{
		return world;
	}

	public Region getRegion()
	{
		return region;
	}

	public boolean isDefined()
	{
		return defined;
	}
	
	public String setRegion(Location a, Location b, Location c)
	{
		try
		{
			region = new Region(a, b, c, this);
			return "";
		}
		
		catch(MultiDimensionalException e)
		{
			return ChatColor.RED + e.getMessage();
		}
		
		catch(RegionlessSpawnException e)
		{
			return ChatColor.RED + e.getMessage();
		}
		
		catch(NoBarrierException e)
		{
			return ChatColor.RED + e.getMessage();
		}
		
		catch(NoSpectationLayer e)
		{
			return ChatColor.RED + e.getMessage();
		}
	}

	public void saveRegionData(Location pointA, Location pointB, Location spawn)
	{
		try
		{
			fc.set("world", spawn.getWorld().getName());

			fc.set("point1.x", pointA.getBlockX());
			fc.set("point1.y", pointA.getBlockY());
			fc.set("point1.z", pointA.getBlockZ());

			fc.set("point2.x", pointB.getBlockX());
			fc.set("point2.y", pointB.getBlockY());
			fc.set("point2.z", pointB.getBlockZ());

			fc.set("spawn.x", spawn.getBlockX());
			fc.set("spawn.y", spawn.getBlockY());
			fc.set("spawn.z", spawn.getBlockZ());

			fc.save(file);
			
			defined = true;
		}

		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public RogueRainbow getPlugin()
	{
		return pl;
	}
}
