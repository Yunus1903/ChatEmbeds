package com.yunus1903.chatembeds.client.screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.client.embed.Embed;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

/**
 * @author Yunus1903
 * @since 23/09/2020
 */
public abstract class AbstractImageEmbedScreen<T extends Embed> extends EmbedScreen<T>
{
    private final int imageWidth, imageHeight;
    protected int scaledImageWidth, scaledImageHeight;
    private MutableComponent openImage;

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

        openImage = new TextComponent("Open image");
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (minecraft == null) return;
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (mouseOverImage(mouseX, mouseY)) openImage.withStyle(ChatFormatting.UNDERLINE);
        else
        {
            openImage.setStyle(Style.EMPTY.withClickEvent(
                    new ClickEvent(ClickEvent.Action.OPEN_URL, embed.getUrl().toString())));
            openImage.withStyle(ChatFormatting.DARK_GRAY);
        }

        minecraft.font.drawShadow(matrixStack,
                Language.getInstance().getVisualOrder(openImage),
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
            else onClose();
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
                + minecraft.font.width(openImage.getString())
                && mouseY >= ((height - scaledImageHeight) >> 1)
                + scaledImageHeight + 5
                && mouseY <= ((height - scaledImageHeight) >> 1)
                + scaledImageHeight + 5 + minecraft.font.lineHeight;
    }
}
