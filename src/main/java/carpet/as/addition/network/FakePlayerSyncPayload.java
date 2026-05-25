package carpet.as.addition.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 服务端 → 客户端同步包：传递当前所有假人的 UUID 集合及各 UI 位置的颜色配置。
 * 服务端在任意子规则开启时发送完整集合，全部关闭时发送空集合。
 *
 * @param fakePlayerUuids 当前在线假人的 UUID 集合；全部规则关闭时为空集合
 * @param headColor       头顶名称标签背景色 ARGB 值；-1 表示该位置未启用
 * @param tabColor        Tab 玩家列表行背景色 ARGB 值；-1 表示该位置未启用
 * @param commandColor    命令补全建议行背景色 ARGB 值；-1 表示该位置未启用
 */
@NullMarked
public record FakePlayerSyncPayload(
        Set<UUID> fakePlayerUuids,
        int headColor,
        int tabColor,
        int commandColor
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<FakePlayerSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("carpet-as-addition", "fake_player_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FakePlayerSyncPayload> STREAM_CODEC =
            CustomPacketPayload.codec(
                    (value, buf) -> {
                        buf.writeVarInt(value.fakePlayerUuids().size());
                        for (UUID uuid : value.fakePlayerUuids()) {
                            buf.writeUUID(uuid);
                        }
                        buf.writeInt(value.headColor());
                        buf.writeInt(value.tabColor());
                        buf.writeInt(value.commandColor());
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        Set<UUID> uuids = new HashSet<>(size);
                        for (int i = 0; i < size; i++) {
                            uuids.add(buf.readUUID());
                        }
                        int head = buf.readInt();
                        int tab = buf.readInt();
                        int command = buf.readInt();
                        return new FakePlayerSyncPayload(uuids, head, tab, command);
                    }
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** 在模组初始化阶段注册此包到 S2C 注册表。 */
    public static void register() {
        PayloadTypeRegistry.playS2C().register(TYPE, STREAM_CODEC);
    }
}
