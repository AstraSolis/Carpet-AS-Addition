package carpet.as.addition.client.fakeplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 客户端假人 UUID 缓存。
 * 由服务端通过 {@code FakePlayerSyncPayload} 同步维护。
 * 同时提供渲染时使用的 ThreadLocal 标记，用于在 Mixin 链中传递"当前正在渲染假人"的上下文。
 */
@Environment(EnvType.CLIENT)
public final class FakePlayerCache {

    private FakePlayerCache() {}

    /**
     * 假人相关 UI 元素统一使用的背景色：半透明绿色（alpha=0x40），与普通玩家深色背景区分。
     * 适用于头顶名称标签、Tab 玩家列表行背景、命令补全建议行背景。
     */
    public static final int NAMETAG_BG_COLOR = 0x4000CC44;

    private static final AtomicReference<Set<UUID>> FAKE_PLAYER_UUIDS =
            new AtomicReference<>(Collections.emptySet());

    /** 当前渲染线程是否正在渲染假人名称标签（ThreadLocal 标记）。 */
    private static final ThreadLocal<Boolean> RENDERING_FAKE_PLAYER = ThreadLocal.withInitial(() -> false);

    /** 由网络包处理器调用，更新本地假人 UUID 缓存。 */
    public static void update(Set<UUID> uuids) {
        FAKE_PLAYER_UUIDS.set(Collections.unmodifiableSet(uuids));
    }

    /** 返回当前缓存的假人 UUID 集合快照（不可变）。 */
    public static Set<UUID> getAll() {
        return FAKE_PLAYER_UUIDS.get();
    }

    /** 判断指定 UUID 是否属于假人。 */
    public static boolean isFakePlayer(UUID uuid) {
        return FAKE_PLAYER_UUIDS.get().contains(uuid);
    }

    /**
     * 通过玩家名称判断是否为假人。
     * 在客户端玩家列表中查找匹配名称的 UUID，再与假人缓存比对。
     * 用于命令补全等只有名称字符串、没有 UUID 的场景。
     */
    public static boolean isFakePlayerByName(String name) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        return mc.player.connection.getOnlinePlayers().stream()
                .filter(info -> name.equals(info.getProfile().name()))
                .anyMatch(info -> isFakePlayer(info.getProfile().id()));
    }

    /** 在 submitNameTag 开始前标记：当前正在处理假人名称标签。 */
    public static void markFakePlaying() {
        RENDERING_FAKE_PLAYER.set(true);
    }

    /** 在 submitNameTag 返回后清除标记。 */
    public static void clearFakePlaying() {
        RENDERING_FAKE_PLAYER.remove();
    }

    /** 检查当前渲染上下文是否为假人名称标签。 */
    public static boolean isFakePlaying() {
        return RENDERING_FAKE_PLAYER.get();
    }
}
