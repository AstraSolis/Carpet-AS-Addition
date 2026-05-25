package carpet.as.addition.client.mixin;

//? if >=1.21 {
import carpet.as.addition.client.fakeplayer.FakePlayerCache;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
//? if >=26.1 {
/*import net.minecraft.client.renderer.state.level.CameraRenderState;
*///?} else {
import net.minecraft.client.renderer.state.CameraRenderState;
//?}
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
 *
 * <p>仅适用于 MC 1.21+（新渲染管线引入 AvatarRenderer）。
 */
@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {

    @Inject(
            //? if >=26.1 {
            /*method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            *///?} else {
            method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            //?}
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
        if (FakePlayerCache.getHeadColor() == -1) return;
        if (Minecraft.getInstance().level == null) return;
        Entity entity = Minecraft.getInstance().level.getEntity(state.id);
        if (entity instanceof Player player && FakePlayerCache.isFakePlayer(player.getUUID())) {
            FakePlayerCache.markFakePlaying();
        }
    }

    @Inject(
            //? if >=26.1 {
            /*method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            *///?} else {
            method = "submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            //?}
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
//?} else {
/*import carpet.as.addition.client.fakeplayer.FakePlayerCache;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 1.20.1 不存在 AvatarRenderer，改为注入 EntityRenderer.renderNameTag() 实现相同的 ThreadLocal 标记逻辑
@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class AvatarRendererMixin {

    @Inject(
            method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void onRenderNameTagHead(Entity entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        FakePlayerCache.clearFakePlaying();
        if (FakePlayerCache.getHeadColor() == -1) return;
        if (entity instanceof Player player && FakePlayerCache.isFakePlayer(player.getUUID())) {
            FakePlayerCache.markFakePlaying();
        }
    }

    @Inject(
            method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN")
    )
    private void onRenderNameTagReturn(Entity entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        FakePlayerCache.clearFakePlaying();
    }
}
*///?}
