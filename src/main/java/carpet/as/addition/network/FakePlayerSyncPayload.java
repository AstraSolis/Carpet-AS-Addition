package carpet.as.addition.network;

//? if >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
//?} else {
/*import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import java.util.Collections;
*///?}
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
//? if >=1.20.5 {
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
        //? if >=26.1 {
        /*PayloadTypeRegistry.clientboundPlay().register(TYPE, STREAM_CODEC);
        *///?} else {
        PayloadTypeRegistry.playS2C().register(TYPE, STREAM_CODEC);
        //?}
    }
}
//?} else {
/*public class FakePlayerSyncPayload implements FabricPacket {

    public static final PacketType<FakePlayerSyncPayload> TYPE =
            PacketType.create(
                    new ResourceLocation("carpet-as-addition", "fake_player_sync"),
                    FakePlayerSyncPayload::new
            );

    private final Set<UUID> fakePlayerUuids;
    private final int headColor;
    private final int tabColor;
    private final int commandColor;

    public FakePlayerSyncPayload(Set<UUID> fakePlayerUuids, int headColor, int tabColor, int commandColor) {
        this.fakePlayerUuids = Collections.unmodifiableSet(fakePlayerUuids);
        this.headColor = headColor;
        this.tabColor = tabColor;
        this.commandColor = commandColor;
    }

    public FakePlayerSyncPayload(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Set<UUID> uuids = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            uuids.add(buf.readUUID());
        }
        this.fakePlayerUuids = Collections.unmodifiableSet(uuids);
        this.headColor = buf.readInt();
        this.tabColor = buf.readInt();
        this.commandColor = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(fakePlayerUuids.size());
        for (UUID uuid : fakePlayerUuids) {
            buf.writeUUID(uuid);
        }
        buf.writeInt(headColor);
        buf.writeInt(tabColor);
        buf.writeInt(commandColor);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public Set<UUID> fakePlayerUuids() { return fakePlayerUuids; }
    public int headColor() { return headColor; }
    public int tabColor() { return tabColor; }
    public int commandColor() { return commandColor; }

    // 1.20.1 使用 PacketType 机制，不需要显式注册 S2C 数据包类型
    public static void register() {}
}
*///?}
