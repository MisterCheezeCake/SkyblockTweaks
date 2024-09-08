package wtf.cheeze.sbt.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.features.chat.ChatProtections;
import wtf.cheeze.sbt.features.chat.PartyFeatures;

public class Chat {

    public static ConfigCategory getCategory(ConfigImpl defaults, ConfigImpl config) {
        return ConfigCategory.createBuilder()
                .name(Text.literal("Chat"))
                .tooltip(Text.literal("Settings for chat related features"))
                .group(PartyFeatures.Config.getGroup(defaults, config))
                .group(PartyFeatures.Config.getBlackList(defaults, config))
                .group(ChatProtections.Config.getGroup(defaults, config))
                .build();
    }
}
