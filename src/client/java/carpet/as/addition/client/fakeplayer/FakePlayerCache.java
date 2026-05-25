package carpet.as.addition.client.fakeplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 客户端假人 UUID 缓存。
 * 由服务端通过 {@code FakePlayerSyncPayload} 同步维护。
 * 同时提供渲染时使用的 ThreadLocal 标记，用于在 Mixin 链中传递"当前正在渲染假人"的上下文。
 */
@Environment(EnvType.CLIENT)
public final class FakePlayerCache {

    private FakePlayerCache() {}

    private static final AtomicReference<Set<UUID>> FAKE_PLAYER_UUIDS =
            new AtomicReference<>(Collections.emptySet());

    /**
     * 假人名称缓存，在 {@link #update} 和 {@link #updateUuids} 时从在线玩家列表中同步构建。
     * 用于 {@link #isFakePlayerByName} 的 O(1) 查询，避免每帧遍历。
     */
    private static final AtomicReference<Set<String>> FAKE_PLAYER_NAMES =
            new AtomicReference<>(Collections.emptySet());

    /**
     * 各 UI 位置的颜色 ARGB 值，由服务端规则同步。
     * -1 表示该位置未启用（对应规则值为 "false"）。
     */
    private static final AtomicInteger HEAD_COLOR = new AtomicInteger(-1);
    private static final AtomicInteger TAB_COLOR = new AtomicInteger(-1);
    private static final AtomicInteger COMMAND_COLOR = new AtomicInteger(-1);

    /** 当前渲染线程是否正在渲染假人名称标签（ThreadLocal 标记）。 */
    private static final ThreadLocal<Boolean> RENDERING_FAKE_PLAYER = ThreadLocal.withInitial(() -> false);

    /**
     * 由网络包处理器调用，更新本地假人 UUID 缓存及各 UI 位置的颜色配置，并同步重建名称缓存。
     * 名称缓存的重建仅在数据变更时触发（网络包事件），不在每帧渲染时执行。
     *
     * @param headColor    头顶标签背景色 ARGB，-1 表示禁用
     * @param tabColor     Tab 列表行背景色 ARGB，-1 表示禁用
     * @param commandColor 命令补全行背景色 ARGB，-1 表示禁用
     */
    public static void update(Set<UUID> uuids, int headColor, int tabColor, int commandColor) {
        FAKE_PLAYER_UUIDS.set(Collections.unmodifiableSet(uuids));
        FAKE_PLAYER_NAMES.set(buildNameSet(uuids));
        HEAD_COLOR.set(headColor);
        TAB_COLOR.set(tabColor);
        COMMAND_COLOR.set(commandColor);
    }

    /**
     * 仅更新假人 UUID 缓存，不改变各 UI 位置的颜色配置。
     * 供 {@link carpet.as.addition.client.mixin.ClientPacketListenerMixin} 在玩家离开时
     * 从缓存中剔除 UUID，避免等待下一次服务端同步包。
     */
    public static void updateUuids(Set<UUID> uuids) {
        FAKE_PLAYER_UUIDS.set(Collections.unmodifiableSet(uuids));
        FAKE_PLAYER_NAMES.set(buildNameSet(uuids));
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
     * 通过玩家名称判断是否为假人，O(1) 查询名称缓存。
     * 名称缓存在 {@link #update} 时与 UUID 集合同步重建。
     */
    public static boolean isFakePlayerByName(String name) {
        return FAKE_PLAYER_NAMES.get().contains(name);
    }

    private static Set<String> buildNameSet(Set<UUID> uuids) {
        if (uuids.isEmpty()) return Collections.emptySet();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return Collections.emptySet();
        return mc.player.connection.getOnlinePlayers().stream()
                .filter(info -> uuids.contains(info.getProfile().id()))
                .map(info -> info.getProfile().name())
                .collect(Collectors.toUnmodifiableSet());
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

    /**
     * 返回头顶名称标签的背景色 ARGB 值。
     * -1 表示未启用，Mixin 应跳过颜色替换。
     */
    public static int getHeadColor() {
        return HEAD_COLOR.get();
    }

    /**
     * 返回 Tab 玩家列表行的背景色 ARGB 值。
     * -1 表示未启用，Mixin 应跳过颜色替换。
     */
    public static int getTabColor() {
        return TAB_COLOR.get();
    }

    /**
     * 返回命令补全建议行的背景色 ARGB 值。
     * -1 表示未启用，Mixin 应跳过颜色替换。
     */
    public static int getCommandColor() {
        return COMMAND_COLOR.get();
    }
}
