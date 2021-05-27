package de.hecki.nbttooltips;

import net.labymod.addon.AddonTransformer;
import net.labymod.api.TransformerType;

public class NBTToolTipsTransformer extends AddonTransformer {

    @Override
    public void registerTransformers() {
        this.registerTransformer(TransformerType.VANILLA, "bettertooltips.mixin.json");
    }
}
