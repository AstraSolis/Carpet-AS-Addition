package carpet.as.addition;

/**
 * 假人名称标签的可选背景颜色。
 * 颜色值格式为 ARGB，alpha 固定为 0x40（约 25% 不透明度），
 * 与游戏默认名称标签背景的透明度风格统一。
 */
public enum NametagColor {
    green(0x4000CC44),
    red(0x40CC4400),
    blue(0x400044CC),
    yellow(0x40CCCC00),
    orange(0x40CC7700),
    purple(0x408800CC),
    white(0x40CCCCCC),
    aqua(0x4000CCCC);

    /** 颜色的 ARGB 整数值。 */
    public final int argb;

    NametagColor(int argb) {
        this.argb = argb;
    }

    /**
     * 根据规则字符串值解析颜色对应的 ARGB 整数。
     *
     * @param value 规则字符串值（如 "green"、"false"）
     * @return 对应的 ARGB 整数；若值为 "false" 或无法识别则返回 -1（表示禁用）
     */
    public static int resolve(String value) {
        try {
            return NametagColor.valueOf(value).argb;
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }
}
