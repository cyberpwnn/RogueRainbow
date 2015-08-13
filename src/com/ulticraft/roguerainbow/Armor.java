package com.ulticraft.roguerainbow;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;

public class Armor
{
	public static void colorArmor(Player p)
	{
		if(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.WOOL))
		{
			DyeColor color = ((Wool)p.getLocation().getBlock().getRelative(BlockFace.DOWN).getState().getData()).getColor();
			
			try
			{
				if(p.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
				{
					LeatherArmorMeta lam = (LeatherArmorMeta) p.getInventory().getHelmet().getItemMeta();
					lam.setColor(color.getColor());
					p.getInventory().getHelmet().setItemMeta(lam);
				}
				
				else
				{
					ItemStack lhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
					LeatherArmorMeta lam = (LeatherArmorMeta) lhelmet.getItemMeta();
					lam.setColor(color.getColor());
					p.getInventory().setHelmet(lhelmet);
				}
			}
			
			catch(NullPointerException e)
			{
				
			}
			
			try
			{
				if(p.getInventory().getChestplate().getType().equals(Material.LEATHER_CHESTPLATE))
				{
					LeatherArmorMeta lam = (LeatherArmorMeta) p.getInventory().getChestplate().getItemMeta();
					lam.setColor(color.getColor());
					p.getInventory().getChestplate().setItemMeta(lam);
				}
				
				else
				{
					ItemStack lhelmet = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
					LeatherArmorMeta lam = (LeatherArmorMeta) lhelmet.getItemMeta();
					lam.setColor(color.getColor());
					p.getInventory().setChestplate(lhelmet);
				}
			}
			
			catch(NullPointerException e)
			{
				
			}
			
			try
			{
				if(p.getInventory().getLeggings().getType().equals(Material.LEATHER_LEGGINGS))
				{
					LeatherArmorMeta lam = (LeatherArmorMeta) p.getInventory().getLeggings().getItemMeta();
					lam.setColor(color.getColor());
					p.getInventory().getLeggings().setItemMeta(lam);
				}
				
				else
				{
					ItemStack lhelmet = new ItemStack(Material.LEATHER_LEGGINGS, 1);
					LeatherArmorMeta lam = (LeatherArmorMeta) lhelmet.getItemMeta();
					lam.setColor(color.getColor());
					p.getInventory().setLeggings(lhelmet);
				}
			}
			
			catch(NullPointerException e)
			{
				
			}
			
			try
			{
				if(p.getInventory().getBoots().getType().equals(Material.LEATHER_BOOTS))
				{
					LeatherArmorMeta lam = (LeatherArmorMeta) p.getInventory().getBoots().getItemMeta();
					lam.setColor(color.getColor());
					p.getInventory().getBoots().setItemMeta(lam);
				}
				
				else
				{
					ItemStack lhelmet = new ItemStack(Material.LEATHER_BOOTS, 1);
					LeatherArmorMeta lam = (LeatherArmorMeta) lhelmet.getItemMeta();
					lam.setColor(color.getColor());
					p.getInventory().setBoots(lhelmet);
				}
			}
			
			catch(NullPointerException e)
			{
				
			}
		}
		
		else
		{
			return;
		}
	}
}
