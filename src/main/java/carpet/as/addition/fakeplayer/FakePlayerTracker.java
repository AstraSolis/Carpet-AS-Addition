package carpet.as.addition.fakeplayer;

import carpet.as.addition.CarpetASAdditionSettings;
import carpet.as.addition.network.FakePlayerSyncPayload;
import carpet.patches.EntityPlayerMPFake;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 服务端假人追踪器。
 * 监听玩家连接/断开事件，在适当时机将假人 UUID 集合同步给客户端。
 */
public final class FakePlayerTracker {

    private static volatile boolean registered;

    private FakePlayerTracker() {}

    /** 注册玩家连接事件监听器。幂等，重复调用无效。 */
    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer joining = handler.player;
            if (joining instanceof EntityPlayerMPFake) {
                // 新假人加入：广播更新后的假人列表给所有真实玩家
                server.execute(() -> broadcastFakePlayerList(server));
            } else {
                // 真实玩家加入：仅向该玩家发送当前假人列表
                server.execute(() -> sendFakePlayerListTo(joining, server));
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (handler.player instanceof EntityPlayerMPFake) {
                // 假人断开：广播更新后的假人列表
                // 此时假人已从玩家列表移除，getFakePlayerUuids 会返回不含该假人的集合
                server.execute(() -> broadcastFakePlayerList(server));
            }
        });
    }

    /**
     * 广播当前假人 UUID 集合给所有在线的真实玩家。
     * 若规则未开启，广播空集合（清除客户端缓存）。
     */
    public static void broadcastFakePlayerList(MinecraftServer server) {
        FakePlayerSyncPayload payload = new FakePlayerSyncPayload(getFakePlayerUuids(server));
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!(player instanceof EntityPlayerMPFake)) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }

    private static void sendFakePlayerListTo(ServerPlayer player, MinecraftServer server) {
        FakePlayerSyncPayload payload = new FakePlayerSyncPayload(getFakePlayerUuids(server));
        ServerPlayNetworking.send(player, payload);
    }

    private static Set<UUID> getFakePlayerUuids(MinecraftServer server) {
        if (!CarpetASAdditionSettings.fakePlayerNametag) {
            return Collections.emptySet();
        }
        return server.getPlayerList().getPlayers().stream()
                .filter(p -> p instanceof EntityPlayerMPFake)
                .map(Entity::getUUID)
                .collect(Collectors.toUnmodifiableSet());
    }
}
