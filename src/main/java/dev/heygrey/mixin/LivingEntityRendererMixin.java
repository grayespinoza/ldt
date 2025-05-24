package dev.heygrey.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.heygrey.config.Configuration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
  @WrapOperation(
    method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V")
  )
  private void wrapRender(M model, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original) {
    if (!(model instanceof PlayerEntityModel)) {
      model.render(matrixStack, vertexConsumer, light, overlay, color);
      return;
    }
    boolean isFirstPerson = MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON;
    float pitch = MinecraftClient.getInstance().player.getPitch(1.0f);
    if (pitch > Configuration.getInstance().initiatingAngle && isFirstPerson && Configuration.getInstance().affectsFirstPerson
        || pitch > Configuration.getInstance().initiatingAngle && !isFirstPerson && Configuration.getInstance().affectsThirdPerson) {
      model.render(matrixStack, vertexConsumer, light, overlay, getColor(getAlpha(pitch), color));
    } else {
      model.render(matrixStack, vertexConsumer, light, overlay, color);
    }
  }

  public int getAlpha(float pitch) {
    return (int) ((255 - Configuration.getInstance().terminalTransparency) * (1.0f - Math.pow(pitch / 90.0f, 2)) + Configuration.getInstance().terminalTransparency);
  }

  public int getColor(int alpha, int color) {
    return (color & 0xFFFFFF) | (alpha << 24);
  }
}
