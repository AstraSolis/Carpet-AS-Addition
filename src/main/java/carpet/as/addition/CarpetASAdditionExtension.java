package carpet.as.addition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import carpet.as.addition.fakeplayer.FakePlayerTracker;
import carpet.utils.Translations;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.Map;

/**
 * Carpet 扩展实现。
 * <p>
 * 规则注册到 {@link CarpetServer#settingsManager}，与 Carpet 本体共用 {@code /carpet} 与分类浏览
 *（与 carpet-extra 等附属 mod 相同做法）。不要为此 mod 单独创建 {@link SettingsManager}。
 */
public class CarpetASAdditionExtension implements CarpetExtension {

	/**
	 * JVM 生命周期级别的幂等保护标记。
	 * <p>
	 * {@link SettingsManager#registerGlobalRuleObserver} 向全局静态列表追加回调，
	 * 若在同一 JVM 内多次调用（例如开发环境 {@code runServer} 重启服务器但不退出进程），
	 * 会导致回调重复注册，规则变化时广播多次。此标记确保全局观察者仅注册一次。
	 * <p>
	 * <b>开发环境热重载注意：</b>若使用字节码替换类工具（如 Hotswap Agent），
	 * 此标记可能不会随类定义更新而重置，需完整重启 JVM 进程才能重新注册。
	 */
	private static volatile boolean globalObserverRegistered;

	/**
	 * 返回 {@code null}：不注册独立的 /carpet-as-addition 设置界面，规则并入主 Carpet。
	 */
	@Override
	public SettingsManager extensionSettingsManager() {
		return null;
	}

	/**
	 * Carpet 在此阶段创建 {@link CarpetServer#settingsManager} 并解析本体规则之后，
	 * 再调用各扩展的 {@code onGameStarted}。
	 */
	@Override
	public void onGameStarted() {
		CarpetServer.settingsManager.parseSettingsClass(CarpetASAdditionSettings.class);
		// FakePlayerTracker.register() 内部有自己的幂等守卫，可直接调用
		FakePlayerTracker.register();
		if (!globalObserverRegistered) {
			globalObserverRegistered = true;
			// 监听任意规则变化，当三个假人名称标签子规则被切换时重新广播假人列表
			SettingsManager.registerGlobalRuleObserver((source, rule, value) -> {
				String name = rule.name();
				if ("fakePlayerNametagHead".equals(name)
						|| "fakePlayerNametagTab".equals(name)
						|| "fakePlayerNametagCommand".equals(name)) {
					FakePlayerTracker.broadcastFakePlayerList(source.getServer());
				}
			});
		}
	}

	/**
	 * 翻译键使用 {@code carpet.rule.*} 前缀（与主 SettingsManager 的 identifier 一致）。
	 */
	@Override
	public Map<String, String> canHasTranslations(String lang) {
		Map<String, String> translations = Translations.getTranslationFromResourcePath(
			"assets/carpet-as-addition/lang/" + lang + ".json"
		);
		return translations != null ? translations : Collections.emptyMap();
	}

	@Override
	public void onServerLoaded(MinecraftServer server) {
		// 世界加载完成后向已在线玩家同步假人列表（任一子规则已开启且假人先于客户端重连时）
		if (NametagColor.resolve(CarpetASAdditionSettings.fakePlayerNametagHead) != -1
				|| NametagColor.resolve(CarpetASAdditionSettings.fakePlayerNametagTab) != -1
				|| NametagColor.resolve(CarpetASAdditionSettings.fakePlayerNametagCommand) != -1) {
			FakePlayerTracker.broadcastFakePlayerList(server);
		}
	}

	@Override
	public String version() {
		return FabricLoader.getInstance()
			.getModContainer(CarpetASAddition.MOD_ID)
			.map(container -> CarpetASAddition.MOD_ID + " " + container.getMetadata().getVersion().getFriendlyString())
			.orElse(CarpetASAddition.MOD_ID);
	}
}
