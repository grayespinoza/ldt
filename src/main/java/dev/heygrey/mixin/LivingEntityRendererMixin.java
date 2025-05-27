package dev.heygrey.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.heygrey.config.Configuration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<
    T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
  @WrapOperation(
      method =
          "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
  private void wrapRender(
      M model,
      MatrixStack matrixStack,
      VertexConsumer vertexConsumer,
      int light,
      int overlay,
      int color,
      Operation<Void> original,
      @Local(argsOnly = true) S state) {
    if (!(model instanceof PlayerEntityModel)
        || !(state instanceof PlayerEntityRenderState playerState)) {
      original.call(model, matrixStack, vertexConsumer, light, overlay, color);
      return;
    }
    float pitch = MinecraftClient.getInstance().player.getPitch(1.0f);
    if (!(pitch > Configuration.getInstance().initiatingAngle)) {
      original.call(model, matrixStack, vertexConsumer, light, overlay, color);
      return;
    }
    boolean isSelf =
        playerState.name.equals(MinecraftClient.getInstance().player.getName().getString());
    if (!isSelf && !Configuration.getInstance().affectsAllPlayers) {
      original.call(model, matrixStack, vertexConsumer, light, overlay, color);
      return;
    }
    boolean isFirstPerson =
        MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON;
    if (isFirstPerson && !Configuration.getInstance().affectsFirstPerson
        || !isFirstPerson && !Configuration.getInstance().affectsThirdPerson) {
      original.call(model, matrixStack, vertexConsumer, light, overlay, color);
      return;
    }
    original.call(
        model, matrixStack, vertexConsumer, light, overlay, getColor(getAlpha(pitch), color));
  }

  private int getAlpha(float pitch) {
    return (int)
        ((255 - Configuration.getInstance().terminalTransparency)
                * (1.0f - Math.pow(pitch / 90.0f, 2))
            + Configuration.getInstance().terminalTransparency);
  }

  private int getColor(int alpha, int color) {
    return (color & 0xFFFFFF) | (alpha << 24);
  }
}
