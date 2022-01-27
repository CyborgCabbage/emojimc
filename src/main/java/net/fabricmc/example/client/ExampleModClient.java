package net.fabricmc.example.client;

import com.ibm.icu.util.CodePointMap;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.example.mixin.FontManagerAccessor;
import net.fabricmc.example.mixin.FontStorageAccessor;
import net.fabricmc.example.mixin.MinecraftClientAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BlankGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.IntStream;

public class ExampleModClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("modid");
    ArrayList<Integer> allSymbols = new ArrayList<>();
    boolean checkedEmoji = false;
    ArrayList<String> emojiList = new ArrayList<>();
    @Override
    public void onInitializeClient() {
        for(int c = 0; c <= 1000000; c++){
            if(Character.getType(c) != Character.OTHER_SYMBOL) continue;
            if(Character.UnicodeScript.of(c) != Character.UnicodeScript.COMMON) continue;
            if(Character.UnicodeBlock.of(c) == Character.UnicodeBlock.ENCLOSED_ALPHANUMERICS) continue;
            if(Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY) continue;
            if(Character.UnicodeBlock.of(c) == Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS) continue;
            if(Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_STROKES) continue;
            if(Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CONTROL_PICTURES) continue;
            if(Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KANBUN) continue;
            allSymbols.add(c);
        }
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if(!checkedEmoji) {
                FontManager fontManager = ((MinecraftClientAccessor) client).getFontManager();
                Map<Identifier, FontStorage> fontStorages = ((FontManagerAccessor) fontManager).getFontStorages();
                if(fontStorages.size() >= 4) {
                    fontStorages.forEach((identifier, fontStorage) -> LOGGER.info(identifier.toString()));
                    for(Integer codepoint: allSymbols){
                        String string = Character.toString(codepoint);
                        //All Characters
                        /*fontStorages.forEach((identifier, fontStorage) -> {
                            if(!fontStorage.getGlyph(codepoint).equals(BlankGlyph.INSTANCE)){
                                if(!emojiList.contains(string)) emojiList.add(string);
                            }
                        });*/
                        //Lowres Characters
                        List<Font> fonts = ((FontStorageAccessor)fontStorages.get(new Identifier("default"))).getFonts();
                        for(Font font: fonts) {
                            if(font.getProvidedGlyphs().size() < 5000) {
                                if (font.getGlyph(codepoint) != null) {
                                    if (!emojiList.contains(string)) emojiList.add(string);
                                }
                            }
                        }
                    }
                    FontStorage fontStorage = fontStorages.get(new Identifier("default"));
                    List<Font> fonts = ((FontStorageAccessor)fontStorage).getFonts();
                    LOGGER.info("Font sizes");
                    for(Font font: fonts){
                        LOGGER.info(String.valueOf(font.getProvidedGlyphs().size()));
                    }
                    LOGGER.info(String.join("",emojiList));
                    checkedEmoji = true;
                }
            }
        });
    }
}
