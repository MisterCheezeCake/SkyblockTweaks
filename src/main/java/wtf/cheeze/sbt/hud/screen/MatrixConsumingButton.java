package wtf.cheeze.sbt.hud.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class MatrixConsumingButton extends ButtonWidget {
    private final Consumer<MatrixStack> matrixConsumer;
    public MatrixConsumingButton(Text message, PressAction onPress, int x, int y, int width, int height, Consumer<MatrixStack> matrixConsumer) {
        super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.matrixConsumer = matrixConsumer;
    }

    public MatrixConsumingButton(Text message, PressAction onPress, int x, int y, Consumer<MatrixStack> matrixConsumer) {
        this(message, onPress, x, y, 80, 20, matrixConsumer);
    }

    public MatrixConsumingButton(Text message, PressAction onPress, Consumer<MatrixStack> matrixConsumer) {
        this(message, onPress, 0, 0, matrixConsumer);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();
        matrixConsumer.accept(context.getMatrices());
        super.renderWidget(context, mouseX, mouseY, delta);
        context.getMatrices().pop();
    }
}
