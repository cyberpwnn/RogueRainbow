package com.ulticraft.roguerainbow;

import net.md_5.bungee.api.ChatColor;

public class Info
{
	public static final String CMD_RR = "roguerainbow";
	public static final String CMD_RRJ = "rrj";
	public static final String CMD_RRQ = "rrq";
	
	public static final String TAG_RR = ChatColor.LIGHT_PURPLE + "[RogueRainbow]";
	
	public static final String PERM_PLAY = "roguerainbow.play";
	public static final String PERM_SETUP = "roguerainbow.setup";
	
	public static final String[] MSG_HELP = new String[]
	{
		ChatColor.RED + "=" + ChatColor.GOLD + "=" + ChatColor.YELLOW + "=" + ChatColor.GREEN + "=" + ChatColor.AQUA + "=" + ChatColor.BLUE + "=" + ChatColor.LIGHT_PURPLE + " RogueRainbow! v1.0 " + ChatColor.BLUE + "=" + ChatColor.AQUA + "=" + ChatColor.GREEN + "=" + ChatColor.YELLOW + "=" + ChatColor.GOLD + "=" + ChatColor.RED + "=",
		ChatColor.LIGHT_PURPLE + " /rr " + ChatColor.GREEN + "join" + ChatColor.BLUE + "  ( /rrj )",
		ChatColor.LIGHT_PURPLE + " /rr " + ChatColor.YELLOW + "quit" + ChatColor.AQUA + "  ( /rrq )"
	};
}
