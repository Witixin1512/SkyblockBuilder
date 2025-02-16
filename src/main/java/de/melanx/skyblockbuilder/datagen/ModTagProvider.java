package de.melanx.skyblockbuilder.datagen;

import de.melanx.skyblockbuilder.ModBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.moddingx.libx.annotation.data.Datagen;
import org.moddingx.libx.datagen.provider.CommonTagsProviderBase;
import org.moddingx.libx.mod.ModX;

@Datagen
public class ModTagProvider extends CommonTagsProviderBase {

    public ModTagProvider(ModX mod, DataGenerator generator, ExistingFileHelper helper) {
        super(mod, generator, helper);
    }

    @Override
    public void setup() {
        //noinspection unchecked
        this.block(ModBlockTags.ADDITIONAL_VALID_SPAWN)
                .addTags(BlockTags.LEAVES)
                .add(Blocks.WATER);
    }
}
