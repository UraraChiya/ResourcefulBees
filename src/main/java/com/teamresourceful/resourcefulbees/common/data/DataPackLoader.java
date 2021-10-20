package com.teamresourceful.resourcefulbees.common.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.*;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DataPackLoader implements IPackFinder {

    private static final String DATAPACK_NAME = "resourcefulbees:internals";
    public static final DataPackLoader INSTANCE = new DataPackLoader();

    @Override
    public void loadPacks(@NotNull Consumer<ResourcePackInfo> packList, @NotNull ResourcePackInfo.IFactory factory) {
        try (MemoryDataPack dataPack = new MemoryDataPack()) {
            DataGen.getTags().forEach((location, resourceLocations) -> {
                ITag.Builder builder = ITag.Builder.tag();
                resourceLocations.forEach(t -> builder.addElement(t, DATAPACK_NAME));
                dataPack.putJson(ResourcePackType.SERVER_DATA, location, builder.serializeToJson());
            });

            ResourcePackInfo pack = ResourcePackInfo.create(
                    DATAPACK_NAME,
                    true,
                    () -> dataPack,
                    factory,
                    ResourcePackInfo.Priority.BOTTOM,
                    IPackNameDecorator.BUILT_IN
            );
            packList.accept(pack);
        }
    }


    private static class MemoryDataPack implements IResourcePack {

        private static final JsonObject META = ModConstants.GSON.fromJson("{\"pack_format\": 4, \"description\": \"Data for resourcefulbees tags.\"}", JsonObject.class);
        private final HashMap<ResourceLocation, Supplier<? extends InputStream>> data = new HashMap<>();

        private boolean isServerData(ResourcePackType type) {
            return ResourcePackType.SERVER_DATA.equals(type);
        }

        public void putJson(ResourcePackType type, ResourceLocation location, JsonElement json) {
            if (!isServerData(type)) return;
            data.put(location, () -> new ByteArrayInputStream(ModConstants.GSON.toJson(json).getBytes(StandardCharsets.UTF_8)));
        }

        @Override
        public @NotNull InputStream getRootResource(@NotNull String file) throws IOException {
            if(file.contains("/") || file.contains("\\")) {
                throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
            }
            throw new FileNotFoundException(file);
        }

        @Override
        public @NotNull InputStream getResource(@NotNull ResourcePackType type, @NotNull ResourceLocation location) throws IOException {
            if(this.hasResource(type, location)) return data.get(location).get();
            throw new FileNotFoundException(location.toString());
        }

        @Override
        public @NotNull Collection<ResourceLocation> getResources(@NotNull ResourcePackType type, @NotNull String namespace, @NotNull String path, int maxFolderWalk, @NotNull Predicate<String> predicate) {
            if (!isServerData(type)) return Collections.emptyList();
            return data.keySet().stream()
                    .filter(location->location.getNamespace().equals(namespace))
                    .filter(location->location.getPath().split("/").length < maxFolderWalk)
                    .filter(location->location.getPath().startsWith(path))
                    .filter(location-> predicate.test(location.getPath().substring(Math.max(location.getPath().lastIndexOf('/'), 0)))
            ).collect(Collectors.toList());
        }

        @Override
        public boolean hasResource(@NotNull ResourcePackType type, @NotNull ResourceLocation location) {
            return isServerData(type) && data.containsKey(location);
        }

        @Override
        public @NotNull Set<String> getNamespaces(@NotNull ResourcePackType type) {
            if (!isServerData(type)) return Collections.emptySet();
            return data.keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet());
        }

        @Nullable
        @Override
        public <T> T getMetadataSection(@NotNull IMetadataSectionSerializer<T> serializer) {
            return serializer.fromJson(META);
        }

        @Override
        public @NotNull String getName() {
            return DATAPACK_NAME;
        }

        @Override
        public boolean isHidden() {
            return true;
        }

        @Override
        public void close() {
            //Does nothing
        }
    }
}
