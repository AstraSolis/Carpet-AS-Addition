package carpet.as.addition.client.mixin;

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
 */
@Environment(EnvType.CLIENT)
@Mixin(NameTagFeatureRenderer.Storage.class)
public abstract class NameTagStorageMixin {

    /**
     * 拦截所有 {@code NameTagSubmit} 构造调用，对 {@code backgroundColor} 参数应用假人颜色。
     * 条件 {@code backgroundColor != 0} 跳过无背景（透明）的名称标签绘制调用。
     */
    @ModifyArg(
            method = "add(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SubmitNodeStorage$NameTagSubmit;<init>(Lorg/joml/Matrix4f;FFLnet/minecraft/network/chat/Component;IIID)V"
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
