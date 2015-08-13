package com.ulticraft.roguerainbow;

import java.util.logging.Logger;
import net.minecraft.server.v1_8_R3.Material;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RogueRainbow extends JavaPlugin implements Listener
{
	private Logger logger;
	private RegionConfig rc;

	private boolean verbose;
	private Player setupMode;
	
	private Location position1;
	private Location position2;
	private Location positionSpawn;

	private Game game;

	@Override
	public void onEnable()
	{
		// removewhendone
		verbose = true;

		logger = getLogger();

		verbose("Loading Arena");

		getServer().getPluginManager().registerEvents(this, this);
		rc = new RegionConfig(this);
		setupMode = null;
		
		game = new Game(rc, this);
		game.start();
	}

	@Override
	public void onDisable()
	{
		game.stop();
	}

	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		if(cmd.getName().equalsIgnoreCase(Info.CMD_RRJ))
		{
			game.addPlayer((Player) sender);
		}
		
		else if(cmd.getName().equalsIgnoreCase(Info.CMD_RRQ))
		{
			game.kickPlayer((Player) sender);
		}
		
		else if(cmd.getName().equalsIgnoreCase(Info.CMD_RR))
		{			
			if(args.length == 0)
			{
				for(String i : Info.MSG_HELP)
				{
					sender.sendMessage(i);
				}
				
				if(sender.hasPermission(Info.PERM_SETUP))
				{
					sender.sendMessage(ChatColor.LIGHT_PURPLE + " /rr " + ChatColor.RED + "set");
				}
			}
			
			else
			{
				if(!(sender instanceof Player))
				{
					return true;
				}
				
				if(args[0].equalsIgnoreCase("join"))
				{
					game.addPlayer((Player) sender);
				}
				
				else if(args[0].equalsIgnoreCase("quit"))
				{
					game.kickPlayer((Player) sender);
				}
				
				else if(args[0].equalsIgnoreCase("set"))
				{
					if(sender.hasPermission(Info.PERM_SETUP))
					{
						if(sender instanceof Player)
						{
							setupMode = (Player) sender;
							position1 = null;
							position2 = null;
							positionSpawn = null;
						}
						
						else
						{
							sender.sendMessage("Sorry, only Players!");
						}
					}
					
					else
					{
						sender.sendMessage(ChatColor.RED + "Sorry you dont have permission to do this!");
					}
				}
				
				else
				{
					for(String i : Info.MSG_HELP)
					{
						sender.sendMessage(i);
					}
				}
			}
		}
		
		return true;
	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent e)
	{
		if(setupMode == null)
		{
			return;
		}
		
		if(setupMode != e.getPlayer())
		{
			return;
		}
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && !e.getClickedBlock().getType().equals(Material.AIR))
		{
			e.setCancelled(true);
			
			if(e.getPlayer().isSneaking())
			{
				if(e.getClickedBlock().getLocation().equals(positionSpawn))
				{
					return;
				}
				
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CLICK, 1.0f, 1.0f);
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CLICK, 2.0f, 2.0f);
				e.getPlayer().sendMessage(ChatColor.GREEN + "Click! Spawn Selected");
				positionSpawn = e.getClickedBlock().getLocation();
				ParticleEffect.CRIT_MAGIC.display(1f, 0.3f, 1f, 1, 800, e.getClickedBlock().getLocation(), 8f);
			}
			
			else
			{
				if(e.getClickedBlock().getLocation().equals(position2))
				{
					return;
				}
				
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CLICK, 1.0f, 1.0f);
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CLICK, 2.0f, 2.0f);
				e.getPlayer().sendMessage(ChatColor.GREEN + "Click! Position 2 Selected");
				position2 = e.getClickedBlock().getLocation();
				ParticleEffect.CRIT.display(1f, 0.3f, 1f, 1, 800, e.getClickedBlock().getLocation(), 8f);
			}
		}
		
		else if(e.getAction() == Action.LEFT_CLICK_BLOCK && !e.getClickedBlock().getType().equals(Material.AIR))
		{
			e.setCancelled(true);
			
			if(e.getPlayer().isSneaking())
			{
				if(e.getClickedBlock().getLocation().equals(positionSpawn))
				{
					return;
				}
				
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CLICK, 1.0f, 1.0f);
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CLICK, 2.0f, 2.0f);
				e.getPlayer().sendMessage(ChatColor.GREEN + "Click! Spawn Selected");
				positionSpawn = e.getClickedBlock().getLocation();
				ParticleEffect.CRIT_MAGIC.display(1f, 0.3f, 1f, 1, 800, e.getClickedBlock().getLocation(), 8f);
			}
			
			else
			{
				if(e.getClickedBlock().getLocation().equals(position1))
				{
					return;
				}
				
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CLICK, 1.0f, 1.0f);
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CLICK, 2.0f, 2.0f);
				e.getPlayer().sendMessage(ChatColor.GREEN + "Click! Position 1 Selected");
				position1 = e.getClickedBlock().getLocation();
				ParticleEffect.CRIT.display(1f, 0.3f, 1f, 1, 800, e.getClickedBlock().getLocation(), 8f);
			}
		}
		
		if(position1 != null && position2 != null && positionSpawn != null)
		{
			final String f = rc.setRegion(position1, position2, positionSpawn);
			
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ANVIL_USE, 2.0f, 0.3f);
			setupMode = null;
			
			if(f == "")
			{
				game.reconfigured();
			}
			
			getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
			{
				@Override
				public void run()
				{
					if(f == "")
					{
						positionSpawn.getWorld().strikeLightningEffect(positionSpawn);
						
						ParticleEffect.SMOKE_LARGE.display(0f, 0.5f, 0f, 0.5f, 300, positionSpawn, 100);
						
						new Title("", ChatColor.GREEN + "Set Arena!", 5, 30, 50).send(e.getPlayer());
					}
					
					else
					{
						e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.FALL_BIG, 1.0f, 0.3f);
						
						new Title(ChatColor.RED + "FAIL",  ChatColor.UNDERLINE + "" + ChatColor.DARK_RED + f, 5, 40, 100).send(e.getPlayer());
					}
				}
			}, 30L);
		}
	}

	public void info(String msg)
	{
		logger.info(msg);
	}

	public void warn(String msg)
	{
		logger.warning(msg);
	}

	public void fatality(String msg)
	{
		info("ERROR: " + msg.toUpperCase());
	}

	public void verbose(String msg)
	{
		if(isVerbose())
		{
			logger.info("VERBOSE: " + msg);
		}
	}

	public boolean isVerbose()
	{
		return verbose;
	}
}
