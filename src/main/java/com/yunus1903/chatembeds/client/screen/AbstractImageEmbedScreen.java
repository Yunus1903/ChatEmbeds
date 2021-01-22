package com.yunus1903.chatembeds.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.yunus1903.chatembeds.client.embed.Embed;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;

/**
 * @author Yunus1903
 * @since 23/09/2020
 */
public abstract class AbstractImageEmbedScreen<T extends Embed> extends EmbedScreen<T>
{
    private final int imageWidth, imageHeight;
    protected int scaledImageWidth, scaledImageHeight;
    private IFormattableTextComponent openImage;

    public AbstractImageEmbedScreen(ChatScreen parent, int scrollPos, T embed, NativeImage image)
    {
        super(parent, scrollPos, embed);
        this.imageWidth = this.scaledImageWidth = image.getWidth();
        this.imageHeight = this.scaledImageHeight = image.getHeight();
    }

    @Override
    protected void init()
    {
        if (scaledImageWidth > width / 2 || scaledImageHeight > height / 2)
        {
            if (scaledImageWidth > width / 2) // Max width
            {
                scaledImageWidth = width / 2;
                scaledImageHeight = (int) (((float) imageHeight / (float) imageWidth) * (float) scaledImageWidth);
            }

            if (scaledImageHeight > height / 2) // Max height
            {
                scaledImageHeight = height / 2;
                scaledImageWidth = (int) (((float) imageWidth / (float) imageHeight) * (float) scaledImageHeight);
            }
        }

        openImage = new StringTextComponent("Open image");
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (minecraft == null) return;
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (mouseOverImage(mouseX, mouseY)) openImage.mergeStyle(TextFormatting.UNDERLINE);
        else
        {
            openImage.setStyle(Style.EMPTY.setClickEvent(
                    new ClickEvent(ClickEvent.Action.OPEN_URL, embed.getUrl().toString())));
            openImage.mergeStyle(TextFormatting.DARK_GRAY);
        }

        minecraft.fontRenderer.func_238407_a_(matrixStack,
                LanguageMap.getInstance().func_241870_a(openImage),
                (width - scaledImageWidth) >> 1,
                ((height - scaledImageHeight) >> 1) + scaledImageHeight + 5,
                0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (mouseX < (width - scaledImageWidth) >> 1
                || mouseX > ((width - scaledImageWidth) >> 1) + scaledImageWidth
                || mouseY < (height - scaledImageHeight) >> 1
                || mouseY > ((height - scaledImageHeight) >> 1) + scaledImageHeight)
        {
            if (mouseOverImage(mouseX, mouseY)) handleComponentClicked(openImage.getStyle());
            else closeScreen();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Check if mouse if hovering over the image
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @return {@code true} if mouse is hovering over the shown image
     */
    private boolean mouseOverImage(double mouseX, double mouseY)
    {
        return minecraft != null
                && mouseX >= ((width - scaledImageWidth) >> 1)
                && mouseX <= ((width - scaledImageWidth) >> 1)
                + minecraft.fontRenderer.getStringWidth(openImage.getString())
                && mouseY >= ((height - scaledImageHeight) >> 1)
                + scaledImageHeight + 5
                && mouseY <= ((height - scaledImageHeight) >> 1)
                + scaledImageHeight + 5 + minecraft.fontRenderer.FONT_HEIGHT;
    }
}