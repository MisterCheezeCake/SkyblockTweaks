package wtf.cheeze.sbt.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionDescription;
import net.minecraft.network.chat.Component;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.features.chat.ChatProtections;
import wtf.cheeze.sbt.features.chat.PartyFeatures;

public class Chat {
    public static ConfigCategory getCategory(ConfigImpl defaults, ConfigImpl config) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("sbt.config.chat"))
                .tooltip(Component.translatable("sbt.config.chat.desc"))
                .group(PartyFeatures.Config.getGroup(defaults, config))
                .group(PartyFeatures.Config.getBlackList(defaults, config))
                .group(ChatProtections.Config.getGroup(defaults, config))
                .build();
    }

    public static final String BASE_KEY = "sbt.config.chat.";

    public static Component key(String key) {
        return Component.translatable(BASE_KEY + key);
    }
    public static OptionDescription keyD(String key) {
        return OptionDescription.of(Component.translatable(BASE_KEY + key + ".desc"));
    }
}
