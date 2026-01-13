package wtf.cheeze.sbt.features.misc;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

public class HudElementHider {
    public static void hideElements() {
        HudElementRegistry.replaceElement(VanillaHudElements.ARMOR_BAR, hudElement -> {
            if (SBTConfig.get().hudTweaks.noRenderArmor && SkyblockData.inSB) return (context, tickCounter) -> {};
            return hudElement;
        });

        HudElementRegistry.replaceElement(VanillaHudElements.HEALTH_BAR, hudElement -> {
            if (SBTConfig.get().hudTweaks.noRenderHearts && SkyblockData.inSB && (!SBTConfig.get().hudTweaks.showHearsInRift || SkyblockData.location != Location.RIFT)) return (context, tickCounter) -> {};
            return hudElement;
        });

        HudElementRegistry.replaceElement(VanillaHudElements.FOOD_BAR, hudElement -> {
            if (SBTConfig.get().hudTweaks.noRenderHunger && SkyblockData.inSB) return (context, tickCounter) -> {};
            return hudElement;
        });

        HudElementRegistry.replaceElement(VanillaHudElements.STATUS_EFFECTS, hudElement -> {
            if (SBTConfig.get().hudTweaks.noRenderPotionOverlay && SkyblockData.inSB) return (context, tickCounter) -> {};
            return hudElement;
        });

        HudElementRegistry.replaceElement(VanillaHudElements.MOUNT_HEALTH, hudElement -> {
            if (SBTConfig.get().hudTweaks.noRenderMountHealth && SkyblockData.inSB) return (context, tickCounter) -> {};
            return hudElement;
        });

        HudElementRegistry.replaceElement(VanillaHudElements.BOSS_BAR, hudElement -> {
            if (SBTConfig.get().hudTweaks.noRenderBossBar && SkyblockData.inSB) return (context, tickCounter) -> {};
            return hudElement;
        });
    }
}
