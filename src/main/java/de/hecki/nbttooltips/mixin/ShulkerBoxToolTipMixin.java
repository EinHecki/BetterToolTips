package de.hecki.nbttooltips.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.hecki.nbttooltips.NBTToolTips;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mixin(ContainerScreen.class)
public abstract class ShulkerBoxToolTipMixin extends Screen {

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    protected ShulkerBoxToolTipMixin(ITextComponent titleIn) {
        super(titleIn);
    }

    @Shadow
    public abstract boolean keyPressed(int keyCode, int scanCode, int modifiers);

    void boxPart(final MatrixStack m, final int atX, final int atY, final int fromX, final int fromY, final int toX, final int toY) {
        GL11.glDisable(2929);
        GlStateManager.color4f(198, 198, 198, 1.0f);
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("minecraft:bettertooltips/shulker_parts.png"));
        this.blit(m, atX, atY, fromX, fromY, toX, toY);
        GL11.glEnable(2929);
    }

    private void drawBar(final MatrixStack m, final int x, final int y, final int yStart, final int boxWidth, final int howTall) {
        this.boxPart(m, x, y, 0, yStart, 5, howTall);
        for (int i = 0; i < boxWidth + 1; ++i) {
            this.boxPart(m, x + 4 + i * 18, y, 4, yStart, 18, howTall);
        }
        this.boxPart(m, x + 4 + (boxWidth + 1) * 18, y, 22, yStart, 6, howTall);
    }

    @Inject(method = "render", at = @At("TAIL"), cancellable = true)
    private void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!NBTToolTips.showShulkerToolTipInstant() || hoveredSlot == null) {
            if (!Screen.hasShiftDown()) {
                return;
            }
        }
        final ItemStack item = hoveredSlot.getStack();
        if (item.getItem().getTranslationKey().contains("shulker_box")) {
            final int boxWidth = 8;
            final ListNBT nbttaglist = (item.hasTag() ? item.getTag() : new CompoundNBT()).getCompound("BlockEntityTag").getList("Items", 10);
            int slot = 0;
            final List<ItemStack> boxContents = new ArrayList<>();
            for (net.minecraft.nbt.INBT inbt : nbttaglist) {
                final ItemStack stack = ItemStack.read((CompoundNBT) inbt);
                final String stringofitems = inbt.toString();
                final int slotnum = Integer.parseInt(stringofitems.substring(stringofitems.indexOf("{Slot:") + 1, stringofitems.indexOf("b,")).replaceAll("[^0-9]", ""));
                while (slot != slotnum) {
                    ++slot;
                    boxContents.add(ItemStack.EMPTY);
                }
                ++slot;
                boxContents.add(stack);
            }
            slot %= boxWidth + 1;
            while (boxWidth + 1 > slot && slot != 0) {
                ++slot;
                boxContents.add(ItemStack.EMPTY);
            }
            final int lengthOver = mouseX + 32 + boxWidth * 18 - Minecraft.getInstance().getMainWindow().getScaledWidth();
            final int toolTipHeight = 8 + (boxContents.size() - 1) / boxWidth * 18;
            final int centerToolTip = toolTipHeight / 2;
            final int lengthAbove = mouseY - 23 - centerToolTip;
            final int lengthBelow = mouseY - 10 + toolTipHeight - Minecraft.getInstance().getMainWindow().getScaledHeight() - centerToolTip;
            final int drawX = mouseX + 4 - ((lengthOver > 0) ? lengthOver : 0);
            final int drawY = mouseY - 12 - centerToolTip - ((lengthBelow > 0) ? lengthBelow : 0) - ((lengthAbove < 0) ? lengthAbove : 0);
            GlStateManager.translatef(0.0f, 0.0f, 777.0f);
            this.drawBar(matrixStack, drawX, drawY - 11, 0, boxWidth, 16);
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, item.getDisplayName().getString(), (float) (drawX + 6), (float) (drawY - 6), Color.DARK_GRAY.getRGB());
            int row = 0;
            slot = 0;
            int x = 0;
            int y = drawY + 6 + 18 * row;
            this.boxPart(matrixStack, drawX, drawY + 5, 0, 4, 5, 18);
            this.boxPart(matrixStack, drawX + (boxWidth + 1) * 18 + 5, drawY + 5, 23, 5, 5, 18);
            for (ItemStack boxContent : boxContents) {
                if (slot > boxWidth) {
                    slot = 0;
                    ++row;
                    this.boxPart(matrixStack, drawX, drawY + 5 + 18 * row, 0, 4, 5, 18);
                    this.boxPart(matrixStack, drawX + (boxWidth + 1) * 18 + 5, drawY + 5 + 18 * row, 23, 5, 5, 18);
                }
                x = drawX + 6 + 18 * slot;
                y = drawY + 6 + 18 * row;
                this.boxPart(matrixStack, x - 1, y - 1, 5, 17, 18, 18);
                Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(boxContent, x, y);
                String string = Integer.toString(boxContent.getCount());
                if (boxContent.getCount() == 1) {
                    string = "";
                }
                Minecraft.getInstance().getItemRenderer().renderItemOverlayIntoGUI(Minecraft.getInstance().fontRenderer, boxContent, x, y, string);
                ++slot;
            }
            while (boxWidth + 1 > slot) {
                x = drawX + 6 + 18 * slot;
                this.boxPart(matrixStack, x - 1, y - 1, 5, 17, 18, 18);
                ++slot;
            }
            this.drawBar(matrixStack, drawX, drawY + (row + 1) * 18 + 5, 35, boxWidth, 5);

        }
    }
}
