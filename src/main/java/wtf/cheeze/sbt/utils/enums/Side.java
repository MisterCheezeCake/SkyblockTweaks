package wtf.cheeze.sbt.utils.enums;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;
import wtf.cheeze.sbt.utils.text.TextUtils;

public enum Side implements NameableEnum {
    LEFT,
    RIGHT;

    @SuppressWarnings("MagicNumber")
    public int positionPopup(int screenX) {
        return switch (this) {
            // screenX - popupWidth (80) - 16 (desired offset)
            case LEFT -> screenX - 96;
            // screenX + screenWidth (176) + 16 (desired offset)
            case RIGHT -> screenX + 192;
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal(TextUtils.firstLetterUppercase(this.name().toLowerCase()));
    }
}
