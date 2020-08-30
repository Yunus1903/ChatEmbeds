package com.yunus1903.chatembeds.client.embed;

import com.yunus1903.chatembeds.ChatEmbeds;
import com.yunus1903.chatembeds.ChatEmbedsConfig;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.IReorderingProcessor;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Abstract class for chat embeds
 * @author Yunus1903
 * @since 29/08/2020
 */
public abstract class Embed
{
    private static final String USER_AGENT = "Mozilla/4.0";

    final URL url;
    final int ticks, chatLineId;
    @Nullable
    final HttpURLConnection connection;
    private final List<? extends ChatLine<IReorderingProcessor>> chatLines;

    /**
     * Constructor
     * @param url {@link URL} source of the embed
     * @param ticks Minecraft ticks
     * @param chatLineId ID of the chat line
     */
    Embed(URL url, int ticks, int chatLineId)
    {
        this.url = url;
        this.ticks = ticks;
        this.chatLineId = chatLineId;
        this.connection = openConnection();
        this.chatLines = createChatLines();
        if (this.connection != null) this.connection.disconnect();
    }

    /**
     * Opens {@link HttpURLConnection URL connection}
     * @return Instance of {@link HttpURLConnection}
     */
    @Nullable
    private HttpURLConnection openConnection()
    {
        try
        {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);
            return connection;
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("Failed to open connection", e);
        }
        return null;
    }

    /**
     * Creates the chatlines
     * @return A list of {@link ChatLine chatlines}
     */
    abstract List<? extends ChatLine<IReorderingProcessor>> createChatLines();

    /**
     * @return List of {@link ChatLine chatlines}
     */
    public List<? extends ChatLine<IReorderingProcessor>> getChatLines()
    {
        return chatLines;
    }

    /**
     * @return Source {@link Embed#url} of this embed
     */
    public URL getUrl()
    {
        return url;
    }

    /**
     * Builder class to create the {@link Embed} instance
     */
    public static class Builder
    {
        private final URL url;
        private final int ticks, chatLineId;

        /**
         * Builder constructor
         * @param url {@link URL} source of the embed
         * @param ticks Minecraft ticks
         * @param chatLineId ID of the chat line
         */
        public Builder(String url, int ticks, int chatLineId)
        {
            URL url1 = null;
            try
            {
                if (new URI(url).getScheme() == null)
                {
                    url = "http://" + url;
                }

                url1 = new URL(url);
            }
            catch (MalformedURLException | URISyntaxException e)
            {
                ChatEmbeds.LOGGER.error("Failed to parse URL", e);
            }

            this.url = url1;
            this.ticks = ticks;
            this.chatLineId = chatLineId;
        }

        /**
         * Builds the embed
         * @return A instance of {@link Embed}
         */
        @Nullable
        public Embed build()
        {
            String extension = url.toString().substring(url.toString().lastIndexOf(".") + 1);
            if (extension.contains("?")) extension = extension.substring(0, extension.indexOf("?"));

            if (extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg"))
            {
                if (ChatEmbedsConfig.GeneralConfig.enableImageEmbeds)
                    return new ImageEmbed(url, ticks, chatLineId);
            }
            else if (extension.equals("gif"))
            {
                if (ChatEmbedsConfig.GeneralConfig.enableAnimatedImageEmbeds)
                    return new AnimatedImageEmbed(url, ticks, chatLineId);
            }
            else if (ChatEmbedsConfig.GeneralConfig.enableTextEmbeds) return new TextEmbed(url, ticks, chatLineId);
            return null;
        }
    }
}
