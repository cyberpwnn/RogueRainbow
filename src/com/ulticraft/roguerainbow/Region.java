package com.ulticraft.roguerainbow;

import java.util.ArrayList;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.Wool;

public class Region
{
	private World world;
	private Location pointA;
	private Location pointB;
	private Location spawn;
	private Integer spectation;
	private ArrayList<DyeColor> corruptedWoolColors;
	private RegionConfig rc;

	public Region(Location a, Location b, Location c, RegionConfig rc) throws MultiDimensionalException, RegionlessSpawnException, NoBarrierException, NoSpectationLayer
	{
		rc.getPlugin().verbose("Creating Region");
		if(!(a.getWorld().equals(b.getWorld())))
		{
			throw new MultiDimensionalException("Cannot select multidimensional points!");
		}
				
		corruptedWoolColors = new ArrayList<DyeColor>();
		
		spawn = null;
		pointA = a;
		pointB = b;
		world = a.getWorld();
		boolean barriered = false;

		int minX = Math.min(a.getBlockX(), b.getBlockX());
		int minY = Math.min(a.getBlockY(), b.getBlockY());
		int minZ = Math.min(a.getBlockZ(), b.getBlockZ());
		int maxX = Math.max(a.getBlockX(), b.getBlockX());
		int maxY = Math.max(a.getBlockY(), b.getBlockY());
		int maxZ = Math.max(a.getBlockZ(), b.getBlockZ());

		for(int x = minX; x <= maxX; x++)
		{
			for(int y = minY; y <= maxY; y++)
			{
				for(int z = minZ; z <= maxZ; z++)
				{
					Block block = world.getBlockAt(x, y, z);
					if(block.getType().equals(Material.WOOL))
					{
						DyeColor color = ((Wool)block.getState().getData()).getColor();
						
						if(!corruptedWoolColors.contains(color))
						{
							rc.getPlugin().verbose("Found Corrupted Wool: " + color.toString() + " " + block.getType().toString());
							corruptedWoolColors.add(color);
						}
					}
					
					if(block.getType().equals(Material.BARRIER))
					{
						if(!barriered)
						{
							barriered = true;
							rc.getPlugin().verbose("Found Barriers");
						}
					}

					if(c.getBlockX() == x && c.getBlockY() == y && c.getBlockZ() == z && spawn == null)
					{
						rc.getPlugin().verbose("Found Spawn");
						spawn = new Location(c.getWorld(), c.getBlockX() + 0.5, c.getBlockY(), c.getBlockZ() + 0.5);
					}
				}
			}
		}
		
		if(spawn == null)
		{
			throw new RegionlessSpawnException("Spawn is not inside defined Region");
		}
		
		if(!barriered)
		{
			throw new NoBarrierException("No Barriers found in region");
		}
		
		for(int y = minY; y <= maxY; y++)
		{
			Block block = world.getBlockAt(spawn.getBlockX(), y, spawn.getBlockZ());
			if(block.getType().equals(Material.BARRIER))
			{
				spectation = y;
				rc.getPlugin().verbose("Found Spectation layer");
				break;
			}
		}
		
		if(spectation == null)
		{
			throw new NoSpectationLayer("No Spectator Barriers found!");
		}
		
		rc.saveRegionData(pointA, pointB, spawn);
	}
	
	public boolean contains(Block block)
	{
		Location a = getPointA();
		Location b = getPointB();
		
		int minX = Math.min(a.getBlockX(), b.getBlockX());
		int minY = Math.min(a.getBlockY(), b.getBlockY());
		int minZ = Math.min(a.getBlockZ(), b.getBlockZ());
		int maxX = Math.max(a.getBlockX(), b.getBlockX());
		int maxY = Math.max(a.getBlockY(), b.getBlockY());
		int maxZ = Math.max(a.getBlockZ(), b.getBlockZ());

		for(int x = minX; x <= maxX; x++)
		{
			for(int y = minY; y <= maxY; y++)
			{
				for(int z = minZ; z <= maxZ; z++)
				{
					Block blk = world.getBlockAt(x, y, z);
					if(block.getLocation().equals(blk.getLocation()))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}

	public World getWorld()
	{
		return world;
	}

	public Location getPointA()
	{
		return pointA;
	}

	public Location getPointB()
	{
		return pointB;
	}

	public Location getSpawn()
	{
		return new Location(spawn.getWorld(), spawn.getBlockX() + 0.5, spawn.getBlockY() + 1, spawn.getBlockZ() + 0.5);
	}
	
	public ArrayList<DyeColor> getCorruptedWool()
	{
		return corruptedWoolColors;
	}

	public RegionConfig getRc()
	{
		return rc;
	}
}
