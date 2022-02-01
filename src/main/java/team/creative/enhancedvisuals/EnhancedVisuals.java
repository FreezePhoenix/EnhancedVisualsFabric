package team.creative.enhancedvisuals;

import java.util.Map.Entry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.config.holder.ConfigHolderDynamic;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;
import team.creative.creativecore.common.network.CreativeNetwork;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.client.EVClient;
import team.creative.enhancedvisuals.common.death.DeathMessages;
import team.creative.enhancedvisuals.common.event.EVEvents;
import team.creative.enhancedvisuals.common.handler.VisualHandlers;
import team.creative.enhancedvisuals.common.packet.DamagePacket;
import team.creative.enhancedvisuals.common.packet.ExplosionPacket;
import team.creative.enhancedvisuals.common.packet.PotionPacket;
import team.creative.enhancedvisuals.common.visual.VisualRegistry;

public class EnhancedVisuals implements ModInitializer, ClientModInitializer {
    
    public static final String MODID = "enhancedvisuals";
    
    public static final Logger LOGGER = LogManager.getLogger(EnhancedVisuals.MODID);
    public static final CreativeNetwork NETWORK = new CreativeNetwork("1.0", LOGGER, new ResourceLocation(EnhancedVisuals.MODID, "main"));
    public static EVEvents EVENTS;
    public static DeathMessages MESSAGES;
    public static EnhancedVisualsConfig CONFIG;

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(EVClient::init);
        CreativeCoreClient.registerClientConfig(MODID);
    }

    @Override
    public void onInitialize() {
        NETWORK.registerType(ExplosionPacket.class, ExplosionPacket::new);
        NETWORK.registerType(DamagePacket.class, DamagePacket::new);
        NETWORK.registerType(PotionPacket.class, PotionPacket::new);
        EVENTS = new EVEvents();
        WorldRenderEvents.END.register(EVENTS::clientTick);
//        ClientTickEvents.END_CLIENT_TICK.register(EVENTS::clientTick);
        VisualHandlers.init();
        MESSAGES = new DeathMessages();

        ConfigHolderDynamic root = CreativeConfigRegistry.ROOT.registerFolder(MODID);
        root.registerValue("general", CONFIG = new EnhancedVisualsConfig(), ConfigSynchronization.CLIENT, false);
        root.registerValue("messages", MESSAGES);
        ConfigHolderDynamic handlers = root.registerFolder("handlers", ConfigSynchronization.CLIENT);
        for (Entry<ResourceLocation, VisualHandler> entry : VisualRegistry.entrySet())
            handlers.registerValue(entry.getKey().getPath(), entry.getValue());
    }
}
