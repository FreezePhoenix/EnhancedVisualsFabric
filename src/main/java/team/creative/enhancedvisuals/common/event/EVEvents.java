package team.creative.enhancedvisuals.common.event;

import java.lang.reflect.Field;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import team.creative.enhancedvisuals.EnhancedVisuals;
import team.creative.enhancedvisuals.client.EVClient;
import team.creative.enhancedvisuals.client.VisualManager;
import team.creative.enhancedvisuals.client.sound.SoundMuteHandler;
import team.creative.enhancedvisuals.common.packet.DamagePacket;
import team.creative.enhancedvisuals.common.packet.ExplosionPacket;
import team.creative.enhancedvisuals.common.packet.PotionPacket;
import team.creative.enhancedvisuals.mixin.EntityAccessor;
import team.creative.enhancedvisuals.mixin.ExplosionAccessor;

public class EVEvents {
    public void explosion(Explosion explosion, List<Entity> affected) {
             Vec3 position = new Vec3(((ExplosionAccessor) explosion).getX(), ((ExplosionAccessor) explosion).getY(), ((ExplosionAccessor) explosion).getZ());
                ExplosionPacket packet = new ExplosionPacket(position, ((ExplosionAccessor) explosion).getRadius(), ((ExplosionAccessor) explosion).getSource() != null ? (((ExplosionAccessor) explosion).getSource()).getId() : -1);
                for (Entity entity : affected)
                    if (entity instanceof ServerPlayer)
                        EnhancedVisuals.NETWORK.sendToClient(packet, (ServerPlayer) entity);
    }

    public void impact(Projectile projectile) {
        if (projectile instanceof ThrownPotion entity && !projectile.level.isClientSide) {
            AABB axisalignedbb = entity.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
            List<LivingEntity> list = entity.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            if (!list.isEmpty()) {
                for (LivingEntity livingentity : list) {
                    if (livingentity.isAffectedByPotions() && livingentity instanceof ServerPlayer) {
                        double d0 = entity.distanceToSqr(livingentity);
                        if (d0 < 16.0D)
                            EnhancedVisuals.NETWORK.sendToClient(new PotionPacket(Math.sqrt(d0), entity.getItem()), (ServerPlayer) livingentity);
                    }
                }
            }
        }
    }

    public void damage(Player target, DamageSource source, float damage) {
        if (EnhancedVisuals.CONFIG.enableDamageDebug)
            target.sendMessage(new TextComponent(source.msgId + "," + source.getLocalizedDeathMessage(target)
                                                                                      .getString()), Util.NIL_UUID);
        EnhancedVisuals.NETWORK.sendToClient(new DamagePacket(target, source, damage), (ServerPlayer) target);
    }

    @Environment(EnvType.CLIENT)
    public void clientTick(WorldRenderContext event) {
        if (EVClient.shouldTick()) {
            Player player = Minecraft.getInstance().player;
            VisualManager.onTick(player);
        }
        SoundMuteHandler.tick();
    }
    public static boolean areEyesInWater(Player player) {
        return ((EntityAccessor) player).getWasEyeInWater();
    }
}
