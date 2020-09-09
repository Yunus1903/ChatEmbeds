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
        private final String url;
        private final int ticks, chatLineId;

        /**
         * Builder constructor
         * @param url Source of the embed as a String
         * @param ticks Minecraft ticks
         * @param chatLineId ID of the chat line
         */
        public Builder(String url, int ticks, int chatLineId)
        {
            this.url = url;
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
            URL parsedURL = parseURL(url);
            if (parsedURL == null) return null;

            String extension = parsedURL.toString().substring(parsedURL.toString().lastIndexOf(".") + 1);
            if (extension.contains("?")) extension = extension.substring(0, extension.indexOf("?"));

            try
            {
                String imageURL = ImageExtractor.extractImageURL(parsedURL);
                if (imageURL != null)
                {
                    parsedURL = new URL(imageURL);
                    ChatEmbeds.LOGGER.debug(parsedURL);
                    if (extension.equals("gif") || extension.equals("gifv"))
                    {
                        if (ChatEmbedsConfig.GeneralConfig.enableAnimatedImageEmbeds)
                            return new AnimatedImageEmbed(parsedURL, ticks, chatLineId);
                    }
                    else if (ChatEmbedsConfig.GeneralConfig.enableImageEmbeds)
                        return new ImageEmbed(parsedURL, ticks, chatLineId);
                }
            }
            catch (MalformedURLException e)
            {
                ChatEmbeds.LOGGER.debug("Failed to recreate URL", e);
            }
            catch (IOException ignored) { }

            if (ChatEmbedsConfig.GeneralConfig.enableTextEmbeds) return new TextEmbed(parsedURL, ticks, chatLineId);
            return null;
        }

        /**
         * Parse string into {@link URL}
         * @param url URL as a string
         * @return Parsed {@link URL}
         */
        @Nullable
        private static URL parseURL(String url)
        {
            URL parsedURL = null;
            try
            {
                if (new URI(url).getScheme() == null)
                {
                    url = "http://" + url;
                }

                parsedURL = new URL(url);
            }
            catch (MalformedURLException | URISyntaxException e)
            {
                ChatEmbeds.LOGGER.error("Failed to parse URL", e);
            }
            return parsedURL;
        }
    }
}
