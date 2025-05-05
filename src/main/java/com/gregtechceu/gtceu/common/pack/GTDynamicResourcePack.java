package com.gregtechceu.gtceu.common.pack;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.addon.AddonFinder;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

import static com.gregtechceu.gtceu.common.pack.GTDynamicDataPack.writeJson;

public class GTDynamicResourcePack implements PackResources {

    protected static final ObjectSet<String> CLIENT_DOMAINS = new ObjectOpenHashSet<>();
    protected static final GTDynamicPackContents CONTENTS = new GTDynamicPackContents();

    private final PackLocationInfo info;

    static {
        CLIENT_DOMAINS.addAll(Sets.newHashSet(GTCEu.MOD_ID, "minecraft", "neoforge", "c"));
    }

    public GTDynamicResourcePack(PackLocationInfo info) {
        this(info, AddonFinder.getAddons().keySet());
    }

    public GTDynamicResourcePack(PackLocationInfo info, Collection<String> domains) {
        this.info = info;
        CLIENT_DOMAINS.addAll(domains);
    }

    public static void clearClient() {
        CONTENTS.clearData();
    }

    public static void addBlockModel(ResourceLocation loc, JsonElement obj) {
        byte[] modelBytes = obj.toString().getBytes(StandardCharsets.UTF_8);
        ResourceLocation l = getModelLocation(loc);
        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTCEu.getGameDir().resolve("gtceu/dumped/assets");
            writeJson(l, null, parent, modelBytes);
        }
        CONTENTS.addToData(l, modelBytes);
    }

    public static void addBlockModel(ResourceLocation loc, Supplier<JsonElement> obj) {
        addBlockModel(loc, obj.get());
    }

    public static void addItemModel(ResourceLocation loc, JsonElement obj) {
        byte[] modelBytes = obj.toString().getBytes(StandardCharsets.UTF_8);
        ResourceLocation l = getItemModelLocation(loc);
        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTCEu.getGameDir().resolve("gtceu/dumped/assets");
            writeJson(l, null, parent, modelBytes);
        }
        CONTENTS.addToData(l, modelBytes);
    }

    public static void addItemModel(ResourceLocation loc, Supplier<JsonElement> obj) {
        addItemModel(loc, obj.get());
    }

    public static void addBlockState(ResourceLocation loc, JsonElement stateJson) {
        byte[] stateBytes = stateJson.toString().getBytes(StandardCharsets.UTF_8);
        ResourceLocation l = getBlockStateLocation(loc);
        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTCEu.getGameDir().resolve("gtceu/dumped/assets");
            writeJson(l, null, parent, stateBytes);
        }
        CONTENTS.addToData(l, stateBytes);
    }

    public static void addBlockState(ResourceLocation loc, Supplier<JsonElement> generator) {
        addBlockState(loc, generator.get());
    }

    public static void addBlockTexture(ResourceLocation loc, byte[] data) {
        ResourceLocation l = getTextureLocation("block", loc);
        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTCEu.getGameDir().resolve("gtceu/dumped/assets");
            writeByteArray(l, null, parent, data);
        }
        CONTENTS.addToData(l, data);
    }

    public static void addItemTexture(ResourceLocation loc, byte[] data) {
        ResourceLocation l = getTextureLocation("item", loc);
        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTCEu.getGameDir().resolve("gtceu/dumped/assets");
            writeByteArray(l, null, parent, data);
        }
        CONTENTS.addToData(l, data);
    }

    @ApiStatus.Internal
    public static void writeByteArray(ResourceLocation id, @Nullable String subdir, Path parent, byte[] data) {
        try {
            Path file;
            if (subdir != null) {
                file = parent.resolve(id.getNamespace()).resolve(subdir).resolve(id.getPath() + ".png"); // assume PNG
            } else {
                file = parent.resolve(id.getNamespace()).resolve(id.getPath()); // assume the file type is also appended
                                                                                // if a full path is given.
            }
            Files.createDirectories(file.getParent());
            try (OutputStream output = Files.newOutputStream(file)) {
                output.write(data);
            }
        } catch (IOException e) {
            GTCEu.LOGGER.error("Failed to save asset JSON for id {} to disk.", id, e);
        }
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... elements) {
        return null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if (type == PackType.CLIENT_RESOURCES) {
            return CONTENTS.getResource(location);
        }
        return null;
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
        if (packType == PackType.CLIENT_RESOURCES) {
            CONTENTS.listResources(namespace, path, resourceOutput);
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? CLIENT_DOMAINS : Set.of();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionSerializer<T> metaReader) {
        if (metaReader == PackMetadataSection.TYPE) {
            return (T) new PackMetadataSection(Component.literal("GTCEu dynamic assets"),
                    SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES));
        }
        return null;
    }

    @Override
    public @NotNull PackLocationInfo location() {
        return info;
    }

    @Override
    public void close() {
        // NOOP
    }

    public static ResourceLocation getBlockStateLocation(ResourceLocation blockId) {
        return ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(),
                String.join("", "blockstates/", blockId.getPath(), ".json"));
    }

    public static ResourceLocation getModelLocation(ResourceLocation blockId) {
        return ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(),
                String.join("", "models/", blockId.getPath(), ".json"));
    }

    public static ResourceLocation getItemModelLocation(ResourceLocation itemId) {
        return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(),
                String.join("", "models/item/", itemId.getPath(), ".json"));
    }

    public static ResourceLocation getTextureLocation(@Nullable String path, ResourceLocation tagId) {
        if (path == null) {
            return ResourceLocation.fromNamespaceAndPath(tagId.getNamespace(),
                    String.join("", "textures/", tagId.getPath(), ".png"));
        }
        return ResourceLocation.fromNamespaceAndPath(tagId.getNamespace(),
                String.join("", "textures/", path, "/", tagId.getPath(), ".png"));
    }
}
