package com.yunus1903.chatembeds.client.embed;

import at.dhyan.open_imaging.GifDecoder;
import com.google.common.io.ByteStreams;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.ChatEmbeds;
import com.yunus1903.chatembeds.ChatEmbedsConfig;
import com.yunus1903.chatembeds.client.EmbedChatLine;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.yunus1903.chatembeds.client.ChatScrollPos.getScrollPos;

/**
 * Animated image Text chat embed
 * @author Yunus1903
 * @since 29/08/2020
 */
@SuppressWarnings("UnstableApiUsage")
public class AnimatedImageEmbed extends Embed
{
    private List<Frame> frames; // TODO: 22/01/2021 Fix final

    protected EmbedChatLine<AnimatedImageEmbed> currentRenderer;
    protected int currentFrame;

    AnimatedImageEmbed(URL url, int ticks, int chatLineId)
    {
        super(url, ticks, chatLineId);
    }

    @Override
    List<? extends GuiMessage<FormattedCharSequence>> createChatLines()
    {
        frames = new ArrayList<>();
        List<GuiMessage<FormattedCharSequence>> lines = new ArrayList<>();
        if (!loadImage()) return lines;

        if (!ChatEmbedsConfig.GeneralConfig.removeUrlMessage)
            lines.add(new GuiMessage<>(ticks, Language.getInstance().getVisualOrder(new TextComponent("")), chatLineId));

        NativeImage scaledImage = frames.get(0).getScaledImage();

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

            lines.add(new EmbedChatLine<>(ticks, chatLineId, this)
            {
                private int time;
                private int lastFrameIndex;
                private int lastScrollPos;

                @Override
                public void render(Minecraft mc, PoseStack matrixStack, int x, int y)
                {
                    if (lastScrollPos != getScrollPos())
                    {
                        currentRenderer = null;
                        lastScrollPos = getScrollPos();
                    }

                    if (currentRenderer == null) currentRenderer = this;

                    if (currentRenderer == this)
                    {
                        final int frameIndex = mc.frameTimer.getLogEnd();

                        if (lastFrameIndex != frameIndex)
                        {
                            lastFrameIndex = frameIndex;
                            time++;
                        }

                        if (time >= frames.get(currentFrame).getDelay() / 2)
                        {
                            time = 0;
                            if (currentFrame + 1 >= frames.size()) currentFrame = 0;
                            else currentFrame++;
                        }
                    }

                    RenderSystem.setShaderTexture(0, frames.get(currentFrame).getResourceLocation());
                    RenderSystem.enableBlend();
                    GuiComponent.blit(matrixStack, x, y, u0, v0, destWidth, destHeight, textureWidth, textureHeight);
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
        GifDecoder.GifImage image = null;

        try
        {
            if (connection == null) return false;

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
            {
                @Override
                public synchronized byte[] toByteArray()
                {
                    return this.buf;
                }
            };

            ByteStreams.copy(connection.getInputStream(), outputStream);
            image = GifDecoder.read(new ByteArrayInputStream(outputStream.toByteArray(), 0, outputStream.size()));
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("Exception getting image from InputStream", e);
        }

        if (image == null) return false;

        try
        {
            int numberOfFrames = image.getFrameCount();
            if (numberOfFrames == 0) return false;

            for (int i = 0; i < image.getFrameCount(); i++)
            {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image.getFrame(i), "gif", outputStream);
                NativeImage nativeImage = NativeImage.read(new ByteArrayInputStream(outputStream.toByteArray()));

                ResourceLocation resourceLocation = Minecraft.getInstance().getTextureManager()
                        .register("chat_embed_animated_image_frame_"
                                + i, new DynamicTexture(nativeImage));
                frames.add(new Frame(nativeImage, resourceLocation, image.getDelay(i)));
            }
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("Exception loading animated image", e);
        }

        return !frames.isEmpty();
    }

    public List<AnimatedImageEmbed.Frame> getFrames()
    {
        return Collections.unmodifiableList(frames);
    }

    public static class Frame
    {
        private final NativeImage image, scaledImage;
        private final ResourceLocation resourceLocation;
        private final int delay;

        private Frame(NativeImage image, ResourceLocation resourceLocation, int delay)
        {
            this.image = image;
            ChatComponent gui = Minecraft.getInstance().gui.getChat();
            this.scaledImage = ImageEmbed.scaleImage(image,
                    Math.min(ChatEmbedsConfig.GeneralConfig.chatImageEmbedMaxWidth, gui.getWidth()),
                    ChatEmbedsConfig.GeneralConfig.chatImageEmbedMaxHeight);
            this.resourceLocation = resourceLocation;
            this.delay = delay;
        }

        public NativeImage getImage()
        {
            return image;
        }

        public NativeImage getScaledImage()
        {
            return scaledImage;
        }

        public ResourceLocation getResourceLocation()
        {
            return resourceLocation;
        }

        public int getDelay()
        {
            return delay;
        }
    }
}
