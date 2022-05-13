package com.yunus1903.chatembeds.client.embed;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yunus1903.chatembeds.ChatEmbeds;
import com.yunus1903.chatembeds.ChatEmbedsConfig;
import com.yunus1903.chatembeds.client.EmbedChatLine;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Image chat embed
 * @author Yunus1903
 * @since 29/08/2020
 */
@SuppressWarnings("DuplicatedCode")
public class ImageEmbed extends Embed
{
    private NativeImage image, scaledImage;
    private Identifier imageResourceLocation;

    ImageEmbed(URL url, int ticks, int chatLineId)
    {
        super(url, ticks, chatLineId);
    }

    @Override
    List<? extends ChatHudLine<OrderedText>> createChatHudLines()
    {
        List<ChatHudLine<OrderedText>> lines = new ArrayList<>();
        if (!loadImage()) return lines;

        if (!ChatEmbedsConfig.getConfig().removeUrlMessage)
            lines.add(new ChatHudLine<>(ticks, Language.getInstance().reorder(new LiteralText("")), chatHudLineId));

        double imageHeight = scaledImage.getHeight();
        double lineHeight = 9.0D;
        double totalLines = imageHeight / lineHeight;

        for (int i = 0; i < Math.ceil(totalLines); i++)
        {
            double heightScale = i == (int) totalLines ? (totalLines - i) : 1.0D;
            float u0 = 0;
            float v0 = (float) (i * lineHeight);
            int destWidth = scaledImage.getWidth();
            int destHeight = (int) (lineHeight * heightScale);
            int textureWidth = scaledImage.getWidth();
            int textureHeight = scaledImage.getHeight();

            lines.add(new EmbedChatLine<>(ticks, chatHudLineId, this)
            {
                @Override
                public void render(MinecraftClient mc, MatrixStack matrixStack, int x, int y)
                {
                    RenderSystem.setShaderTexture(0, imageResourceLocation);
                    RenderSystem.enableBlend();
                    DrawableHelper.drawTexture(matrixStack, x, y, u0, v0, destWidth, destHeight, textureWidth, textureHeight);
                }

                @Override
                public int getWidth()
                {
                    return scaledImage.getWidth();
                }
            });
        }

        return lines;
    }

    private boolean loadImage()
    {
        try
        {
            if (connection == null) return false;
            image = NativeImage.read(connection.getInputStream());
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("Exception reading image", e);
        }

        if (image == null) return false;

        ChatHud gui = MinecraftClient.getInstance().inGameHud.getChatHud();
        ChatEmbedsConfig config = ChatEmbedsConfig.getConfig();
        scaledImage = scaleImage(image,
                Math.min(config.chatImageEmbedMaxWidth, gui.getWidth()),
                config.chatImageEmbedMaxHeight);
        imageResourceLocation = MinecraftClient.getInstance().getTextureManager()
                .registerDynamicTexture("chat_embed_image", new NativeImageBackedTexture(image));

        return true;
    }

    protected static NativeImage scaleImage(NativeImage image, int maxWidth, int maxHeight)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width > maxWidth)
        {
            width = maxWidth;
            height = (int) (((float) image.getHeight() / (float) image.getWidth()) * (float) width);
        }

        if (height > maxHeight)
        {
            height = maxHeight;
            width = (int) (((float) image.getWidth() / (float) image.getHeight()) * (float) height);
        }

        NativeImage scaledImage = new NativeImage(width, height, false);
        image.resizeSubRectTo(0, 0, image.getWidth(), image.getHeight(), scaledImage);
        return scaledImage;
    }

    public NativeImage getImage()
    {
        return image;
    }

    public NativeImage getScaledImage()
    {
        return scaledImage;
    }

    public Identifier getImageResourceLocation()
    {
        return imageResourceLocation;
    }
}
