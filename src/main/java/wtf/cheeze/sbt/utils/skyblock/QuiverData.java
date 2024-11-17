package wtf.cheeze.sbt.utils.skyblock;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

public class QuiverData {
    private static final Pattern QUIVER_PATTERN = Pattern.compile("Active Arrow: (.+) \\((\\d+)\\)");

    public final Text arrowName;
    public final int arrowCount;

   QuiverData (ItemStack stack) {
        var lines = stack.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        var line = lines.get(4);
        var matcher = QUIVER_PATTERN.matcher(line.getString());
        if (matcher.matches()) {
            arrowCount = Integer.parseInt(matcher.group(2));
            var type = line.getSiblings().get(1);
            arrowName = Text.literal(type.getString().trim()).setStyle(type.getStyle());
        } else {
            arrowCount = 0;
            arrowName = Text.of("Placeholder Arrow");
        }
    }
}
