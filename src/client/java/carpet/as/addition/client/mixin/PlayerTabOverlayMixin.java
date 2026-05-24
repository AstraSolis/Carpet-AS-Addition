package carpet.as.addition.client.mixin;

import carpet.as.addition.client.fakeplayer.FakePlayerCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改 Tab 玩家列表中假人的行背景颜色。
 *
 * <p>实现原理（两步走）：
 * <ol>
 *   <li>在 render 的第一个 for-each 循环中，通过拦截 {@code getNameForDisplay} 调用，
 *       按 list 顺序收集各玩家是否为假人的标记。</li>
 *   <li>在 render 的第二个 for 循环中，通过拦截 {@code GuiGraphics.fill} 调用，
 *       按序消费标记列表，对假人行应用绿色背景。</li>
 * </ol>
 *
 * <p>使用 {@code @Slice} 将第二步的 fill 拦截限定在 {@code getBackgroundColor} 调用之后，
 * 从而自动排除 header 和 backdrop fill（均在此之前）。
 * footer fill（若有）发生在循环之后，此时 {@code fakePlayerRowIndex} 已等于
 * {@code fakePlayerTabFlags.size()}，边界检查确保直接使用原始颜色，无需额外判断。
 */
@Environment(EnvType.CLIENT)
@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin {

    @Unique
    private static final int FAKE_PLAYER_TAB_BG_COLOR = FakePlayerCache.NAMETAG_BG_COLOR;

    /**
     * 按渲染顺序记录每个玩家是否为假人，在每次 render 开始时重置。
     * 由第一个 for-each 循环（getNameForDisplay redirect）填入，
     * 由第二个 for 循环（fill redirect）按索引消费。
     */
    @Unique
    private final List<Boolean> fakePlayerTabFlags = new ArrayList<>();

    /** 当前渲染行对应的假人标记消费索引。 */
    @Unique
    private int fakePlayerRowIndex = 0;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(
            GuiGraphics guiGraphics, int screenWidth, Scoreboard scoreboard,
            @Nullable Objective objective, CallbackInfo ci) {
        fakePlayerTabFlags.clear();
        fakePlayerRowIndex = 0;
    }

    /**
     * 拦截 render 内对 getNameForDisplay 的每次调用，按序收集假人状态。
     */
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;getNameForDisplay(Lnet/minecraft/client/multiplayer/PlayerInfo;)Lnet/minecraft/network/chat/Component;"
            )
    )
    private Component captureAndGetNameForDisplay(PlayerTabOverlay self, PlayerInfo playerInfo) {
        fakePlayerTabFlags.add(FakePlayerCache.isTabEnabled() && FakePlayerCache.isFakePlayer(playerInfo.getProfile().id()));
        return self.getNameForDisplay(playerInfo);
    }

    /**
     * 拦截 render 内 getBackgroundColor 调用之后的所有 fill 调用，对假人行替换绿色背景。
     *
     * <p>{@code @Slice} 确保此 redirect 仅作用于玩家行 fill 与 footer fill，
     * 自动排除 header fill 和 backdrop fill（两者均在 getBackgroundColor 之前）。
     * footer fill 时 fakePlayerRowIndex 已越界，hasFlagForRow 为 false，
     * 直接使用原始颜色，无需任何像素高度判断。
     *
     * <p>场景 D 防御：两循环间列表变化时 fakePlayerRowIndex 可能越界，
     * 越界时降级为原始颜色，不崩溃，下一帧自动恢复。
     */
    @Redirect(
            method = "render",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/Options;getBackgroundColor(I)I"
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
            )
    )
    private void redirectRowFill(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
        boolean hasFlagForRow = fakePlayerRowIndex < fakePlayerTabFlags.size();
        if (hasFlagForRow) {
            boolean isFake = fakePlayerTabFlags.get(fakePlayerRowIndex++);
            guiGraphics.fill(x1, y1, x2, y2, isFake ? FAKE_PLAYER_TAB_BG_COLOR : color);
        } else {
            guiGraphics.fill(x1, y1, x2, y2, color);
        }
    }
}
