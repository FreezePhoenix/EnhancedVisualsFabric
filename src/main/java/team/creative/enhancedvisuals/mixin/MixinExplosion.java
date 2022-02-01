package team.creative.enhancedvisuals.mixin;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import team.creative.enhancedvisuals.EnhancedVisuals;

import java.util.List;
import java.util.Set;

@Mixin(Explosion.class)
public class MixinExplosion {
	@Inject(at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"),
	        method = "Lnet/minecraft/world/level/Explosion;explode()V",
	        locals = LocalCapture.CAPTURE_FAILHARD)
	private void onDetonate(CallbackInfo ci, Set set, int i, float q, int k, int l, int r, int s, int t, int u, List list) {
		EnhancedVisuals.EVENTS.explosion((Explosion)(Object) this, list);
	}
}
