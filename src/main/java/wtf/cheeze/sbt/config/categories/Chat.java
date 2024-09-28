package wtf.cheeze.sbt.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionDescription;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.features.chat.ChatProtections;
import wtf.cheeze.sbt.features.chat.PartyFeatures;

public class Chat {

    public static ConfigCategory getCategory(ConfigImpl defaults, ConfigImpl config) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("sbt.config.chat"))
                .tooltip(Text.translatable("sbt.config.chat.desc"))
                .group(PartyFeatures.Config.getGroup(defaults, config))
                .group(PartyFeatures.Config.getBlackList(defaults, config))
                .group(ChatProtections.Config.getGroup(defaults, config))
                .build();
    }

    public static final String BASE_KEY = "sbt.config.chat.";

    public static Text key(String key) {
        return Text.translatable(BASE_KEY + key);
    }
    public static OptionDescription keyD(String key) {
        return OptionDescription.of(Text.translatable(BASE_KEY + key + ".desc"));
    }
}
