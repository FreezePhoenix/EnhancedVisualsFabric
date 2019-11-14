package team.creative.enhancedvisuals.common.death;

import java.util.Random;

import team.creative.creativecore.common.config.CreativeConfig;
import team.creative.creativecore.common.config.CreativeConfigBase;

public class DeathMessages extends CreativeConfigBase {
	
	@CreativeConfig
	public String[] deathMessages = new String[] { "Do you really want to respawn? think of it again.", "Life is hard. Deal with it!", "You are dead ... wait you already know that.", "Did I let the stove on...?", "Should have shot back first...", "Yep, that's messed up...", "Rage incomming!", "I think you dropped something.", "Time for a break?" };
	
	public boolean enabled = true;
	
	private Random rand = new Random();
	
	public String pickRandomDeathMessage() {
		if (deathMessages.length == 0)
			return null;
		return deathMessages[rand.nextInt(deathMessages.length - 1)];
	}
	
	@Override
	public void configured() {
		
	}
	
}