package com.corosus.desirepaths;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = DesirePaths.MODID, version = DesirePaths.VERSION, dependencies="required-after:coroutil")
public class DesirePaths
{
    public static final String MODID = "desirepaths";
    public static final String VERSION = "1.0";
    
    public static Block dirt_6;
    public static Block dirt_5;
    public static Block dirt_4;
    public static Block dirt_3;
    public static Block dirt_2;
    public static Block dirt_1;
    
    @SidedProxy(clientSide = "com.corosus.desirepaths.ClientProxy", serverSide = "com.corosus.desirepaths.CommonProxy")
    public static CommonProxy proxy;
    
    public static List<Block> listDegradeProgression = new ArrayList<Block>();
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init();
    	
    	
    	MinecraftForge.EVENT_BUS.register(new EventHandlerForge());
    }
}
