package wtf.cheeze.sbt.config.persistent;

import wtf.cheeze.sbt.utils.skyblock.SkyblockConstants;

import java.util.EnumMap;

public class ProfileData {
    public EnumMap<SkyblockConstants.Skills, Integer> skillLevels = new EnumMap<>(SkyblockConstants.Skills.class);
}
