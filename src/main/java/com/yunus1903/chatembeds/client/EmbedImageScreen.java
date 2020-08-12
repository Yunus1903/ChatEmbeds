package com.yunus1903.chatembeds.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

/**
 * @author Yunus1903
 * @since 11/08/2020
 */
public class EmbedImageScreen extends Screen
{
    private final Screen parent;
    private final int scrollPos;
    private final NativeImage image;
    private final String url;

    private int imageWidth, imageHeight;

    private ResourceLocation imageRL;
    private IFormattableTextComponent openImage;

    public EmbedImageScreen(Screen parent, int scrollPos, NativeImage image, String url)
    {
        super(new StringTextComponent("Embed image"));
        this.parent = parent;
        this.scrollPos = scrollPos;
        this.image = image;
        this.url = url;
    }

    @Override
    protected void init()
    {
        imageRL = minecraft.getTextureManager().getDynamicTextureLocation("embed_fullscreen_image", new DynamicTexture(image));

        imageWidth = image.getWidth();
        imageHeight = image.getHeight();

        if (imageWidth > width / 2 || imageHeight > height / 2)
        {
            if (imageWidth > width / 2) // Max width
            {
                imageWidth = width / 2;
                imageHeight = (int) (((float) image.getHeight() / (float) image.getWidth()) * (float) imageWidth);
            }

            if (imageHeight > height / 2) // Max height
            {
                imageHeight = height / 2;
                imageWidth = (int) (((float) image.getWidth() / (float) image.getHeight()) * (float) imageHeight);
            }

            NativeImage scaledImage = new NativeImage(imageWidth, imageHeight, false);
            image.resizeSubRectTo(0, 0, image.getWidth(), image.getHeight(), scaledImage);
        }

        openImage = new StringTextComponent("Open image");
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(matrixStack);

        minecraft.getTextureManager().bindTexture(imageRL);
        AbstractGui.blit(matrixStack, (width - imageWidth) / 2, (height - imageHeight) / 2, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        if (mouseOverOpenImage(mouseX, mouseY)) openImage.mergeStyle(TextFormatting.UNDERLINE);
        else
        {
            openImage.setStyle(Style.EMPTY.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
            openImage.mergeStyle(TextFormatting.DARK_GRAY);
        }

        minecraft.fontRenderer.func_238407_a_(matrixStack, openImage, ((width - imageWidth) >> 1), ((height - imageHeight) >> 1) + imageHeight + 5, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_)
    {
        if (mouseX < (width - imageWidth) >> 1 || mouseX > ((width - imageWidth) >> 1) + imageWidth || mouseY < (height - imageHeight) >> 1 || mouseY > ((height - imageHeight) >> 1) + imageHeight)
        {
            if (mouseOverOpenImage(mouseX, mouseY)) handleComponentClicked(openImage.getStyle());
            else closeScreen();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, p_231044_5_);
    }

    @Override
    public void closeScreen()
    {
        minecraft.displayGuiScreen(parent);
        minecraft.ingameGUI.getChatGUI().scrollPos = scrollPos;
    }

    private boolean mouseOverOpenImage(double mouseX, double mouseY)
    {
        if (mouseX >= ((width - imageWidth) >> 1) && mouseX <= ((width - imageWidth) >> 1) + minecraft.fontRenderer.getStringWidth(openImage.getString()) && mouseY >= ((height - imageHeight) >> 1) + imageHeight + 5 && mouseY <= ((height - imageHeight) >> 1) + imageHeight + 5 + minecraft.fontRenderer.FONT_HEIGHT)
        {
            return true;
        }
        return false;
    }
}
