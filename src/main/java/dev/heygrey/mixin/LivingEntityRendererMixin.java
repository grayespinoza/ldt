package dev.heygrey.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.heygrey.config.LookDownTransparencyConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<
    T extends LivingEntity,
    S extends LivingEntityRenderState,
    M extends EntityModel<? super S>> {
  @WrapOperation(
      method =
          "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
  private void wrapRender(
      M model,
      PoseStack matrices,
      VertexConsumer vertices,
      int light,
      int overlay,
      int color,
      Operation<Void> original,
      @Local(argsOnly = true) S state) {
    LookDownTransparencyConfiguration ldtConfiguration =
        LookDownTransparencyConfiguration.getInstance();
    Minecraft client = Minecraft.getInstance();

    if (!ldtConfiguration.transparency) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }

    if (!(model instanceof PlayerModel)
        || !(state instanceof PlayerRenderState playerState)) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }

    float pitch = client.player.getXRot(1.0f);
    if (!(pitch > ldtConfiguration.initiatingAngle)) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }

    boolean isSelf = playerState.name.equals(client.player.getName().getString());
    if (!isSelf && !ldtConfiguration.affectsAllPlayers) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }

    boolean isFirstPerson = client.options.getCameraType() == CameraType.FIRST_PERSON;
    if (isFirstPerson && !ldtConfiguration.affectsFirstPerson
        || !isFirstPerson && !ldtConfiguration.affectsThirdPerson) {
      original.call(model, matrices, vertices, light, overlay, color);
      return;
    }

    original.call(model, matrices, vertices, light, overlay, getColor(getAlpha(pitch), color));
  }

  private int getAlpha(float pitch) {
    return (int)
        ((255 - LookDownTransparencyConfiguration.getInstance().terminalTransparency)
                * (1.0f - Math.pow(pitch / 90.0f, 2))
            + LookDownTransparencyConfiguration.getInstance().terminalTransparency);
  }

  private int getColor(int alpha, int color) {
    return (color & 0xFFFFFF) | (alpha << 24);
  }
}
