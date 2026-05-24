package carpet.as.addition.client.mixin;

import carpet.as.addition.client.fakeplayer.FakePlayerCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 拦截原版玩家离开包，主动从假人缓存中剔除已移除玩家的 UUID。
 *
 * <p>修复场景：
 * <ul>
 *   <li>场景 A：假人移除后同名真实玩家进入——离线模式下两者 UUID 相同，
 *       在服务端延迟广播的同步包到达之前，真实玩家会被误判为假人。
 *       拦截原版的 {@code handlePlayerInfoRemove} 可将 UUID 立即从缓存中剔除，
 *       无需等待 {@code FakePlayerSyncPayload}。</li>
 *   <li>场景 E：{@code isFakePlayerByName} 在缓存未及时更新时，
 *       同名真实玩家上线后其名称会误匹配缓存 UUID，本修复同步解决。</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Inject(method = "handlePlayerInfoRemove", at = @At("TAIL"))
    private void onPlayerInfoRemove(ClientboundPlayerInfoRemovePacket packet, CallbackInfo ci) {
        Set<UUID> updated = new HashSet<>(FakePlayerCache.getAll());
        packet.profileIds().forEach(updated::remove);
        FakePlayerCache.updateUuids(updated);
    }
}
