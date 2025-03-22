package wtf.cheeze.sbt.hud;

import wtf.cheeze.sbt.events.HudRenderEvents;
import wtf.cheeze.sbt.features.huds.*;
import wtf.cheeze.sbt.features.mining.EventTimerHud;
import wtf.cheeze.sbt.features.mining.MiningHud;

import java.util.ArrayList;

public class HudManager {
    public static final ArrayList<HUD> HUDS = new ArrayList<>();

    public static void registerEvents() {
        HUDS.add(SkillHudManager.INSTANCE.SKILL_HUD);
        HUDS.add(SkillHudManager.INSTANCE.SKILL_BAR);
        HUDS.add(new SpeedHud());
        HUDS.add(new DefenseHud());
        HUDS.add(new EhpHud());
        HUDS.add(new DamageReductionHud());
        HUDS.add(new HealthHud());
        HUDS.add(new ManaHud());
        HUDS.add(new OverflowManaHud());
        HUDS.add(new DrillFuelHud());
        HUDS.add(new DrillFuelBar());
        HUDS.add(new HealthBar());
        HUDS.add(new ManaBar());
        HUDS.add(new CoordinatesHud());
        HUDS.add(new RealTimeHud());
        HUDS.add(new FpsHud());
        HUDS.add(new TickerHud());
        HUDS.add(new QuiverHud());
        HUDS.add(new ArmorStackHud());
        HUDS.add(new RiftTimeHud());

        HUDS.add(MiningHud.INSTANCE);
        HUDS.add(new EventTimerHud());


        HudRenderEvents.AFTER_MAIN_HUD.register((context, tickCounter) -> HUDS.forEach(hud -> hud.render(context, false)));
    }
}
