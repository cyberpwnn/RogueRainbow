package com.ulticraft.roguerainbow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.bukkit.DyeColor;

public class CorruptedWool
{
	public enum Mode
	{
		NORMAL, INVERT
	}
	
	private ArrayList<DyeColor> corruptedColors;
	private Mode mode;
	
	public CorruptedWool(ArrayList<DyeColor> corruptedColors)
	{
		Random r = new Random();
		Mode mode = Mode.NORMAL;
		
		this.corruptedColors = new ArrayList<DyeColor>();
		
		if(r.nextInt(3) == 1)
		{
			this.mode = Mode.INVERT;
		}
		
		if(mode == Mode.NORMAL)
		{
			Collections.shuffle(corruptedColors);
			this.corruptedColors.add(corruptedColors.get(0));
		}
		
		else
		{
			Collections.shuffle(corruptedColors);
			DyeColor nil = corruptedColors.get(0);
			
			for(DyeColor i : corruptedColors)
			{
				if(i != nil)
				{
					this.corruptedColors.add(i);
				}
			}
		}
		
		this.mode = mode;
	}

	public ArrayList<DyeColor> getCorruptedColors()
	{
		return corruptedColors;
	}

	public Mode getMode()
	{
		return mode;
	}
}
