package de.melanx.skyblockbuilder.util;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LazyBiomeRegistryWrapper extends MappedRegistry<Biome> {

    private static final HashMap<Registry<Biome>, LazyBiomeRegistryWrapper> cache = new HashMap<>();
    private final Registry<Biome> parent;
    private final Map<ResourceLocation, Biome> modifiedBiomes = new HashMap<>();
    private final Map<ResourceKey<Biome>, Holder<Biome>> modifiedByKey = new HashMap<>();
    private final Map<ResourceLocation, ResourceKey<Biome>> keyCache = new HashMap<>();
    private List<Holder.Reference<Biome>> holdersInOrder;

    private LazyBiomeRegistryWrapper(Registry<Biome> parent) {
        super(parent.key(), Lifecycle.experimental(), null);
        this.parent = parent;
    }

    public static LazyBiomeRegistryWrapper get(Registry<Biome> parent) {
        if (parent instanceof LazyBiomeRegistryWrapper wrapper) {
            return wrapper;
        }

        return cache.computeIfAbsent(parent, LazyBiomeRegistryWrapper::new);
    }

    @Nonnull
    @Override
    public Holder<Biome> registerMapping(int id, @Nonnull ResourceKey<Biome> key, @Nonnull Biome value, @Nonnull Lifecycle lifecycle) {
        throw new IllegalStateException("Can't register to biome registry wrapper.");
    }

    @Nonnull
    @Override
    public Holder<Biome> register(@Nonnull ResourceKey<Biome> key, @Nonnull Biome value, @Nonnull Lifecycle lifecycle) {
        throw new IllegalStateException("Can't register to biome registry wrapper.");
    }

    @Nonnull
    @Override
    public Holder<Biome> registerOrOverride(@Nonnull OptionalInt id, @Nonnull ResourceKey<Biome> key, @Nonnull Biome value, @Nonnull Lifecycle lifecycle) {
        throw new IllegalStateException("Can't register to biome registry wrapper.");
    }

    @Override
    @Nullable
    public Biome get(@Nullable ResourceLocation name) {
        return this.modified(this.parent.get(name));
    }

    @Override
    @Nullable
    public Biome get(@Nullable ResourceKey<Biome> key) {
        return this.modified(this.parent.get(key));
    }

    @Override
    @Nullable
    public ResourceLocation getKey(@Nonnull Biome value) {
        return value.getRegistryName();
    }

    @Nonnull
    @Override
    public Optional<Holder<Biome>> getHolder(@Nonnull ResourceKey<Biome> key) {
        return this.modified(this.parent.getHolder(key));
    }

    @Override
    @Nullable
    public Biome byId(int value) {
        return this.modified(this.parent.byId(value));
    }

    @Nonnull
    @Override
    public Optional<Holder<Biome>> getHolder(int value) {
        return this.modified(this.parent.getHolder(value));
    }

    @Override
    public boolean containsKey(@Nonnull ResourceLocation name) {
        return this.parent.containsKey(name);
    }

    @Override
    public int getId(@Nullable Biome value) {
        return this.parent.getId(this.parent.get(value == null ? null : value.getRegistryName()));
    }

    @Nonnull
    @Override
    public Stream<Holder.Reference<Biome>> holders() {
        return this.holdersInOrder().stream();
    }

    private List<Holder.Reference<Biome>> holdersInOrder() {
        if (this.holdersInOrder == null) {
            this.holdersInOrder = this.parent.holders().filter(Objects::nonNull).map(holder -> (Holder.Reference<Biome>) this.modified(holder)).toList();
        }

        return this.holdersInOrder;
    }

    @Nonnull
    @Override
    public Iterator<Biome> iterator() {
        return Iterators.transform(this.holdersInOrder().iterator(), Holder::value);
    }

    @Nonnull
    @Override
    public Set<ResourceLocation> keySet() {
        return this.parent.keySet();
    }

    @Nonnull
    @Override
    public Set<Map.Entry<ResourceKey<Biome>, Biome>> entrySet() {
        return this.parent.entrySet().stream()
                .map(e -> Pair.of(e.getKey(), this.modified(e.getValue())))
                .collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Holder<Biome> getOrCreateHolder(@Nonnull ResourceKey<Biome> key) {
        return this.modifiedByKey.computeIfAbsent(key, nKey -> {
            Holder.Reference<Biome> holder = Holder.Reference.createStandAlone(this, nKey);
            //noinspection ConstantConditions
            holder.bind(nKey, this.get(key));
            return holder;
        });
    }

    public Holder<Biome> modified(Holder<Biome> biomeHolder) {
        //noinspection ConstantConditions
        ResourceKey<Biome> key = ResourceKey.create(this.key(), biomeHolder.value().getRegistryName());
        if (this.modifiedByKey.containsKey(key)) {
            return this.modifiedByKey.get(key);
        }

        Biome modified = RandomUtility.modifyCopyBiome(biomeHolder.value());
        Holder.Reference<Biome> modifiedHolder = Holder.Reference.createStandAlone(this, key);
        modifiedHolder.bind(key, modified);
        this.modifiedByKey.put(key, modifiedHolder);
        return modifiedHolder;
    }

    private Optional<Holder<Biome>> modified(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Holder<Biome>> biomeHolder) {
        if (biomeHolder.isPresent()) {
            Holder<Biome> holder = biomeHolder.get();
            //noinspection ConstantConditions
            ResourceKey<Biome> key = ResourceKey.create(this.key(), holder.value().getRegistryName());
            if (this.modifiedByKey.containsKey(key)) {
                return Optional.of(this.modifiedByKey.get(key));
            }

            Biome modified = RandomUtility.modifyCopyBiome(holder.value());
            Holder.Reference<Biome> modifiedHolder = Holder.Reference.createStandAlone(this, key);
            modifiedHolder.bind(key, modified);
            this.modifiedByKey.put(key, modifiedHolder);
            return Optional.of(modifiedHolder);
        }

        return biomeHolder;
    }

    private Biome modified(@Nullable Biome biome) {
        if (biome == null) {
            return null;
        } else if (this.modifiedBiomes.containsKey(biome.getRegistryName())) {
            return this.modifiedBiomes.get(biome.getRegistryName());
        } else {
            Biome modified = RandomUtility.modifyCopyBiome(biome);
            this.modifiedBiomes.put(biome.getRegistryName(), modified);
            return modified;
        }
    }

    @Nonnull
    @Override
    public Optional<ResourceKey<Biome>> getResourceKey(@Nonnull Biome biome) {
        ResourceLocation id = this.getKey(biome);
        if (id == null) {
            return Optional.empty();
        } else {
            return Optional.of(this.keyCache.computeIfAbsent(id, k -> ResourceKey.create(Registry.BIOME_REGISTRY, k)));
        }
    }
}
