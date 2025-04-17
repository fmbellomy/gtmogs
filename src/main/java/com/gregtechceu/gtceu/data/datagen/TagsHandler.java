package com.gregtechceu.gtceu.data.datagen;

import com.gregtechceu.gtceu.api.material.material.MarkerMaterials.Color;

import net.minecraft.world.item.Items;

import static com.gregtechceu.gtceu.api.material.material.ItemMaterialData.registerMaterialInfoItems;
import static com.gregtechceu.gtceu.api.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.data.material.GTMaterials.*;

public class TagsHandler {

    public static void initExtraUnificationEntries() {
        registerMaterialInfoItems(ingot, Clay, Items.CLAY_BALL);

        registerMaterialInfoItems(dye, Color.Black, Items.BLACK_DYE);
        registerMaterialInfoItems(dye, Color.Red, Items.RED_DYE);
        registerMaterialInfoItems(dye, Color.Green, Items.GREEN_DYE);
        registerMaterialInfoItems(dye, Color.Brown, Items.BROWN_DYE);
        registerMaterialInfoItems(dye, Color.Blue, Items.BLUE_DYE);
        registerMaterialInfoItems(dye, Color.Purple, Items.PURPLE_DYE);
        registerMaterialInfoItems(dye, Color.Cyan, Items.CYAN_DYE);
        registerMaterialInfoItems(dye, Color.LightGray, Items.LIGHT_GRAY_DYE);
        registerMaterialInfoItems(dye, Color.Gray, Items.GRAY_DYE);
        registerMaterialInfoItems(dye, Color.Pink, Items.PINK_DYE);
        registerMaterialInfoItems(dye, Color.Lime, Items.LIME_DYE);
        registerMaterialInfoItems(dye, Color.Yellow, Items.YELLOW_DYE);
        registerMaterialInfoItems(dye, Color.LightBlue, Items.LIGHT_BLUE_DYE);
        registerMaterialInfoItems(dye, Color.Magenta, Items.MAGENTA_DYE);
        registerMaterialInfoItems(dye, Color.Orange, Items.ORANGE_DYE);
        registerMaterialInfoItems(dye, Color.White, Items.WHITE_DYE);
    }
}
