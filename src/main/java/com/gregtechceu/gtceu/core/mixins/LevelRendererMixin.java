package com.gregtechceu.gtceu.core.mixins;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.MaterialBlock;
import com.gregtechceu.gtceu.api.item.datacomponents.AoESymmetrical;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.common.blockentity.CableBlockEntity;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = LevelRenderer.class, priority = 500)
@OnlyIn(Dist.CLIENT)
public abstract class LevelRendererMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    private @Nullable ClientLevel level;

    @Unique
    private final RandomSource gtceu$modelRandom = RandomSource.create();

    @Inject(method = "renderLevel",
            at = @At(value = "INVOKE",
                     target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;long2ObjectEntrySet()Lit/unimi/dsi/fastutil/objects/ObjectSet;"))
    private void renderLevel(DeltaTracker partialTick, boolean renderBlockOutline, Camera camera,
                             GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f viewMatrix,
                             Matrix4f projectionMatrix, CallbackInfo ci,
                             @Local(ordinal = 0) PoseStack poseStack) {
        if (minecraft.player == null || minecraft.level == null) return;

        ItemStack mainHandItem = minecraft.player.getMainHandItem();
        if (minecraft.player.isShiftKeyDown() ||
                !ToolHelper.hasBehaviorsComponent(mainHandItem) ||
                !(minecraft.hitResult instanceof BlockHitResult hitResult)) {
            return;
        }
        AoESymmetrical aoeDefinition = ToolHelper.getAoEDefinition(mainHandItem);
        if (aoeDefinition.isZero()) return;

        BlockPos hitPos = hitResult.getBlockPos();
        BlockState hitState = level.getBlockState(hitPos);

        SortedSet<BlockDestructionProgress> progresses = destructionProgress.get(hitPos.asLong());
        if (progresses == null || progresses.isEmpty() || !mainHandItem.isCorrectToolForDrops(hitState)) return;
        BlockDestructionProgress progress = progresses.last();

        UseOnContext context = new UseOnContext(minecraft.player, InteractionHand.MAIN_HAND, hitResult);
        var positions = ToolHelper.getHarvestableBlocks(aoeDefinition, context);

        Vec3 vec3 = camera.getPosition();
        double camX = vec3.x();
        double camY = vec3.y();
        double camZ = vec3.z();

        for (BlockPos pos : positions) {
            poseStack.pushPose();
            poseStack.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);
            PoseStack.Pose last = poseStack.last();
            VertexConsumer breakProgressDecal = new SheetedDecalTextureGenerator(
                    this.renderBuffers.crumblingBufferSource()
                            .getBuffer(ModelBakery.DESTROY_TYPES.get(progress.getProgress())),
                    last, 1.0f);
            ModelData modelData = level.getModelData(pos);
            this.minecraft.getBlockRenderer().renderBreakingTexture(level.getBlockState(pos), pos,
                    level, poseStack, breakProgressDecal, modelData);
            poseStack.popPose();
        }
    }

    @Shadow
    private static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape,
                                    double x, double y, double z,
                                    float red, float green, float blue, float alpha) {
        throw new AssertionError();
    }

    @WrapOperation(method = "renderLevel",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void gtceu$handleAOEOutline(LevelRenderer instance, PoseStack poseStack, VertexConsumer consumer,
                                        Entity entity, double camX, double camY, double camZ,
                                        BlockPos pos, BlockState state, Operation<Void> original) {
        if (minecraft.player == null || level == null) return;

        ItemStack mainHandItem = minecraft.player.getMainHandItem();

        if (state.isAir() || minecraft.player.isShiftKeyDown() || !level.isInWorldBounds(pos) ||
                !mainHandItem.isCorrectToolForDrops(state) || !ToolHelper.hasBehaviorsComponent(mainHandItem) ||
                !(minecraft.hitResult instanceof BlockHitResult hitResult)) {
            gtceu$renderContextAwareOutline(instance, poseStack, consumer, entity, camX, camY, camZ,
                    pos, state, original);
            return;
        }

        UseOnContext context = new UseOnContext(minecraft.player, InteractionHand.MAIN_HAND, hitResult);
        var blocks = ToolHelper.getHarvestableBlocks(ToolHelper.getAoEDefinition(mainHandItem), context);
        blocks.sort((o1, o2) -> {
            if (level.getBlockState(o1).getBlock() instanceof MaterialBlock) {
                if (level.getBlockState(o2).getBlock() instanceof MaterialBlock) {
                    return 0;
                }
                return 1;
            }
            if (level.getBlockState(o2).getBlock() instanceof MaterialBlock) {
                return -1;
            }
            return 0;
        });
        blocks.forEach(blockPos -> gtceu$renderContextAwareOutline(instance, poseStack, consumer, entity,
                camX, camY, camZ, blockPos, level.getBlockState(blockPos), original));
    }

    @Unique
    private void gtceu$renderContextAwareOutline(LevelRenderer instance, PoseStack poseStack, VertexConsumer consumer,
                                                 Entity entity, double camX, double camY, double camZ,
                                                 BlockPos pos, BlockState state, Operation<Void> original) {
        assert level != null;
        var rendererCfg = ConfigHolder.INSTANCE.client.renderer;
        int rgb = 0;
        boolean doRenderColoredOutline = false;

        // spotless:off
        // if it's translucent and a material block, always do the colored outline
        MaterialEntry materialEntry = gtceu$getTranslucentBlockMaterial(state, pos);
        if (!materialEntry.isEmpty()) {
            doRenderColoredOutline = true;
            rgb = materialEntry.material().getMaterialRGB();
        } else if (level.getBlockEntity(pos) instanceof IMachineBlockEntity mbe) {
            if (rendererCfg.coloredTieredMachineOutline && mbe.getMetaMachine() instanceof ITieredMachine tiered) {
                doRenderColoredOutline = true;
                rgb = GTValues.VCM[tiered.getTier()];
            }
        } else if (rendererCfg.coloredWireOutline && level.getBlockEntity(pos) instanceof CableBlockEntity cbe) {
            doRenderColoredOutline = true;
            rgb = GTValues.VCM[GTUtil.getTierByVoltage(cbe.getNodeData().getVoltage())];
        }

        VoxelShape blockShape = state.getShape(level, pos, CollisionContext.of(entity));
        // spotless:on
        if (doRenderColoredOutline) {
            float red = FastColor.ARGB32.red(rgb) / 255f;
            float green = FastColor.ARGB32.green(rgb) / 255f;
            float blue = FastColor.ARGB32.blue(rgb) / 255f;
            renderShape(poseStack, consumer, blockShape,
                    pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ,
                    red, green, blue, 1f);
            return;
        }
        BlockPos.MutableBlockPos mutable = pos.mutable();
        for (BlockPos o : GTUtil.NON_CORNER_NEIGHBOURS) {
            BlockPos offset = mutable.setWithOffset(pos, o);
            if (!gtceu$getTranslucentBlockMaterial(level.getBlockState(offset), offset).isEmpty()) {
                renderShape(poseStack, consumer, blockShape,
                        pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ,
                        0, 0, 0, 1f);
                return;
            }
        }
        original.call(instance, poseStack, consumer, entity, camX, camY, camZ, pos, state);
    }

    @Unique
    private @NotNull MaterialEntry gtceu$getTranslucentBlockMaterial(BlockState state, BlockPos pos) {
        assert level != null;
        // skip non-solid blocks from other mods (like vanilla ice blocks)
        if (!state.isSolidRender(level, pos) && !(state.getBlock() instanceof MaterialBlock)) {
            return MaterialEntry.NULL_ENTRY;
        }

        BakedModel blockModel = minecraft.getBlockRenderer().getBlockModel(state);
        ModelData modelData = level.getModelData(pos);
        modelData = blockModel.getModelData(level, pos, state, modelData);

        gtceu$modelRandom.setSeed(state.getSeed(pos));
        if (blockModel.getRenderTypes(state, gtceu$modelRandom, modelData).contains(RenderType.translucent())) {
            return ChemicalHelper.getMaterialEntry(state.getBlock());
        }
        return MaterialEntry.NULL_ENTRY;
    }
}
