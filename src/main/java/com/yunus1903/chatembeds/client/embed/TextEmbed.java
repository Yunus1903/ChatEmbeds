package com.yunus1903.chatembeds.client.embed;

import com.yunus1903.chatembeds.ChatEmbeds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Text chat embed
 * @author Yunus1903
 * @since 29/08/2020
 */
public class TextEmbed extends Embed
{
    @Nullable
    private FormattedText title, description;

    TextEmbed(URL url, int ticks, int chatLineId)
    {
        super(url, ticks, chatLineId);
    }

    @Override
    List<? extends GuiMessage<FormattedCharSequence>> createChatLines()
    {
        List<GuiMessage<FormattedCharSequence>> lines = new ArrayList<>();
        if (!loadText() || title == null) return lines;

        lines.add(new GuiMessage<>(ticks, Language.getInstance().getVisualOrder(new TextComponent("")), chatLineId));
        lines.add(new GuiMessage<>(ticks, Language.getInstance().getVisualOrder(title), chatLineId));
        lines.add(new GuiMessage<>(ticks, Language.getInstance().getVisualOrder(new TextComponent("")), chatLineId));
        if (description != null)
        {
            int i = Mth.floor((double) ChatComponent.getWidth(Minecraft.getInstance().options.chatWidth) / Minecraft.getInstance().options.chatScale);
            description = ((MutableComponent) description).withStyle(ChatFormatting.GRAY);
            List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(description, i, Minecraft.getInstance().font);
            list.forEach(line -> lines.add(new GuiMessage<>(ticks, line, chatLineId)));
            lines.add(new GuiMessage<>(ticks, Language.getInstance().getVisualOrder(new TextComponent("")), chatLineId));
        }

        return lines;
    }

    private boolean loadText()
    {
        HTMLDocument doc = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
        HTMLEditorKit.Parser parser = new ParserDelegator();

        try
        {
            if (connection == null) return false;
            parser.parse(new InputStreamReader(connection.getInputStream()), doc.getReader(0), true);
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("Exception reading HTML", e);
        }

        try
        {
            title = new TextComponent(doc.getProperty("title").toString()).withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);
        }
        catch (NullPointerException ignored) { }

        if (title == null)
        {
            try
            {
                title = new TextComponent((String) doc.getElement(doc.getDefaultRootElement(), HTML.Attribute.NAME, "title").getAttributes().getAttribute(HTML.Attribute.CONTENT)).withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);
            }
            catch (NullPointerException ignored) { }
        }

        try
        {
            String desc = (String) doc.getElement(doc.getDefaultRootElement(), HTML.Attribute.NAME, "description").getAttributes().getAttribute(HTML.Attribute.CONTENT);
            description = new TextComponent(desc.replace("\r", "\n"));
        }
        catch (NullPointerException ignored) { }

        return true;
    }

    @Nullable
    public FormattedText getTitle()
    {
        return title;
    }

    @Nullable
    public FormattedText getDescription()
    {
        return description;
    }
}
