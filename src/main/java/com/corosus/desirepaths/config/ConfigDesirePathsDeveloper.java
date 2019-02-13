package com.corosus.desirepaths.config;

import com.corosus.desirepaths.DesirePaths;
import modconfig.ConfigComment;
import modconfig.IConfigCategory;

public class ConfigDesirePathsDeveloper implements IConfigCategory {

    @ConfigComment("Warning: performance sensitive setting")
    public static double pathWalkWearAmplifier = 1D;

    @ConfigComment("Warning: performance sensitive setting")
    public static double pathRepairAmplifier = 1D;

    @Override
    public String getName() {
        return "DesirePathsDeveloper";
    }

    @Override
    public String getRegistryName() {
        return DesirePaths.MODID + ":" + getName();
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
