package com.corosus.desirepaths.config;

import com.corosus.desirepaths.DesirePaths;
import modconfig.ConfigComment;
import modconfig.IConfigCategory;

public class ConfigDesirePaths implements IConfigCategory {

    /*public static int foliageShaderRange = 40;
    public static int Thread_Foliage_Process_Delay = 1000;
    public static boolean extraGrass = false;*/

    public static boolean pathsWearDown = true;
    public static boolean pathsRepair = true;

    @ConfigComment("The block to use for the final worn stage, keep default to allow it to still repair, changing will stop it from repairing once it wears down this far, other good options: 'minecraft:dirt', 'minecraft:grass_path', 'minecraft:dirt 1' for meta use, which is coarse dirt")
    public static String blockFinalWornStage = "desirepaths:grass_worn_6";

    @Override
    public String getName() {
        return "DesirePaths";
    }

    @Override
    public String getRegistryName() {
        return DesirePaths.MODID + getName();
    }

    @Override
    public String getConfigFileName() {
        return getName();
    }

    @Override
    public String getCategory() {
        return getName();
    }

    @Override
    public void hookUpdatedValues() {

    }
}
