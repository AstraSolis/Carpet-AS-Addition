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
 * 服务端 → 客户端同步包：传递当前所有假人的 UUID 集合。
 * 服务端在规则开启时发送完整集合，规则关闭时发送空集合。
 */
@NullMarked
public record FakePlayerSyncPayload(Set<UUID> fakePlayerUuids) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<FakePlayerSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("carpet-as-addition", "fake_player_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FakePlayerSyncPayload> STREAM_CODEC =
            CustomPacketPayload.codec(
                    (value, buf) -> {
                        buf.writeVarInt(value.fakePlayerUuids().size());
                        for (UUID uuid : value.fakePlayerUuids()) {
                            buf.writeUUID(uuid);
                        }
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        Set<UUID> uuids = new HashSet<>(size);
                        for (int i = 0; i < size; i++) {
                            uuids.add(buf.readUUID());
                        }
                        return new FakePlayerSyncPayload(uuids);
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
