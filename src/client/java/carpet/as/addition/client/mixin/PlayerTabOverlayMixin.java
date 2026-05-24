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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改 Tab 玩家列表中假人的行背景颜色。
 *
 * <p>实现原理：
 * <ol>
 *   <li>在 render 的第一个 for-each 循环中，通过拦截 {@code getNameForDisplay} 调用，
 *       按 list 顺序收集各玩家是否为假人的标记。</li>
 *   <li>在 render 的第二个 for 循环中，通过拦截 {@code GuiGraphics.fill} 调用，
 *       以高度恒为 8px 识别玩家行背景填充，对假人行应用绿色背景。</li>
 * </ol>
 * 两个循环均按 list 顺序遍历，因此索引严格对应。
 */
@Environment(EnvType.CLIENT)
@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin {

    /** 假人 Tab 行背景色：半透明绿色，与普通玩家深色背景区分。 */
    @Unique
    private static final int FAKE_PLAYER_TAB_BG_COLOR = FakePlayerCache.NAMETAG_BG_COLOR;

    /**
     * 按渲染顺序记录每个玩家是否为假人，在每次 render 开始时重置。
     * 元素由第一个 for-each 循环（getNameForDisplay redirect）填入，
     * 由第二个 for 循环（fill redirect）按索引消费。
     */
    @Unique
    private final List<Boolean> fakePlayerTabFlags = new ArrayList<>();

    /**
     * 当前渲染行对应的假人标记消费索引。
     * 场景 D 防御：两个循环之间若玩家列表发生变化（高频进出时偶发），
     * 该索引可能超出 fakePlayerTabFlags 的大小；超出时降级为原始颜色，
     * 不崩溃，仅当帧部分行颜色不正确，下一帧自动恢复。
     */
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
     * getNameForDisplay 在第一个 for-each 循环中按 list 顺序依次调用，
     * 与第二个 for 循环的行渲染顺序一致。
     */
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;getNameForDisplay(Lnet/minecraft/client/multiplayer/PlayerInfo;)Lnet/minecraft/network/chat/Component;"
            )
    )
    private Component captureAndGetNameForDisplay(PlayerTabOverlay self, PlayerInfo playerInfo) {
        fakePlayerTabFlags.add(FakePlayerCache.isFakePlayer(playerInfo.getProfile().id()));
        return self.getNameForDisplay(playerInfo);
    }

    /**
     * 拦截 render 内所有 GuiGraphics.fill 调用。
     * 玩家行背景填充高度恒为 8px（{@code fill(z, aa, z+n, aa+8, w)}），
     * 其余 fill（header/footer/外框）高度均为 9 的倍数，可以可靠区分。
     * 对假人行替换为绿色背景色。
     *
     * <p>若行索引超出标记列表（场景 D：两循环间列表变化），直接使用原始颜色。
     * MC 1.21.1 玩家行高固定为 8px。
     */
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
            )
    )
    private void redirectRowFill(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
        boolean isPlayerRow = y2 - y1 == 8;
        boolean hasFlagForRow = fakePlayerRowIndex < fakePlayerTabFlags.size();

        if (isPlayerRow && hasFlagForRow) {
            boolean isFake = fakePlayerTabFlags.get(fakePlayerRowIndex++);
            guiGraphics.fill(x1, y1, x2, y2, isFake ? FAKE_PLAYER_TAB_BG_COLOR : color);
        } else {
            guiGraphics.fill(x1, y1, x2, y2, color);
        }
    }
}
