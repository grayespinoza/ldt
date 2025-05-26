package dev.heygrey.mixin;

import dev.heygrey.config.Configuration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin<
    S extends BipedEntityRenderState,
    M extends BipedEntityModel<S>,
    A extends BipedEntityModel<S>> {
  @Inject(
      method =
          "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V",
      at = @At("HEAD"),
      cancellable = true)
  private void injectRender(
      MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider,
      int i,
      S bipedEntityRenderState,
      float f,
      float g,
      CallbackInfo ci) {
    boolean isFirstPerson =
        MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON;
    float pitch = MinecraftClient.getInstance().player.getPitch(1.0f);
    if (pitch > Configuration.getInstance().initiatingAngle
            && isFirstPerson
            && Configuration.getInstance().affectsFirstPerson
            && Configuration.getInstance().affectsArmor
        || pitch > Configuration.getInstance().initiatingAngle
            && !isFirstPerson
            && Configuration.getInstance().affectsThirdPerson
            && Configuration.getInstance().affectsArmor) {
      ci.cancel();
    }
  }
}
