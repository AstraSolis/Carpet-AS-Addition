package carpet.as.addition.client.mixin;

import carpet.as.addition.client.fakeplayer.FakePlayerCache;
import com.mojang.brigadier.suggestion.Suggestion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * 修改命令补全列表中假人名称的行背景颜色。
 *
 * <p>实现原理：拦截 {@code SuggestionsList.render} 内的所有 {@code GuiGraphics.fill} 调用。
 * 每个补全行的背景填充高度恒为 12px，据此从其他边框（1px）区分。
 * 通过 y 坐标反推当前行索引，再对建议文本做假人名称匹配，对假人行应用绿色背景。
 */
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.gui.components.CommandSuggestions$SuggestionsList")
public abstract class CommandSuggestionsMixin {

    @Shadow
    private int offset;

    @Final @Shadow
    private Rect2i rect;

    @Final @Shadow
    private List<Suggestion> suggestionList;

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
            )
    )
    private void redirectSuggestionFill(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
        if (y2 - y1 == 12) {
            int n = (y1 - this.rect.getY()) / 12;
            int idx = n + this.offset;
            if (idx >= 0 && idx < this.suggestionList.size()) {
                String text = this.suggestionList.get(idx).getText();
                if (FakePlayerCache.isFakePlayerByName(text)) {
                    guiGraphics.fill(x1, y1, x2, y2, FakePlayerCache.NAMETAG_BG_COLOR);
                    return;
                }
            }
        }
        guiGraphics.fill(x1, y1, x2, y2, color);
    }
}
