package team.creative.enhancedvisuals.common.handler;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.event.SelectEndermanEvent;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.api.type.VisualTypeOverlay;
import team.creative.enhancedvisuals.client.VisualManager;

public class SlenderHandler extends VisualHandler {
	
	@CreativeConfig
	public double defaultIntensity = 0;
	
	@CreativeConfig
	public double maxIntensity = 0.3;
	
	@CreativeConfig
	public double distanceFactor = 0.25;
	
	@CreativeConfig
	public VisualType slender = new VisualTypeOverlay("slender", 50);
	
	public Visual slenderVisual;
	
	@Override
	public void tick(@Nullable PlayerEntity player) {
		if (slenderVisual == null) {
			slenderVisual = new Visual(slender, 0);
			VisualManager.add(slenderVisual);
		}
		
		float intensity = (float) defaultIntensity;
		
		if (player != null) {
			float modifier = 0.0F;
			double d0 = player.func_226277_ct_();
			double d1 = player.func_226278_cu_();
			double d2 = player.func_226281_cx_();
			
			AxisAlignedBB box = player.getBoundingBox();
			box = box.grow(16, 16, 16);
			
			SelectEndermanEvent event = new SelectEndermanEvent(new EntityPredicate());
			MinecraftForge.EVENT_BUS.post(event);
			if (!event.isCanceled()) {
				EndermanEntity mob = player.world.getClosestEntityWithinAABB(EndermanEntity.class, event.predicate, player, d0, d1, d2, box);
				if (mob != null) {
					float distModifier = (float) (1.0F / Math.pow(Math.sqrt(Math.pow(d0 - mob.func_226277_ct_(), 2) + Math.pow(d1 - mob.func_226278_cu_(), 2) + Math.pow(d2 - mob.func_226281_cx_(), 2)) / 3.0D, 2));
					if (distModifier > modifier) {
						modifier = distModifier;
						if (modifier > 3.5F) {
							modifier = 3.5F;
						}
					}
					
					slenderVisual.opacity = (float) Math.max(defaultIntensity, Math.min(maxIntensity, distanceFactor * modifier));
				}
			}
		}
		
		slenderVisual.opacity = intensity;
	}
}