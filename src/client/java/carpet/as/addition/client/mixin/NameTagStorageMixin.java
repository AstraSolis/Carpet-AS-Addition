package carpet.as.addition.client.mixin;

//? if >=1.21 {
import carpet.as.addition.client.fakeplayer.FakePlayerCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 修改假人名称标签的背景颜色。
 * 通过 {@link ModifyArg} 拦截 {@code NameTagSubmit} 构造时的 {@code backgroundColor} 参数（index=6）。
 * ThreadLocal 标记由 {@link AvatarRendererMixin} 在同步调用链上游设置。
 *
 * <p>仅适用于 MC 1.21+（新渲染管线引入 NameTagFeatureRenderer.Storage）。
 */
@Environment(EnvType.CLIENT)
@Mixin(NameTagFeatureRenderer.Storage.class)
public abstract class NameTagStorageMixin {

    /**
     * 拦截所有 {@code NameTagSubmit} 构造调用，对 {@code backgroundColor} 参数应用假人颜色。
     * 条件 {@code backgroundColor != 0} 跳过无背景（透明）的名称标签绘制调用。
     */
    @ModifyArg(
            //? if >=26.1 {
            /*method = "add(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            *///?} else {
            method = "add(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/CameraRenderState;)V",
            //?}
            at = @At(
                    value = "INVOKE",
                    //? if >=26.1 {
                    /*target = "Lnet/minecraft/client/renderer/SubmitNodeStorage$NameTagSubmit;<init>(Lorg/joml/Matrix4fc;FFLnet/minecraft/network/chat/Component;IIID)V"
                    *///?} else {
                    target = "Lnet/minecraft/client/renderer/SubmitNodeStorage$NameTagSubmit;<init>(Lorg/joml/Matrix4f;FFLnet/minecraft/network/chat/Component;IIID)V"
                    //?}
            ),
            index = 6
    )
    private int modifyFakePlayerBgColor(int backgroundColor) {
        int color = FakePlayerCache.getHeadColor();
        if (backgroundColor != 0 && color != -1 && FakePlayerCache.isFakePlaying()) {
            return color;
        }
        return backgroundColor;
    }
}
//?} else {
/*import carpet.as.addition.client.fakeplayer.FakePlayerCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// 1.20.1 不存在 NameTagFeatureRenderer.Storage，改为注入 EntityRenderer.renderNameTag()
// 通过 @ModifyArg 拦截 Font.drawInBatch() 的 backgroundColor 参数（index=8）实现相同效果
@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class NameTagStorageMixin {

    @ModifyArg(
            method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I",
                    ordinal = 0
            ),
            index = 8
    )
    private int modifyFakePlayerBgColor(int backgroundColor) {
        int color = FakePlayerCache.getHeadColor();
        if (backgroundColor != 0 && color != -1 && FakePlayerCache.isFakePlaying()) {
            return color;
        }
        return backgroundColor;
    }
}
*///?}
