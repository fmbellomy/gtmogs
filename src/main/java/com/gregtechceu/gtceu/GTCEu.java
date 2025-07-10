package com.gregtechceu.gtceu;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.material.material.IMaterialRegistry;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.CommonInit;
import com.gregtechceu.gtceu.common.network.GTNetwork;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.forge.AlloyBlastPropertyAddition;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import com.mojang.serialization.Codec;
import dev.emi.emi.config.EmiConfig;
import me.shedaniel.rei.api.client.REIRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@Mod(GTCEu.MOD_ID)
public class GTCEu {

    public static final String MOD_ID = "gtceu";
    private static final ResourceLocation TEMPLATE_LOCATION = ResourceLocation.fromNamespaceAndPath(MOD_ID, "");
    public static final Codec<ResourceLocation> GTCEU_ID = Codec.STRING.comapFlatMap(
            str -> ResourceLocation.read(appendIdString(str)),
            s -> s.getNamespace().equals(MOD_ID) ? s.getPath() : s.toString());

    public static final String NAME = "GTCEu";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @ApiStatus.Internal
    public static IEventBus gtModBus;

    public GTCEu(IEventBus modBus, FMLModContainer container) {
        GTCEuAPI.instance = this;
        GTCEu.gtModBus = modBus;
        ConfigHolder.init();

        // must be set here because of KubeJS compat
        // trying to read this before the pre-init stage
        GTCEuAPI.materialManager = (IMaterialRegistry) GTRegistries.MATERIALS;
        GTCEuAPI.initializeHighTier();
        if (GTCEu.isDev()) {
            ConfigHolder.INSTANCE.recipes.generateLowQualityGems = true;
            ConfigHolder.INSTANCE.compat.energy.enableFEConverters = true;
        }
        CommonInit.init(modBus);

        modBus.addListener(AlloyBlastPropertyAddition::addAlloyBlastProperties);
        modBus.addListener(GTNetwork::registerPayloads);
    }

    public static ResourceLocation id(String path) {
        if (path.isBlank()) {
            return TEMPLATE_LOCATION;
        }

        int i = path.indexOf(':');
        if (i > 0) {
            return ResourceLocation.parse(path);
        } else if (i == 0) {
            path = path.substring(i + 1);
        }
        // only convert it to camel_case if it has any uppercase to begin with
        if (FormattingUtil.hasUpperCase(path)) {
            path = FormattingUtil.toLowerCaseUnderscore(path);
        }
        return TEMPLATE_LOCATION.withPath(path);
    }

    public static String appendIdString(String id) {
        int i = id.indexOf(':');
        if (i > 0) {
            return id;
        } else if (i == 0) {
            return MOD_ID + id;
        } else {
            return MOD_ID + ":" + id;
        }
    }

    /**
     * @return if we're running in a production environment
     */
    public static boolean isProd() {
        return FMLLoader.isProduction();
    }

    /**
     * @return if we're not running in a production environment
     */
    public static boolean isDev() {
        return !isProd();
    }

    /**
     * @return if we're running data generation
     */
    public static boolean isDataGen() {
        return DatagenModLoader.isRunningDataGen();
    }

    /**
     * A friendly reminder that the server instance is populated on the server side only, so null/side check it!
     * 
     * @return the current minecraft server instance
     */
    public static MinecraftServer getMinecraftServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    /**
     * @param modId the mod id to check for
     * @return if the mod whose id is {@code modId} is loaded or not
     */
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    /**
     * For async stuff use this, otherwise use {@link GTCEu isClientSide}
     * 
     * @return if the current thread is the client thread
     */
    @SuppressWarnings("ConstantValue")
    public static boolean isClientThread() {
        return isClientSide() && Minecraft.getInstance() != null && Minecraft.getInstance().isSameThread();
    }

    /**
     * @return if the game is the <strong>PHYSICAL</strong> client, e.g. not a dedicated server.
     * @apiNote Do not use this to check if you're currently on the server thread for side-specific actions!
     *          It does <strong>NOT</strong> work for that. Use {@link #isClientThread()} instead.
     * @see #isClientThread()
     */
    public static boolean isClientSide() {
        return FMLEnvironment.dist.isClient();
    }

    /**
     * This check isn't the same for client and server!
     * 
     * @return if it's safe to access the current instance {@link net.minecraft.world.level.Level Level} on client or if
     *         it's safe to access any level on server.
     */
    public static boolean canGetServerLevel() {
        if (isClientSide()) {
            return Minecraft.getInstance().level != null;
        }
        var server = getMinecraftServer();
        return server != null &&
                !(server.isStopped() || server.isShutdown() || !server.isRunning() || server.isCurrentlySaving());
    }

    /**
     * @return the path to the minecraft instance directory
     */
    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    public static class Mods {

        public static boolean isAnyRecipeViewerLoaded() {
            return isModLoaded(GTValues.MODID_EMI) || isModLoaded(GTValues.MODID_JEI) ||
                    isModLoaded(GTValues.MODID_REI);
        }

        public static boolean isJEILoaded() {
            return !(isModLoaded(GTValues.MODID_EMI) || isModLoaded(GTValues.MODID_REI)) &&
                    isModLoaded(GTValues.MODID_JEI);
        }

        public static boolean isREILoaded() {
            return isModLoaded(GTValues.MODID_REI) && (!isClientSide() || REIRuntime.getInstance().isOverlayVisible());
        }

        public static boolean isEMILoaded() {
            return isModLoaded(GTValues.MODID_EMI) && (!isClientSide() || EmiConfig.enabled);
        }

        public static boolean isKubeJSLoaded() {
            return isModLoaded(GTValues.MODID_KUBEJS);
        }

        public static boolean isIrisLoaded() {
            return isModLoaded(GTValues.MODID_IRIS);
        }

        public static boolean isSodiumLoaded() {
            return isModLoaded(GTValues.MODID_SODIUM);
        }

        public static boolean isAE2Loaded() {
            return isModLoaded(GTValues.MODID_APPENG);
        }

        public static boolean isCuriosLoaded() {
            return isModLoaded(GTValues.MODID_CURIOS);
        }

        public static boolean isShimmerLoaded() {
            return isModLoaded(GTValues.MODID_SHIMMER);
        }

        public static boolean isFTBTeamsLoaded() {
            return isModLoaded(GTValues.MODID_FTB_TEAMS);
        }

        public static boolean isHeraclesLoaded() {
            return isModLoaded(GTValues.MODID_HERACLES);
        }

        public static boolean isFTBQuestsLoaded() {
            return isModLoaded(GTValues.MODID_FTB_QUEST);
        }

        public static boolean isArgonautsLoaded() {
            return isModLoaded(GTValues.MODID_ARGONAUTS);
        }

        public static boolean isGameStagesLoaded() {
            return isModLoaded(GTValues.MODID_GAMESTAGES);
        }

        public static boolean isCCTweakedLoaded() {
            return isModLoaded(GTValues.MODID_CCTWEAKED);
        }
    }
}
