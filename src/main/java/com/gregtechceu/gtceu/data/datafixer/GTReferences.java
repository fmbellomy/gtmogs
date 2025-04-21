package com.gregtechceu.gtceu.data.datafixer;

import com.mojang.datafixers.DSL;

public class GTReferences {
    // spotless:off
    public static final DSL.TypeReference MATERIAL_NAME = reference("material_name");
    public static final DSL.TypeReference SAVED_DATA_BEDROCK_FLUID = reference("saved_data/gtceu_bedrock_fluid");
    public static final DSL.TypeReference SAVED_DATA_BEDROCK_ORE = reference("saved_data/gtceu_bedrock_ore");
    // spotless:on
    public static DSL.TypeReference reference(final String pName) {
        return new DSL.TypeReference() {

            @Override
            public String typeName() {
                return pName;
            }

            @Override
            public String toString() {
                return "@" + pName;
            }
        };
    }
}
