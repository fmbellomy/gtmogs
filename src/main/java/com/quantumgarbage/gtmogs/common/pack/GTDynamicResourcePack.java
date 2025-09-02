package com.quantumgarbage.gtmogs.common.pack;

import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.api.addon.AddonFinder;
import com.quantumgarbage.gtmogs.config.ConfigHolder;

import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.quantumgarbage.gtmogs.common.pack.GTDynamicDataPack.writeJson;

public class GTDynamicResourcePack implements PackResources {

    protected static final ObjectSet<String> CLIENT_DOMAINS = new ObjectOpenHashSet<>();
    protected static final GTDynamicPackContents CONTENTS = new GTDynamicPackContents();

    private static final FileToIdConverter ATLAS_ID_CONVERTER = FileToIdConverter.json("atlases");
    private static final FileToIdConverter TEXTURE_ID_CONVERTER = SpriteSource.TEXTURE_ID_CONVERTER;
    private static final FileToIdConverter BLOCKSTATE_ID_CONVERTER = FileToIdConverter.json("blockstates");
    private static final FileToIdConverter BLOCK_MODEL_ID_CONVERTER = FileToIdConverter.json("models/block");
    private static final FileToIdConverter ITEM_MODEL_ID_CONVERTER = FileToIdConverter.json("models/item");

    private final PackLocationInfo info;

    static {
        CLIENT_DOMAINS.addAll(Sets.newHashSet(GTMOGS.MOD_ID, "minecraft", "neoforge", "c"));
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

    public static void addResource(ResourceLocation location, JsonElement obj) {
        addResource(location, obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static void addResource(ResourceLocation location, byte[] data) {
        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTMOGS.getGameDir().resolve("gtmogs/dumped/assets");
            writeJson(location, null, parent, data);
        }
        CONTENTS.addToData(location, data);
    }

    public static void addBlockModel(ResourceLocation loc, JsonElement obj) {
        ResourceLocation l = getBlockModelLocation(loc);
        byte[] modelBytes = obj.toString().getBytes(StandardCharsets.UTF_8);

        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTMOGS.getGameDir().resolve("gtmogs/dumped/assets");
            writeJson(l, null, parent, modelBytes);
        }
        CONTENTS.addToData(l, modelBytes);
    }

    public static void addBlockModel(ResourceLocation loc, Supplier<JsonElement> obj) {
        addBlockModel(loc, obj.get());
    }

    public static void addItemModel(ResourceLocation loc, JsonElement obj) {
        ResourceLocation l = getItemModelLocation(loc);
        byte[] modelBytes = obj.toString().getBytes(StandardCharsets.UTF_8);

        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTMOGS.getGameDir().resolve("gtmogs/dumped/assets");
            writeJson(l, null, parent, modelBytes);
        }
        CONTENTS.addToData(l, modelBytes);
    }

    public static void addItemModel(ResourceLocation loc, Supplier<JsonElement> obj) {
        addItemModel(loc, obj.get());
    }

    public static void addBlockState(ResourceLocation loc, JsonElement stateJson) {
        ResourceLocation l = getBlockStateLocation(loc);
        byte[] stateBytes = stateJson.toString().getBytes(StandardCharsets.UTF_8);

        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTMOGS.getGameDir().resolve("gtmogs/dumped/assets");
            writeJson(l, null, parent, stateBytes);
        }
        CONTENTS.addToData(l, stateBytes);
    }

    public static void addBlockState(ResourceLocation loc, Supplier<JsonElement> generator) {
        addBlockState(loc, generator.get());
    }

    public static void addAtlasSpriteSource(ResourceLocation atlasLoc, SpriteSource source) {
        addAtlasSpriteSourceList(atlasLoc, Collections.singletonList(source));
    }

    public static void addAtlasSpriteSourceList(ResourceLocation atlasLoc, List<SpriteSource> sources) {
        ResourceLocation l = getAtlasLocation(atlasLoc);
        JsonElement sourceJson = SpriteSources.FILE_CODEC.encodeStart(JsonOps.INSTANCE, sources).getOrThrow();
        byte[] sourceBytes = sourceJson.toString().getBytes(StandardCharsets.UTF_8);

        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTMOGS.getGameDir().resolve("gtmogs/dumped/assets");
            writeJson(l, null, parent, sourceBytes);
        }
        CONTENTS.addToData(l, sourceBytes);
    }

    public static void addBlockTexture(ResourceLocation loc, byte[] data) {
        ResourceLocation l = getTextureLocation("block", loc);
        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTMOGS.getGameDir().resolve("gtmogs/dumped/assets");
            writeByteArray(l, null, parent, data);
        }
        CONTENTS.addToData(l, data);
    }

    public static void addItemTexture(ResourceLocation loc, byte[] data) {
        ResourceLocation l = getTextureLocation("item", loc);
        if (ConfigHolder.INSTANCE.dev.dumpAssets) {
            Path parent = GTMOGS.getGameDir().resolve("gtmogs/dumped/assets");
            writeByteArray(l, null, parent, data);
        }
        CONTENTS.addToData(l, data);
    }

    @ApiStatus.Internal
    public static void writeByteArray(ResourceLocation id, @Nullable String subdir, Path parent, byte[] data) {
        try {
            Path file;
            if (subdir != null) {
                // assume PNG
                file = parent.resolve(id.getNamespace()).resolve(subdir).resolve(id.getPath() + ".png");
            } else {
                // assume the file type is also appended if a full path is given.
                file = parent.resolve(id.getNamespace()).resolve(id.getPath());
            }
            Files.createDirectories(file.getParent());
            try (OutputStream output = Files.newOutputStream(file)) {
                output.write(data);
            }
        } catch (IOException e) {
            GTMOGS.LOGGER.error("Failed to write JSON export for file {}", id, e);
        }
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... elements) {
        if (elements.length > 0 && elements[0].equals("pack.png")) {
            return () -> GTMOGS.class.getResourceAsStream("/icon.png");
        }
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
            return (T) new PackMetadataSection(Component.literal("GTMOGS dynamic assets"),
                    SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES));
        }
        return null;
    }

    @Override
    public @NotNull PackLocationInfo location() {
        return info;
    }

    public boolean isBuiltin() {
        return true;
    }

    @Override
    public void close() {
        // NOOP
    }

    public static ResourceLocation getBlockStateLocation(ResourceLocation blockId) {
        return BLOCKSTATE_ID_CONVERTER.idToFile(blockId);
    }

    public static ResourceLocation getBlockModelLocation(ResourceLocation blockId) {
        return BLOCK_MODEL_ID_CONVERTER.idToFile(blockId);
    }

    public static ResourceLocation getItemModelLocation(ResourceLocation itemId) {
        return ITEM_MODEL_ID_CONVERTER.idToFile(itemId);
    }

    public static ResourceLocation getTextureLocation(@Nullable String path, ResourceLocation textureId) {
        if (path == null) {
            return TEXTURE_ID_CONVERTER.idToFile(textureId);
        }
        return TEXTURE_ID_CONVERTER.idToFile(textureId.withPrefix(path + "/"));
    }

    public static ResourceLocation getAtlasLocation(ResourceLocation atlasId) {
        return ATLAS_ID_CONVERTER.idToFile(atlasId);
    }
}
