package carpet.as.addition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
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
		if (CarpetASAdditionSettings.exampleRule) {
			CarpetASAddition.LOGGER.info("exampleRule 已开启");
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
