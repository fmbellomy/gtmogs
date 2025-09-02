package com.quantumgarbage.gtmogs.integration.map;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MapIntegrationUtils {
    public static int getItemColor(Block b) {
        return b.defaultMapColor().col;
    }
    public static ResourceLocation getItemIcon(Block b) {
        return ResourceLocation.parse(BuiltInRegistries.BLOCK.getKey(b).toString());
    }

    public static TextureAtlasSprite getFirstBlockFace(Block b){
        RandomSource random = RandomSource.create();
        var sprite = Minecraft.getInstance().getBlockRenderer().getBlockModel(b.defaultBlockState()).getQuads(b.defaultBlockState(), Direction.NORTH, random).getFirst().getSprite();
        return sprite;
    }
}
