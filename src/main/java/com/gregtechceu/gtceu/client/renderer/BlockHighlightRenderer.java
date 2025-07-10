package com.gregtechceu.gtceu.client.renderer;

import com.gregtechceu.gtceu.api.blockentity.PipeBlockEntity;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.PipeBlockItem;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.IToolGridHighLight;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.api.multiblock.util.RelativeDirection;
import com.gregtechceu.gtceu.api.pipenet.IPipeType;
import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.gregtechceu.gtceu.common.item.behavior.CoverPlaceBehavior;
import com.gregtechceu.gtceu.common.item.tool.rotation.CustomBlockRotations;
import com.gregtechceu.gtceu.data.item.GTItemAbilities;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Set;
import java.util.function.Function;

import static com.gregtechceu.gtceu.utils.GTMatrixUtils.*;

@OnlyIn(Dist.CLIENT)
public class BlockHighlightRenderer {

    public static void renderBlockHighlight(PoseStack poseStack, Camera camera, BlockHitResult target,
                                            MultiBufferSource multiBufferSource, float partialTick) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        var player = mc.player;
        if (level != null && player != null) {
            ItemStack held = player.getMainHandItem();
            BlockPos blockPos = target.getBlockPos();

            Set<GTToolType> toolType = ToolHelper.getToolTypes(held);
            BlockEntity blockEntity = level.getBlockEntity(blockPos);

            Vec3 cameraPos = camera.getPosition();
            // draw tool grid highlight
            if ((!toolType.isEmpty()) || (held.isEmpty() && player.isShiftKeyDown())) {
                IToolGridHighLight gridHighlight = null;
                if (blockEntity instanceof IToolGridHighLight highLight) {
                    gridHighlight = highLight;
                } else if (level.getBlockState(blockPos).getBlock() instanceof IToolGridHighLight highLight) {
                    gridHighlight = highLight;
                } else
                    if (toolType.contains(GTToolType.WRENCH) || held.canPerformAction(GTItemAbilities.WRENCH_ROTATE)) {
                        var behavior = CustomBlockRotations.getCustomRotation(level.getBlockState(blockPos).getBlock());
                        if (behavior != null && behavior.showGrid()) {
                            gridHighlight = new IToolGridHighLight() {

                                @Override
                                public @Nullable ResourceTexture sideTips(Player player, BlockPos pos, BlockState state,
                                                                          Set<GTToolType> toolTypes, ItemStack held,
                                                                          Direction side) {
                                    return behavior.showSideTip(state, side) ? GuiTextures.TOOL_FRONT_FACING_ROTATION :
                                            null;
                                }
                            };
                        }
                    }
                if (gridHighlight == null) {
                    return;
                }
                var state = level.getBlockState(blockPos);
                poseStack.pushPose();
                if (gridHighlight.shouldRenderGrid(player, blockPos, state, held, toolType)) {
                    final IToolGridHighLight finalGridHighlight = gridHighlight;
                    drawGridOverlays(poseStack, multiBufferSource, cameraPos, target,
                            side -> finalGridHighlight.sideTips(player, blockPos, state, toolType, held, side));
                } else {
                    var facing = target.getDirection();
                    var texture = gridHighlight.sideTips(player, blockPos, state, toolType, held, facing);
                    if (texture != null) {
                        RenderSystem.disableDepthTest();
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                        poseStack.translate(facing.getStepX() * 0.01f, facing.getStepY() * 0.01f,
                                facing.getStepZ() * 0.01f);
                        RenderUtil.moveToFace(poseStack,
                                blockPos.getX() - cameraPos.x(),
                                blockPos.getY() - cameraPos.y(),
                                blockPos.getZ() - cameraPos.z(),
                                facing);
                        if (facing.getAxis() == Direction.Axis.Y) {
                            RenderUtil.rotateToFace(poseStack, facing, Direction.SOUTH);
                        } else {
                            RenderUtil.rotateToFace(poseStack, facing, Direction.NORTH);
                        }
                        poseStack.scale(1f / 16, 1f / 16, 0);
                        poseStack.translate(-8, -8, 0);

                        drawResourceTexture(poseStack, multiBufferSource, texture, 0xffffffff,
                                4, 4, 8, 8);

                        RenderSystem.disableBlend();
                        RenderSystem.enableDepthTest();
                    }
                }
                poseStack.popPose();
                return;
            }

            // draw cover grid highlight
            ICoverable coverable = GTCapabilityHelper.getCoverable(level, blockPos, target.getDirection());
            if (coverable != null && CoverPlaceBehavior.isCoverBehaviorItem(held, coverable::hasAnyCover,
                    coverDef -> ICoverable.canPlaceCover(coverDef, coverable))) {
                poseStack.pushPose();

                drawGridOverlays(poseStack, multiBufferSource, cameraPos, target,
                        side -> coverable.hasCover(side) ? null : GuiTextures.TOOL_ATTACH_COVER);

                poseStack.popPose();
            }

            // draw pipe connection grid highlight
            var pipeType = held.getItem() instanceof PipeBlockItem pipeBlockItem ? pipeBlockItem.getBlock().pipeType :
                    null;
            if (pipeType instanceof IPipeType<?> type && blockEntity instanceof PipeBlockEntity<?, ?> pipeBlockEntity &&
                    pipeBlockEntity.getPipeType().type().equals(type.type())) {
                poseStack.pushPose();

                drawGridOverlays(poseStack, multiBufferSource, cameraPos, target,
                        side -> level.isEmptyBlock(blockPos.relative(side)) ?
                                pipeBlockEntity.getPipeTexture(true) : null);

                poseStack.popPose();
            }
        }
    }

    private static float rColour;
    private static float gColour;
    private static float bColour;

    private static void drawGridOverlays(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPos,
                                         BlockHitResult blockHitResult, Function<Direction, ResourceTexture> texture) {
        rColour = gColour = 0.2F + (float) Math.sin((System.currentTimeMillis() % (Mth.PI * 800)) / 800) / 2;
        bColour = 1f;
        var blockPos = blockHitResult.getBlockPos();
        var facing = blockHitResult.getDirection();
        float minX = blockPos.getX();
        float maxX = blockPos.getX() + 1;
        float minY = blockPos.getY();
        float maxY = blockPos.getY() + 1;
        float maxZ = blockPos.getZ() + 1.01f;
        var attachSide = ICoverable.traceCoverSide(blockHitResult);
        var topRight = new Vector3f(maxX, maxY, maxZ);
        var bottomRight = new Vector3f(maxX, minY, maxZ);
        var bottomLeft = new Vector3f(minX, minY, maxZ);
        var topLeft = new Vector3f(minX, maxY, maxZ);
        var shift = new Vector3f(0.25f, 0, 0);
        var shiftVert = new Vector3f(0, 0.25f, 0);

        var cubeCenter = blockPos.getCenter().toVector3f();

        topRight.sub(cubeCenter);
        bottomRight.sub(cubeCenter);
        bottomLeft.sub(cubeCenter);
        topLeft.sub(cubeCenter);

        var south = Direction.SOUTH.step();
        var frontVec = getDirectionAxis(facing);
        var rotationAngle = getRotationAngle(south, frontVec);
        var rotationAxis = getRotationAxis(south, frontVec);
        topRight.rotateAxis(rotationAngle, rotationAxis.x(), rotationAxis.y(), rotationAxis.z());
        bottomRight.rotateAxis(rotationAngle, rotationAxis.x(), rotationAxis.y(), rotationAxis.z());
        bottomLeft.rotateAxis(rotationAngle, rotationAxis.x(), rotationAxis.y(), rotationAxis.z());
        topLeft.rotateAxis(rotationAngle, rotationAxis.x(), rotationAxis.y(), rotationAxis.z());

        Direction front = facing;
        Direction back = facing.getOpposite();
        Direction left = RelativeDirection.LEFT.getActualDirection(facing);
        Direction right = RelativeDirection.RIGHT.getActualDirection(facing);
        Direction top = RelativeDirection.UP.getActualDirection(facing);
        Direction bottom = RelativeDirection.DOWN.getActualDirection(facing);

        ResourceTexture leftBlocked = texture.apply(left);
        ResourceTexture rightBlocked = texture.apply(right);
        ResourceTexture topBlocked = texture.apply(top);
        ResourceTexture bottomBlocked = texture.apply(bottom);
        ResourceTexture frontBlocked = texture.apply(front);
        ResourceTexture backBlocked = texture.apply(back);

        topRight.add(cubeCenter);
        bottomRight.add(cubeCenter);
        bottomLeft.add(cubeCenter);
        topLeft.add(cubeCenter);

        var buffer = bufferSource.getBuffer(RenderType.lines());
        RenderSystem.lineWidth(3);
        var mat = poseStack.last().pose();
        // straight top bottom lines
        drawLine(mat, buffer, new Vector3f(topRight).add(new Vector3f(shift).mul(-1)),
                new Vector3f(bottomRight).add(new Vector3f(shift).mul(-1)));

        drawLine(mat, buffer, new Vector3f(bottomLeft).add(shift), new Vector3f(topLeft).add(shift));

        // straight side to side lines
        drawLine(mat, buffer, new Vector3f(topLeft).add(new Vector3f(shiftVert).mul(-1)),
                new Vector3f(topRight).add(new Vector3f(shiftVert).mul(-1)));

        drawLine(mat, buffer, new Vector3f(bottomLeft).add(shiftVert),
                new Vector3f(bottomRight).add(shiftVert));

        poseStack.pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.translate(facing.getStepX() * 0.01f, facing.getStepY() * 0.01f, facing.getStepZ() * 0.01f);
        RenderUtil.moveToFace(poseStack,
                blockPos.getX() - cameraPos.x(),
                blockPos.getY() - cameraPos.y(),
                blockPos.getZ() - cameraPos.z(),
                facing);
        RenderUtil.rotateToFace(poseStack, facing, Direction.SOUTH);
        poseStack.scale(1f / 16, 1f / 16, 0);
        poseStack.translate(-8, -8, 0);
        poseStack.scale(0.9f, 0.9f, 1);

        if (leftBlocked != null) {
            int color = attachSide == left ? 0xffffffff : 0x44ffffff;
            drawResourceTexture(poseStack, bufferSource, leftBlocked, color, 0, 6, 4, 4);
        }
        if (topBlocked != null) {
            int color = attachSide == top ? 0xffffffff : 0x44ffffff;
            drawResourceTexture(poseStack, bufferSource, topBlocked, color, 6, 12, 4, 4);
        }
        if (rightBlocked != null) {
            int color = attachSide == right ? 0xffffffff : 0x44ffffff;
            drawResourceTexture(poseStack, bufferSource, rightBlocked, color, 12, 6, 4, 4);
        }
        if (bottomBlocked != null) {
            int color = attachSide == bottom ? 0xffffffff : 0x44ffffff;
            drawResourceTexture(poseStack, bufferSource, bottomBlocked, color, 6, 0, 4, 4);
        }
        if (frontBlocked != null) {
            int color = attachSide == front ? 0xffffffff : 0x44ffffff;
            drawResourceTexture(poseStack, bufferSource, frontBlocked, color, 6, 6, 4, 4);
        }
        if (backBlocked != null) {
            int color = attachSide == back ? 0xffffffff : 0x44ffffff;
            drawResourceTexture(poseStack, bufferSource, backBlocked, color, 0, 0, 4, 4);
            drawResourceTexture(poseStack, bufferSource, backBlocked, color, 12, 0, 4, 4);
            drawResourceTexture(poseStack, bufferSource, backBlocked, color, 0, 12, 4, 4);
            drawResourceTexture(poseStack, bufferSource, backBlocked, color, 12, 12, 4, 4);
        }
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        poseStack.popPose();
    }

    private static void drawLine(Matrix4f mat, VertexConsumer buffer, Vector3f from, Vector3f to) {
        var normal = new Vector3f(from).sub(to);

        buffer.addVertex(mat, from.x, from.y, from.z)
                .setColor(rColour, gColour, bColour, 1f)
                .setNormal(normal.x, normal.y, normal.z);
        buffer.addVertex(mat, to.x, to.y, to.z)
                .setColor(rColour, gColour, bColour, 1f)
                .setNormal(normal.x, normal.y, normal.z);
    }

    private static void drawResourceTexture(PoseStack poseStack, MultiBufferSource bufferSource,
                                            ResourceTexture texture, int color,
                                            float x, float y, float w, float h) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.text(texture.imageLocation));
        var pose = poseStack.last().pose();
        float u0 = texture.offsetX, v0 = texture.offsetY;
        float u1 = texture.imageWidth, v1 = texture.imageHeight;
        // spotless:off
        consumer.addVertex(pose, x, y + h, 0).setColor(color).setUv(u0, v0 + v1).setLight(LightTexture.FULL_BRIGHT);
        consumer.addVertex(pose, x + w, y + h, 0).setColor(color).setUv(u0 + u1, v0 + v1).setLight(LightTexture.FULL_BRIGHT);
        consumer.addVertex(pose, x + w, y, 0).setColor(color).setUv(u0 + u1, v0).setLight(LightTexture.FULL_BRIGHT);
        consumer.addVertex(pose, x, y, 0).setColor(color).setUv(u0, v0).setLight(LightTexture.FULL_BRIGHT);
        // spotless:on
    }
}
