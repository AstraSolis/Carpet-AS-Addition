package carpet.as.addition;

import carpet.CarpetServer;
import carpet.as.addition.network.FakePlayerSyncPayload;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fabric 模组入口。
 * <p>
 * 负责在模组加载时向 Carpet 注册本附属扩展；规则、命令等逻辑放在 {@link CarpetASAdditionExtension} 中。
 */
public class CarpetASAddition implements ModInitializer {
	/** 与 fabric.mod.json 中的 id 保持一致 */
	public static final String MOD_ID = "carpet-as-addition";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// PayloadTypeRegistry 注册必须在初始化阶段完成，不能延迟到 onGameStarted
		FakePlayerSyncPayload.register();
		// 必须通过 manageExtension 注册，不要用 Mixin 注册扩展
		CarpetServer.manageExtension(new CarpetASAdditionExtension());
		LOGGER.info("已注册 Carpet 扩展");
	}
}