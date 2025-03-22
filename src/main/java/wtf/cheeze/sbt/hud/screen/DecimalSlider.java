package wtf.cheeze.sbt.hud.screen;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class DecimalSlider extends SliderWidget {

    private final double min;
    private final double max;
    private final double step;
    private Consumer<Double> valueConsumer;

    public double actualValue() {
        return Math.round((min + (max - min) * this.value) / step) * step;
    }

    public DecimalSlider(int x, int y, int width, int height, Text text, double value, double min, double max, double step, Consumer<Double> valueConsumer) {
        super(x, y, width, height, text, value);
        this.min = min;
        this.max = max;
        this.step = step;
        this.valueConsumer = valueConsumer;


    }

    @Override
    protected void updateMessage() {
        this.setMessage(Text.literal(String.format("%.1f", actualValue())));
    }

    @Override
    protected void applyValue() {
        this.valueConsumer.accept(actualValue());
    }
}
