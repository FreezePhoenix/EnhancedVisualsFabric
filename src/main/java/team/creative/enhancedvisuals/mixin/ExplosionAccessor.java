package team.creative.enhancedvisuals.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Explosion.class)
public interface ExplosionAccessor {
	@Accessor("x")
	double getX();
	@Accessor("y")
	double getY();
	@Accessor("z")
	double getZ();
	@Accessor
	float getRadius();
	@Accessor
	Entity getSource();
}
