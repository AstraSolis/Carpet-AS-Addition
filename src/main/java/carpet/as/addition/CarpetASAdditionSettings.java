package carpet.as.addition;

import carpet.api.settings.Rule;

/**
 * 本模组的所有 Carpet 规则（开关、数值等）。
 * <p>
 * 新增规则时请使用 {@link #CATEGORY}，统一归入「AS的附加包」分类。
 * <p>
 * 每条规则在 lang 中至少提供 {@code .desc}，建议同时提供 {@code .name} 与 {@code .extra.*}。
 * 键名常量见 {@link CarpetASAdditionTranslations}。
 */
public class CarpetASAdditionSettings {
	/**
	 * 本 mod 在 /carpet 中的专用分类 ID（勿与 Carpet 内置分类混用）。
	 * 显示名称见 lang 文件中的 {@code carpet.category.as_addition}。
	 */
	public static final String CATEGORY = "as_addition";

	/** 示例规则：默认关闭。描述见 en_us.json / zh_cn.json */
	@Rule(categories = {CATEGORY})
	public static boolean exampleRule = false;

	/**
	 * 假人名称标签背景颜色标记：开启后假人名称标签背景变为绿色，与正常玩家区分。
	 * 客户端需同时安装本模组才能看到效果。
	 */
	@Rule(categories = {CATEGORY})
	public static boolean fakePlayerNametag = false;
}
