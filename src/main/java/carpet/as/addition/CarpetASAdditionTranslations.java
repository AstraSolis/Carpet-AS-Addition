package carpet.as.addition;

/**
 * 本 mod 在 Carpet 翻译系统中的键名（均使用主 SettingsManager 的 {@code carpet} 前缀）。
 * <p>
 * 新增规则时在此类补充常量，并在 {@code assets/carpet-as-addition/lang/} 下同步填写中英文。
 */
@SuppressWarnings("unused")
public final class CarpetASAdditionTranslations {
	private CarpetASAdditionTranslations() {
	}

	/** 本 mod 专用分类，对应 {@link CarpetASAdditionSettings#CATEGORY} */
	public static final String CATEGORY_AS_ADDITION = "carpet.category.as_addition";

	/** 规则 {@code fakePlayerNametag} 的翻译键前缀 */
	public static final class FakePlayerNametag {
		public static final String NAME = "carpet.rule.fakePlayerNametag.name";
		public static final String DESC = "carpet.rule.fakePlayerNametag.desc";
		public static final String EXTRA_0 = "carpet.rule.fakePlayerNametag.extra.0";
		public static final String EXTRA_1 = "carpet.rule.fakePlayerNametag.extra.1";

		private FakePlayerNametag() {
		}
	}

	/** 规则 {@code fakePlayerSleepIgnore} 的翻译键前缀 */
	public static final class FakePlayerSleepIgnore {
		public static final String NAME = "carpet.rule.fakePlayerSleepIgnore.name";
		public static final String DESC = "carpet.rule.fakePlayerSleepIgnore.desc";
		public static final String EXTRA_0 = "carpet.rule.fakePlayerSleepIgnore.extra.0";
		public static final String EXTRA_1 = "carpet.rule.fakePlayerSleepIgnore.extra.1";

		private FakePlayerSleepIgnore() {
		}
	}
}
