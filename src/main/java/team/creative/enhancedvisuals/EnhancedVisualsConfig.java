package team.creative.enhancedvisuals;

import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.api.ICreativeConfig;

public class EnhancedVisualsConfig implements ICreativeConfig {
    
    @CreativeConfig
    public boolean doEffectsInCreative = false;
    
    @CreativeConfig
    public int waterSubstractFactor = 10;
    
    @Override
    public void configured() {
        
    }
    
}
