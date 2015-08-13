package com.ulticraft.roguerainbow;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.ulticraft.core.ExperienceManager;

public class PlayerData
{
	private Player player;
	private Location location;
	private Location destination;
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private ItemStack[] items;
	private GameMode gamemode;
	private ExperienceManager em;
	private double heal;
	private int food;
	private double health = 1;
	private int score = 1;
	private int txp;
	private boolean spectating;
	private int lives;
	private boolean dead;
	
	public PlayerData(Player player, Location destination)
	{
		this.player = player;
		this.destination = destination;
		ItemStack[] pi = player.getInventory().getContents();
		spectating = false;
		em = new ExperienceManager(player);
		txp = em.getCurrentExp();
		items = new ItemStack[pi.length];
		gamemode = player.getGameMode();
		heal = player.getHealth();
		food = player.getFoodLevel();
		lives = 10;
		dead = false;
		
		for(int i = 0; i < pi.length; i++)
		{
		    if(pi[i] != null)
		    {
		        items[i] = pi[i].clone();
		    }
		}
		
		if(player.getInventory().getHelmet() != null)
		{
			helmet = player.getInventory().getHelmet().clone();
		}
		
		if(player.getInventory().getChestplate() != null)
		{
			chestplate = player.getInventory().getChestplate().clone();
		}
		
		if(player.getInventory().getLeggings() != null)
		{
			leggings = player.getInventory().getLeggings().clone();
		}
		
		if(player.getInventory().getBoots() != null)
		{
			boots = player.getInventory().getBoots().clone();
		}
		
		location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
	}
	
	public void start()
	{
		player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));        
		player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(destination);
        updateXP();
	}
	
	public void resumePlayer()
	{
		player.getInventory().setContents(items);
		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
		player.teleport(location);
		player.setGameMode(gamemode);
		player.setHealth(heal);
		player.setFoodLevel(food);
		em.setExp(txp);
		
		Title title = new Title(ChatColor.RED + "Thanks " + ChatColor.BLUE + " for " + ChatColor.GREEN + " Playing!", ChatColor.AQUA + "Score: " + getScore(), 5, 30, 60);
		title.send(getPlayer());
		player.getWorld().strikeLightningEffect(player.getLocation());
	}
	
	public void undie()
	{
		lives = 10;
		dead = false;
	}
	
	public void damage()
	{
		health -= 0.15;
		updateXP();
	}
	
	public void score()
	{
		score++;
		updateXP();
	}
	
	public void updateXP()
	{
		player.setLevel(score);
		player.setExp((float) health);
	}

	public Player getPlayer()
	{
		return player;
	}

	public Location getLocation()
	{
		return location;
	}

	public Location getDestination()
	{
		return destination;
	}
	
	public ItemStack[] getItems()
	{
		return items;
	}

	public ItemStack getHelmet()
	{
		return helmet;
	}

	public ItemStack getChestplate()
	{
		return chestplate;
	}

	public ItemStack getLeggings()
	{
		return leggings;
	}

	public ItemStack getBoots()
	{
		return boots;
	}

	public GameMode getGamemode()
	{
		return gamemode;
	}

	public double getHealth()
	{
		return health;
	}

	public int getScore()
	{
		return score;
	}
	
	public boolean isSpectating()
	{
		return spectating;
	}
	
	public void die()
	{
		if(lives <= 1)
		{
			dead = true;
		}
		
		else
		{
			lives--;
		}
		
		player.getWorld().createExplosion(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 1.0f, false, false);
	}
	
	public boolean isDead()
	{
		return dead;
	}
	
	public int getLives()
	{
		return lives;
	}

	public void spectate()
	{
		if(!spectating)
		{
			player.teleport(new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY() + 3, player.getLocation().getZ(), player.getLocation().getYaw(), 65));
			spectating = true;
		}
	}

	public void rejoin()
	{
		spectating = false;
		health = 1;
	}
}
