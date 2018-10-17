package com.sonicjumper.enhancedvisuals.handlers;

import java.awt.Color;

import javax.annotation.Nullable;

import com.creativemd.igcm.api.ConfigBranch;
import com.creativemd.igcm.api.segments.FloatSegment;
import com.creativemd.igcm.api.segments.IntegerSegment;
import com.sonicjumper.enhancedvisuals.EnhancedVisuals;
import com.sonicjumper.enhancedvisuals.VisualManager;
import com.sonicjumper.enhancedvisuals.visuals.types.VisualType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Optional.Method;

public class HeartBeatHandler extends VisualHandler {
	
	public HeartBeatHandler() {
		super("heart beat", "blur & bloody overlay");
	}
	
	public float heartBeatIntensity = 50.0F;
	public int heartBeatDuration = 5;
	public int maxHearts = 6;
	public int minHeartBeatLength = 15;
	public float heartBeatTimeFactor = 5;
	
	public float heartBeatVolume = 1F;
	
	@Override
	public void initConfig(Configuration config) {
		super.initConfig(config);
		heartBeatIntensity = config.getFloat("heartBeatIntensity", name, heartBeatIntensity, 0, 10000, "heartbeat blur intensity");
		heartBeatDuration = config.getInt("heartBeatDuration", name, heartBeatDuration, 0, 10000, "heartbeat blur duration");
		
		maxHearts = config.getInt("heartLevel", name, maxHearts, 0, 20, "below or equal means the heartbeat will take effect");
		minHeartBeatLength = config.getInt("minHeartBeatLength", name, minHeartBeatLength, 0, 100000, "time between heartbeats = player.health * heartBeatHeartFactor + minHeartBeatLength");
		heartBeatTimeFactor = config.getFloat("heartBeatTimeFactor", name, heartBeatTimeFactor, 0, 100000, "time between heartbeats = player.health * heartBeatHeartFactor + minHeartBeatLength");
		
		heartBeatVolume = config.getFloat("heartBeatVolume", name, heartBeatVolume, 0, 1, "How loud the heart beat sounds should be");
	}
	
	@Override
	@Method(modid = "igcm")
	public void registerConfigElements(ConfigBranch branch) {
		branch.registerElement("heartBeatIntensity", new FloatSegment("heartBeatIntensity", 50F, 0, 10000).setToolTip("heartbeat blur intensity"));
		branch.registerElement("heartBeatDuration", new IntegerSegment("heartBeatDuration", 5, 0, 10000).setToolTip("heartbeat blur duration"));
		
		branch.registerElement("maxHearts", new IntegerSegment("maxHearts", 6, 0, 20).setToolTip("below or equal means the heartbeat will take effect"));
		branch.registerElement("minHeartBeatLength", new IntegerSegment("minHeartBeatLength", 15, 0, 100000).setToolTip("time between heartbeats = player.health * heartBeatHeartFactor + minHeartBeatLength"));
		branch.registerElement("heartBeatTimeFactor", new FloatSegment("heartBeatTimeFactor", 5F, 0, 10000).setToolTip("time between heartbeats = player.health * heartBeatHeartFactor + minHeartBeatLength"));
		
		branch.registerElement("heartBeatVolume", new FloatSegment("heartBeatVolume", 1F, 0, 1).setToolTip("How loud the heart beat sounds should be"));
	}
	
	@Override
	@Method(modid = "igcm")
	public void receiveConfigElements(ConfigBranch branch) {
		heartBeatIntensity = (Float) branch.getValue("heartBeatIntensity");
		heartBeatDuration = (Integer) branch.getValue("heartBeatDuration");
		
		maxHearts = (Integer) branch.getValue("maxHearts");
		minHeartBeatLength = (Integer) branch.getValue("minHeartBeatLength");
		heartBeatTimeFactor = (Float) branch.getValue("heartBeatTimeFactor");
		
		heartBeatVolume = (Float) branch.getValue("heartBeatVolume");
	}
	
	public int lowHealthBuffer;
	
	@Override
	public void onTick(@Nullable EntityPlayer player) {
		if (player != null) {
			if (player.getHealth() <= maxHearts) {
				if (this.lowHealthBuffer <= 0) {
					float f1 = (7.0F - player.getHealth()) * 0.2F;
					this.lowHealthBuffer = (int) (player.getHealth() * heartBeatTimeFactor + minHeartBeatLength);
					VisualManager.addVisualWithShading(VisualType.lowhealth, Math.min(0.7F, f1), lowHealthBuffer, lowHealthBuffer, Color.WHITE);
					
					VisualManager.addVisualWithShading(VisualType.blur, Math.min(0.7F, f1) * heartBeatIntensity, heartBeatDuration, heartBeatDuration, Color.WHITE);
					playSound(new ResourceLocation(EnhancedVisuals.modid + ":heartbeatOut"), new BlockPos(player), heartBeatVolume);
				} else if (this.lowHealthBuffer == 5) {
					playSound(new ResourceLocation(EnhancedVisuals.modid + ":heartbeatIn"), new BlockPos(player), heartBeatVolume);
					VisualManager.addVisualWithShading(VisualType.blur, heartBeatIntensity, heartBeatDuration, heartBeatDuration, Color.WHITE);
					
					this.lowHealthBuffer -= 1;
				} else {
					this.lowHealthBuffer -= 1;
				}
			}
		}
	}
}
