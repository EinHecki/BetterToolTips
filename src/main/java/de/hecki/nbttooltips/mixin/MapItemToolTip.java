package de.hecki.nbttooltips.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.hecki.nbttooltips.NBTToolTips;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ContainerScreen.class)
public abstract class MapItemToolTip extends Screen {

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    protected MapItemToolTip(ITextComponent titleIn) {
        super(titleIn);
    }

    @Shadow
    public abstract boolean keyPressed(int keyCode, int scanCode, int modifiers);

    @Shadow public abstract void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);

    @Inject(method = "render", at = @At("TAIL"), cancellable = true)
    private void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (hoveredSlot == null) return;
        if (!NBTToolTips.showMapToolTip()) {
            return;
        }
        final ItemStack item = hoveredSlot.getStack();
        if (item.getItem() != Items.FILLED_MAP) {
            return;
        }
        assert Minecraft.getInstance().world != null;
        final MapData mapdata = FilledMapItem.getMapData(item, Minecraft.getInstance().world);

        GL11.glPushMatrix();

        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("minecraft:textures/map/map_background_checkerboard.png"));
        final BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

        //GL11.glTranslated(mouseX + 11.5, mouseY + caclculateYOffset(item), 301.0);
        GL11.glTranslated(mouseX - (60 + 11.5), mouseY - 13, 301.0);
        GL11.glScaled(0.5, 0.5, 1.0);

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-7.0, 135.0, 0.0).color(255, 255, 255, 255).tex(0.0f, 1.0f).lightmap(240).endVertex();
        bufferbuilder.pos(135.0, 135.0, 0.0).color(255, 255, 255, 255).tex(1.0f, 1.0f).lightmap(240).endVertex();
        bufferbuilder.pos(135.0, -7.0, 0.0).color(255, 255, 255, 255).tex(1.0f, 0.0f).lightmap(240).endVertex();
        bufferbuilder.pos(-7.0, -7.0, 0.0).color(255, 255, 255, 255).tex(0.0f, 0.0f).lightmap(240).endVertex();
        bufferbuilder.finishDrawing();
        GL11.glEnable(3008);
        WorldVertexBufferUploader.draw(bufferbuilder);
        GL11.glTranslated(0.0, 0.0, 1.0);

        final IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(bufferbuilder);

        if (mapdata != null) Minecraft.getInstance().gameRenderer.getMapItemRenderer().renderMap(matrixStack, irendertypebuffer$impl, mapdata, true, 240);
        irendertypebuffer$impl.finish();
        GL11.glPopMatrix();
    }

//    private double caclculateYOffset(ItemStack item) {
//        double yOffset = 3.5;
//        ITooltipFlag advanced = Minecraft.getInstance().gameSettings.advancedItemTooltips ?
//                ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
//        final List<ITextComponent> tooltip = item.getTooltip(Minecraft.getInstance().player, advanced);
//        yOffset += (tooltip.size() - 1) * 12;
//        if (advanced == ITooltipFlag.TooltipFlags.ADVANCED) {
//            yOffset -= 0.8;
//        }
//        return yOffset;
//    }
}
