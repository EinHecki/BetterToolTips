package de.hecki.nbttooltips.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ToolTipMixin {

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    private void injectBeeTooltip(PlayerEntity playerIn, ITooltipFlag flag, CallbackInfoReturnable<List<ITextComponent>> cir) {
        ItemStack itemStack = ((ItemStack) (Object) this);
        if (cir.getReturnValue() == null) return;

        List<ITextComponent> data = cir.getReturnValue();
        ITextComponent component = StringTextComponent.EMPTY;

        if (itemStack.getItem().getTranslationKey().contains("shulker_box")
                || itemStack.getItem() == Items.BARREL
                || itemStack.getItem() == Items.CHEST
                || itemStack.getItem() == Items.TRAPPED_CHEST
        ) {
            if (!Screen.hasShiftDown()) {
                data.add(new StringTextComponent(" "));
                data.add(new StringTextComponent("Press ").setStyle(Style.EMPTY.setFormatting(TextFormatting.WHITE))
                        .append(new StringTextComponent("SHIFT").setStyle(Style.EMPTY.setFormatting(TextFormatting.RED)))
                        .append(new StringTextComponent(" to view Items").setStyle(Style.EMPTY.setFormatting(TextFormatting.WHITE))));
            }
            return;
        }
        if (itemStack.getItem() == Items.BEE_NEST || itemStack.getItem() == Items.BEEHIVE) {
            if (itemStack.getTag() == null) {
                return;
            }
            data.add(new StringTextComponent(" "));
            data.add(component.deepCopy().append(new StringTextComponent("Bees: ")
                    .setStyle(Style.EMPTY.setFormatting(TextFormatting.GRAY)))
                    .append(new StringTextComponent(itemStack.getTag().getCompound("BlockEntityTag").getList("Bees", 10).size() + ""))
                    .setStyle(Style.EMPTY.setFormatting(TextFormatting.YELLOW)));
        }
    }
}
