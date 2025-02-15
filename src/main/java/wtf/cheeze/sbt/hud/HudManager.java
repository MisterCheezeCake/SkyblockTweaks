package wtf.cheeze.sbt.hud;

import wtf.cheeze.sbt.events.HudRenderEvents;
import wtf.cheeze.sbt.features.huds.*;
import wtf.cheeze.sbt.features.mining.EventTimerHUD;
import wtf.cheeze.sbt.features.mining.MiningHUD;

import java.util.ArrayList;

public class HudManager {
    public static final ArrayList<HUD> HUDS = new ArrayList<HUD>();

    public static void registerEvents() {
        HUDS.add(SkillHUDManager.INSTANCE.SKILL_HUD);
        HUDS.add(SkillHUDManager.INSTANCE.SKILL_BAR);
        HUDS.add(new SpeedHUD());
        HUDS.add(new DefenseHUD());
        HUDS.add(new EhpHUD());
        HUDS.add(new DamageReductionHUD());
        HUDS.add(new HealthHUD());
        HUDS.add(new ManaHUD());
        HUDS.add(new OverflowManaHUD());
        HUDS.add(new DrillFuelHUD());
        HUDS.add(new DrillFuelBar());
        HUDS.add(new HealthBar());
        HUDS.add(new ManaBar());
        HUDS.add(new CoordinatesHUD());
        HUDS.add(new RealTimeHUD());
        HUDS.add(new FpsHUD());
        HUDS.add(new TickerHUD());
        HUDS.add(new QuiverHUD());
        HUDS.add(new ArmorStackHUD());
        HUDS.add(new RiftTimeHUD());

        HUDS.add(MiningHUD.INSTANCE);
        HUDS.add(new EventTimerHUD());


        HudRenderEvents.AFTER_MAIN_HUD.register((context, tickCounter) -> {
            HUDS.forEach(hud -> hud.render(context, false));
        });
    }
}
