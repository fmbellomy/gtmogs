package com.gregtechceu.gtceu.client.util;

import net.minecraft.world.phys.AABB;

public class StaticFaceBakery {

    public static final AABB SLIGHTLY_OVER_BLOCK = new AABB(-0.001f, -0.001f, -0.001f,
            1.001f, 1.001f, 1.001f);
    public static final AABB OUTPUT_OVERLAY = new AABB(-.004f, -.004f, -.004f,
            1.004f, 1.004f, 1.004f);
    public static final AABB AUTO_OUTPUT_OVERLAY = new AABB(-.006f, -.006f, -.006f,
            1.006f, 1.006f, 1.006f);
    public static final AABB COVER_OVERLAY = new AABB(-.008f, -.008f, -.008f,
            1.008f, 1.008f, 1.008f);
}
