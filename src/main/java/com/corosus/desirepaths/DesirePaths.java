package com.corosus.desirepaths;

import com.corosus.desirepaths.config.ConfigDesirePaths;
import com.corosus.desirepaths.config.ConfigDesirePathsDeveloper;
import modconfig.ConfigMod;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = DesirePaths.MODID, version = DesirePaths.VERSION, dependencies="required-after:coroutil@[1.12.1-1.2.14,);after:quark")
public class DesirePaths
{
    public static final String MODID = "desirepaths";
    public static final String VERSION = "${version}";
    
    public static Block dirt_6; //fully worn (our custom dirt)
    public static Block dirt_5;
    public static Block dirt_4;
    public static Block dirt_3;
    public static Block dirt_2;
    public static Block dirt_1; //barely worn
    
    @SidedProxy(clientSide = "com.corosus.desirepaths.ClientProxy", serverSide = "com.corosus.desirepaths.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {

        ConfigMod.addConfigFile(event, new ConfigDesirePaths());
        if (ConfigDesirePaths.enableAdvancedDeveloperConfigFiles) {
            ConfigMod.addConfigFile(event, new ConfigDesirePathsDeveloper());
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init();
    	
    	
    	MinecraftForge.EVENT_BUS.register(new EventHandlerForge());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.initPost();
    }
}
