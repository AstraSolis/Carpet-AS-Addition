package carpet.as.addition.client.mixin;

import carpet.as.addition.client.fakeplayer.FakePlayerCache;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.suggestion.Suggestion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * 修改命令补全列表中假人名称的行背景颜色。
 *
 * <p>实现原理：使用 {@code @Slice} 将 fill redirect 限定在 {@code suggestionList.get()}
 * 首次调用之后（即建议 for 循环体内）。在循环体内，每次 fill 调用之前，
 * {@code suggestion} 局部变量已被赋值，通过 {@code @Local} 直接捕获，
 * 无需任何像素高度判断或坐标反推。
 *
 * <p>边框 fill（scrollbar 指示器，在 for 循环之前）不在切片范围内，自动排除。
 */
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.gui.components.CommandSuggestions$SuggestionsList")
public abstract class CommandSuggestionsMixin {

    /**
     * 拦截建议行背景 fill，对假人名称的建议行应用绿色背景。
     *
     * <p>MC 1.21.x SuggestionsList.render 中，建议行渲染顺序为：
     * <ol>
     *   <li>{@code Suggestion suggestion = suggestionList.get(n + offset)} — fill 前已赋值</li>
     *   <li>{@code guiGraphics.fill(...)} — 绘制行背景</li>
     * </ol>
     * {@code @Slice(from = List.get() 首次调用)} 确保此 redirect 仅作用于 for 循环体内的 fill，
     * 此时 {@code suggestion} 始终在作用域内，可直接通过 {@code @Local} 捕获。
     */
    @Redirect(
            method = "render",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Ljava/util/List;get(I)Ljava/lang/Object;",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
            )
    )
    private void redirectSuggestionFill(
            GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color,
            @Local(name = "suggestion") Suggestion suggestion
    ) {
        if (FakePlayerCache.isCommandEnabled() && FakePlayerCache.isFakePlayerByName(suggestion.getText())) {
            guiGraphics.fill(x1, y1, x2, y2, FakePlayerCache.NAMETAG_BG_COLOR);
            return;
        }
        guiGraphics.fill(x1, y1, x2, y2, color);
    }
}
