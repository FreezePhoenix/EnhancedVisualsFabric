package team.creative.enhancedvisuals.client.sound;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.ChannelAccess.ChannelHandle;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import team.creative.creativecore.common.config.premade.curve.DecimalCurve;
import team.creative.enhancedvisuals.mixin.SoundEngineAccessor;
import team.creative.enhancedvisuals.mixin.SoundManagerAccessor;

@Environment(EnvType.CLIENT)
public class SoundMuteHandler {
    public static boolean isMuting = false;
    
    public static SoundEngine engine;
    public static SoundManager manager;
    
    public static DecimalCurve muteGraph;
    public static int timeTick = 0;
    
    public static void tick() {
        if (isMuting) {
            double factor = muteGraph.valueAt(timeTick);
            
            if (factor <= 0)
                endMuting();
            else {
                setMuteVolume((float) (1 - factor));
                timeTick++;
            }
        }
    }
    
    public static float getClampedVolume(float volume) {
        return volume * (isMuting ? (float) (1 - muteGraph.valueAt(timeTick)) : 1);
    }

    public static Map<SoundInstance, ChannelAccess.ChannelHandle> getSounds() {
        return ((SoundEngineAccessor)engine).getInstanceToChannel();
    }
    
    public static void setMuteVolume(float muteVolume) {
        if (!isMuting)
            return;
        
        getSounds().forEach((p_217926_1_, p_217926_2_) -> {
            float f = (float) ((SoundEngineAccessor)engine).invokeCalculateVolume(p_217926_1_);
            p_217926_2_.execute((p_217923_1_) -> {
                p_217923_1_.setVolume(f);

            });
        });
    }
    
    public static boolean startMuting(DecimalCurve muteGraph) {
        if (engine == null) {
            manager = Minecraft.getInstance().getSoundManager();
            engine = ((SoundManagerAccessor) manager).getSoundEngine();
        }
        
        if (isMuting && SoundMuteHandler.muteGraph.valueAt(timeTick) > muteGraph.valueAt(0)) {
            SoundMuteHandler.muteGraph = muteGraph;
            SoundMuteHandler.timeTick = 0;
            tick();
            return true;
        } else if (!isMuting) {
            SoundMuteHandler.muteGraph = muteGraph;
            SoundMuteHandler.timeTick = 0;
            isMuting = true;
            tick();
            return true;
        }
        
        return false;
    }
    
    public static void endMuting() {
        setMuteVolume(1F);
        isMuting = false;
    }
    
}
