package wtf.cheeze.sbt.utils.injected;

import org.jetbrains.annotations.Nullable;
import wtf.cheeze.sbt.utils.render.Popup;

public interface SBTHandledScreen {
    @Nullable Popup sbt$getPopup();
    void sbt$setPopup(@Nullable Popup popup);
}
