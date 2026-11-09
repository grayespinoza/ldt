package dev.heygrey.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.heygrey.config.LookDownTransparencyConfiguration;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorFeatureRendererMixin<
    S extends HumanoidRenderState,
    M extends HumanoidModel<S>,
    A extends HumanoidModel<S>> {
  @Shadow
  protected abstract void setPartVisibility(A humanoidModel, EquipmentSlot slot);

  @WrapOperation(
      method =
          "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V"))
  private void wrapRenderArmor(
      HumanoidArmorLayer renderer,
      PoseStack matrices,
      MultiBufferSource vertexConsumers,
      ItemStack stack,
      EquipmentSlot slot,
      int light,
      A armorModel,
      Operation<Void> original,
      @Local(argsOnly = true) S state) {
    LookDownTransparencyConfiguration ldtConfiguration =
        LookDownTransparencyConfiguration.getInstance();
    Minecraft client = Minecraft.getInstance();

    if (!ldtConfiguration.transparency) {
      original.call(renderer, matrices, vertexConsumers, stack, slot, light, armorModel);
      return;
    }

    if (!(state instanceof PlayerRenderState playerState)) {
      original.call(renderer, matrices, vertexConsumers, stack, slot, light, armorModel);
      return;
    }

    float pitch = client.player.getXRot(1.0f);
    if (!(pitch > ldtConfiguration.initiatingAngle)) {
      original.call(renderer, matrices, vertexConsumers, stack, slot, light, armorModel);
      return;
    }

    boolean isSelf = playerState.name.equals(client.player.getName().getString());
    if (!isSelf && !ldtConfiguration.affectsAllPlayers) {
      original.call(renderer, matrices, vertexConsumers, stack, slot, light, armorModel);
      return;
    }

    if (!ldtConfiguration.affectsArmor) {
      original.call(renderer, matrices, vertexConsumers, stack, slot, light, armorModel);
      return;
    }

    boolean isFirstPerson = client.options.getCameraType() == CameraType.FIRST_PERSON;
    if (isFirstPerson && !ldtConfiguration.affectsFirstPerson
        || !isFirstPerson && !ldtConfiguration.affectsThirdPerson) {
      original.call(renderer, matrices, vertexConsumers, stack, slot, light, armorModel);
      return;
    }

    setPartVisibility(armorModel, slot);
  }
}
