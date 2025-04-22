package com.gregtechceu.gtceu.data.datagen;

import com.gregtechceu.gtceu.api.material.material.MarkerMaterials.Color;

import net.minecraft.world.item.Items;

import static com.gregtechceu.gtceu.api.material.material.ItemMaterialData.registerMaterialInfoItems;
import static com.gregtechceu.gtceu.api.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.data.material.GTMaterials.*;

public class TagsHandler {

    public static void initExtraUnificationEntries() {
        registerMaterialInfoItem(ingot, Clay, Items.CLAY_BALL);

        registerMaterialInfoItem(dye, Color.Black, Items.BLACK_DYE);
        registerMaterialInfoItem(dye, Color.Red, Items.RED_DYE);
        registerMaterialInfoItem(dye, Color.Green, Items.GREEN_DYE);
        registerMaterialInfoItem(dye, Color.Brown, Items.BROWN_DYE);
        registerMaterialInfoItem(dye, Color.Blue, Items.BLUE_DYE);
        registerMaterialInfoItem(dye, Color.Purple, Items.PURPLE_DYE);
        registerMaterialInfoItem(dye, Color.Cyan, Items.CYAN_DYE);
        registerMaterialInfoItem(dye, Color.LightGray, Items.LIGHT_GRAY_DYE);
        registerMaterialInfoItem(dye, Color.Gray, Items.GRAY_DYE);
        registerMaterialInfoItem(dye, Color.Pink, Items.PINK_DYE);
        registerMaterialInfoItem(dye, Color.Lime, Items.LIME_DYE);
        registerMaterialInfoItem(dye, Color.Yellow, Items.YELLOW_DYE);
        registerMaterialInfoItem(dye, Color.LightBlue, Items.LIGHT_BLUE_DYE);
        registerMaterialInfoItem(dye, Color.Magenta, Items.MAGENTA_DYE);
        registerMaterialInfoItem(dye, Color.Orange, Items.ORANGE_DYE);
        registerMaterialInfoItem(dye, Color.White, Items.WHITE_DYE);
    }
}
