package carpet.as.addition.mixin;

import carpet.as.addition.CarpetASAdditionSettings;
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在 {@link SleepStatus#update} 入口过滤假人，使其不参与睡眠统计。
 * <p>
 * 当 {@code fakePlayerSleepIgnore} 开启时，传入 {@code update} 的玩家列表会先剔除
 * 所有 {@link EntityPlayerMPFake} 实例，使睡眠百分比仅基于真实玩家计算。
 * 规则关闭时原样透传，与原版行为完全一致。
 */
@Mixin(SleepStatus.class)
public abstract class SleepStatusMixin {

    @ModifyVariable(
            method = "update",
            at = @At("HEAD"),
            argsOnly = true
    )
    private List<? extends Player> filterFakePlayersFromSleepCount(List<? extends Player> players) {
        if (!CarpetASAdditionSettings.fakePlayerSleepIgnore || players.isEmpty()) {
            return players;
        }
        List<? extends Player> filtered = players.stream()
                .filter(p -> !(p instanceof EntityPlayerMPFake))
                .collect(Collectors.toCollection(ArrayList::new));
        return filtered.size() == players.size() ? players : filtered;
    }
}
