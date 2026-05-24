package carpet.as.addition.client;

import carpet.as.addition.client.fakeplayer.FakePlayerCache;
import carpet.as.addition.network.FakePlayerSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * 客户端入口。注册客户端网络包处理器。
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
	}
}
