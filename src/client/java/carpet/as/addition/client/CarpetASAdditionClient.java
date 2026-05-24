package carpet.as.addition.client;

import carpet.as.addition.client.fakeplayer.FakePlayerCache;
import carpet.as.addition.network.FakePlayerSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.Collections;

/**
 * 客户端入口。注册客户端网络包处理器与连接生命周期回调。
 */
public class CarpetASAdditionClient implements ClientModInitializer {
	@Override
	@SuppressWarnings("resource")
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
				FakePlayerSyncPayload.TYPE,
				(payload, context) -> context.client().execute(
						() -> FakePlayerCache.update(payload.fakePlayerUuids())
				)
		);

		// 断开连接时立即清空假人缓存，防止残留 UUID 在下一个服务器上误判真实玩家
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
				FakePlayerCache.update(Collections.emptySet())
		);
	}
}
