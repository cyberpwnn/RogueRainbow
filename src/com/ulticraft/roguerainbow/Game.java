package com.ulticraft.roguerainbow;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitScheduler;
import com.ulticraft.core.Cooldown;

public class Game implements Listener
{
	private RegionConfig rc;
	private ArrayList<PlayerData> players;
	private boolean running;
	private boolean playable;
	private BukkitScheduler sch;
	private RogueRainbow pl;
	private CorruptedWool corruptedWool;
	private HashMap<Player, Integer> entirleyDead;
	private HashMap<Player, Integer> coolDead;
	private int baseClock;
	private int clockd;
	private boolean corruption = false;
	private boolean smitten = false;
	private Cooldown leavers;

	private boolean gameActive;
	
	public Game(RegionConfig rc, RogueRainbow pl)
	{
		pl.getServer().getPluginManager().registerEvents(this, pl);
		this.pl = pl;
		
		this.running = false;
		this.rc = rc;
		this.players = new ArrayList<PlayerData>();
		this.entirleyDead = new HashMap<Player, Integer>();
		this.coolDead = new HashMap<Player, Integer>();
		this.playable = rc.isDefined();
		this.sch = pl.getServer().getScheduler();
		this.baseClock = 366;
		this.clockd = 0;
		this.corruption = false;
		this.leavers = new Cooldown(60);
		
		gameActive = false;
	}
	
	public void disbatchTitle(String title, String subtitle)
	{
		Title tm = new Title(title, subtitle, 5, 30, 80);
		for(PlayerData i : getPlayerData())
		{
			tm.send(i.getPlayer());
		}
	}
	
	public void disbatchTitle(String title, String subtitle, Player p)
	{
		Title tm = new Title(title, subtitle, 0, 30, 50);
		tm.send(p);
	}
	
	public void addPlayer(Player player)
	{
		if(!isPlayable())
		{
			player.sendMessage(ChatColor.RED + "All Games are down for maintenance!");
			return;
		}
		
		if(leavers.hasCooldown(player))
		{
			player.sendMessage(ChatColor.RED + "Cannot rejoin. Please wait until the cooldown has finished!");
			return;
		}
		
		if(getPlayerData(player) != null)
		{
			if(getPlayerData(player).isSpectating())
			{
				if(getPlayerData(player).isDead())
				{
					player.sendMessage(ChatColor.RED + "You have no more lives to join!");
					return;
				}
				
				getPlayerData(player).rejoin();
				getPlayerData(player).start();
				return;
			}
		}
		
		if(isPlaying(player))
		{
			player.sendMessage(ChatColor.RED + "You are already playing!");
			return;
		}
		
		else
		{
			if(!isRunning())
			{
				start();
			}
			
			players.add(new PlayerData(player, rc.getRegion().getSpawn()));
			getPlayerData(player).start();
			player.sendMessage(ChatColor.GREEN + "Joining Game");
		}
	}
	
	public void kickPlayer(Player player)
	{
		if(getPlayerData(player) != null)
		{
			if(getPlayerData(player).isSpectating())
			{
				getPlayerData(player).resumePlayer();
				players.remove(getPlayerData(player));
				player.sendMessage(ChatColor.RED + "Quitting Game");
				return;
			}
		}
		
		if(!isPlaying(player))
		{
			player.sendMessage(ChatColor.RED + "You aren't playing!");
			return;
		}
		
		else
		{
			getPlayerData(player).resumePlayer();
			players.remove(getPlayerData(player));
			player.sendMessage(ChatColor.RED + "Quitting Game");
		}
	}
	
	public void spectatePlayer(Player player)
	{
		getPlayerData(player).spectate();
	}
	
	public Player getPlayer(Player player)
	{
		for(PlayerData i : getPlayerData())
		{
			if(i.getPlayer().equals(player))
			{
				return player;
			}
		}
		
		return null;
	}
	
	public PlayerData getPlayerData(Player player)
	{
		for(PlayerData i : getPlayerData())
		{
			if(i.getPlayer().equals(player))
			{
				return i;
			}
		}
		
		return null;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamage(EntityDamageEvent event)
	{
		if(event.getEntity().getType().equals(EntityType.PLAYER))
		{
			if(isPlaying((Player) event.getEntity()))
			{
				event.setDamage(0);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDisconnect(PlayerQuitEvent event)
	{
		if(isPlaying(event.getPlayer()))
		{
			kickPlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onBreakArena(BlockBreakEvent event)
	{
		if(rc.getRegion().contains(event.getBlock()) && !event.getPlayer().hasPermission(Info.PERM_SETUP))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBuildArena(BlockPlaceEvent event)
	{
		if(rc.getRegion().contains(event.getBlock()) && !event.getPlayer().hasPermission(Info.PERM_SETUP))
		{
			event.setCancelled(true);
		}
	}
	
	public void reconfigured()
	{
		playable = rc.isDefined();
	}
	
	public boolean isPlaying(Player player)
	{
		return (getPlayer(player) != null);
	}
	
	public void start()
	{
		running = true;
		pl.verbose("Activating game.");
		clockd = 300;
				
		sch.scheduleSyncRepeatingTask(pl, new Runnable()
		{
			@Override
			public void run()
			{
				gameActive = (players.size() != 0);
				
				if(isGameActive())
				{
					clockd+=5;
					
					if(clockd >= baseClock)
					{
						corruptedWool = new CorruptedWool(rc.getRegion().getCorruptedWool());
						
						corruption = true;
						String corr = "";
						
						for(DyeColor i : corruptedWool.getCorruptedColors())
						{
							corr = corr + i + " ";
						}
						
						DyeColor c = corruptedWool.getCorruptedColors().get(0);
						
						disbatchTitle("", ColorUtils.dyeToChat(c) + corr + " is now corrupted!");
						
						clockd = 0;
						
						for(PlayerData i : getPlayerData())
						{
							if(!i.isSpectating())
							{
								i.score();
							}
							
							i.getPlayer().playSound(i.getPlayer().getLocation(), Sound.AMBIENCE_THUNDER, 2.4f, 0.5f);
							i.getPlayer().playSound(i.getPlayer().getLocation(), Sound.AMBIENCE_THUNDER, 3.0f, 3.0f);
							i.getPlayer().playSound(i.getPlayer().getLocation(), Sound.AMBIENCE_THUNDER, 3.0f, 4.0f);
						}
						
						smitten = false;
					}
					
					for(PlayerData i : getPlayerData())
					{
						i.getPlayer().setFoodLevel(20);
						i.getPlayer().setHealth(i.getLives() * 2);
					}
					
					for(PlayerData i : getPlayerData())
					{
						if(i.isDead() && !entirleyDead.containsKey(i.getPlayer()))
						{
							entirleyDead.put(i.getPlayer(), 240);
							leavers.activate(i.getPlayer());
						}
						
						if(i.isDead() && entirleyDead.containsKey(i.getPlayer()))
						{
							disbatchTitle("", ChatColor.RED + "You can rejoin in " + entirleyDead.get(i.getPlayer())/4 + " seconds!", i.getPlayer());
							entirleyDead.put(i.getPlayer(), entirleyDead.get(i.getPlayer()) - 1);
							if(((float)entirleyDead.get(i.getPlayer()) / 4f) == entirleyDead.get(i.getPlayer())/4)
							{
								i.getPlayer().playSound(i.getPlayer().getLocation(), Sound.CLICK, 0.4f, 6.0f);
							}
							
							if(entirleyDead.get(i.getPlayer()) < 1)
							{
								entirleyDead.remove(i.getPlayer());
								i.undie();
								disbatchTitle(ChatColor.GREEN + "Use /rrj", ChatColor.AQUA + "To rejoin the fight!", i.getPlayer());
							}
						}
						
						else if(i.isSpectating() && i.getLives() > 0 && coolDead.containsKey(i.getPlayer()))
						{
							disbatchTitle("", ChatColor.RED + "Respawning in " + coolDead.get(i.getPlayer())/4 + " seconds!", i.getPlayer());
							coolDead.put(i.getPlayer(), coolDead.get(i.getPlayer()) - 1);
							if(((float)coolDead.get(i.getPlayer()) / 4f) == coolDead.get(i.getPlayer())/4)
							{
								i.getPlayer().playSound(i.getPlayer().getLocation(), Sound.CLICK, 0.3f, 6.0f);
							}
							
							if(coolDead.get(i.getPlayer()) < 1)
							{
								coolDead.remove(i.getPlayer());
								addPlayer(i.getPlayer());
								disbatchTitle("", "", i.getPlayer());
							}
						}
					}
					
					for(Player i : pl.getServer().getOnlinePlayers())
					{
						if(entirleyDead.containsKey(i) && !isPlaying(i))
						{
							entirleyDead.put(i, entirleyDead.get(i) - 1);
							
							if(entirleyDead.get(i) < 1)
							{
								entirleyDead.remove(i);
							}
						}
						
						else if(coolDead.containsKey(i) && !isPlaying(i))
						{
							coolDead.put(i, coolDead.get(i) - 1);
							
							if(coolDead.get(i) < 1)
							{
								coolDead.remove(i);
							}
						}
						
						if(!isPlaying(i) && rc.getRegion().contains(i.getLocation().getBlock()))
						{
							i.teleport(new Location(i.getWorld(), i.getLocation().getX(), i.getLocation().getY()+8, i.getLocation().getZ()));
						}
					}
					
					for(PlayerData i : getPlayerData())
					{
						if(i.getHealth() <= 0)
						{
							if(!i.isSpectating())
							{
								spectatePlayer(i.getPlayer());
								i.die();
								
								if(i.isDead() && !entirleyDead.containsKey(i.getPlayer()))
								{
									entirleyDead.put(i.getPlayer(), 240);
									leavers.activate(i.getPlayer());
								}
								
								else if(i.isSpectating() && i.getLives() > 0)
								{
									coolDead.put(i.getPlayer(), 40);
								}
							}
						}
						
						else
						{
							if(!i.isSpectating())
							{
								Armor.colorArmor(i.getPlayer());
							}
						}
					}
					
					processCorruption();
				}
			}
		}, 0L, 5L);
	}
	
	public void processCorruption()
	{		
		if(!corruption)
		{
			return;
		}
		
		Location a = rc.getRegion().getPointA();
		Location b = rc.getRegion().getPointB();
		
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
					Block block = a.getWorld().getBlockAt(x, y, z);
					if(block.getType().equals(Material.WOOL))
					{
						DyeColor color = ((Wool)block.getState().getData()).getColor();
						
						if(corruptedWool.getCorruptedColors().contains(color))
						{
							ParticleEffect.SPELL_MOB.display(0, 1, 0, 1, 1, block.getLocation(), 50);
							
							if(!smitten)
							{
								
								smitten = true;
							}
							
							for(PlayerData i : getPlayerData())
							{
								if(block.getRelative(BlockFace.UP).getLocation().equals(i.getPlayer().getLocation().getBlock().getLocation()))
								{
									i.damage();
									i.getPlayer().playSound(i.getPlayer().getLocation(), Sound.AMBIENCE_RAIN, 0.3f, 4.0f);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void stop()
	{
		running = false;
		pl.verbose("Game is no longer active.");
	}

	public RegionConfig getRegionConfig()
	{
		return rc;
	}

	public ArrayList<PlayerData> getPlayerData()
	{
		return players;
	}

	public boolean isRunning()
	{
		return running;
	}
	
	public boolean isPlayable()
	{
		return playable;
	}
	
	public boolean isGameActive()
	{
		return gameActive;
	}
}
