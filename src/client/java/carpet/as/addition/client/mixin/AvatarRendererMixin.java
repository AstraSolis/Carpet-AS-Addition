package carpet.as.addition.client.mixin;

import carpet.as.addition.client.fakeplayer.FakePlayerCache;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 拦截玩家名称标签提交流程。
 * 在 submitNameTag 执行期间，通过 ThreadLocal 标记当前是否正在处理假人，
 * 供 {@link NameTagStorageMixin} 在同步调用链中读取。
 *
 * <p>HEAD 注入先清除上一帧可能残留的脏状态（防御上次调用异常退出），
 * 再按需设置标记；RETURN 注入负责正常路径的清除。
 */
@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {

    @Inject(
            method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At("HEAD")
    )
    private void onSubmitNameTagHead(
            AvatarRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState camera,
            CallbackInfo ci
    ) {
        FakePlayerCache.clearFakePlaying();
        if (Minecraft.getInstance().level == null) return;
        Entity entity = Minecraft.getInstance().level.getEntity(state.id);
        if (entity instanceof Player player && FakePlayerCache.isFakePlayer(player.getUUID())) {
            FakePlayerCache.markFakePlaying();
        }
    }

    @Inject(
            method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At("RETURN")
    )
    private void onSubmitNameTagReturn(
            AvatarRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState camera,
            CallbackInfo ci
    ) {
        FakePlayerCache.clearFakePlaying();
    }
}
