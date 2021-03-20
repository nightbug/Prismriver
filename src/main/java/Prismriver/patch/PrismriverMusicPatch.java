package Prismriver.patch;

import Prismriver.core.PrismriverCore;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.audio.MockMusic;
import com.megacrit.cardcrawl.audio.TempMusic;

import java.io.File;
import java.net.URL;
import java.util.stream.StreamSupport;

import static Prismriver.core.PrismriverCore.*;

@SpirePatch(
        clz = MainMusic.class,
        method = "newMusic",
        paramtypez={
                String.class
        }
)
public class PrismriverMusicPatch {

    public static String songKey = "";

    @SpirePrefixPatch
    public static SpireReturn<Music> musicPatch(String path) {
        if(isDefaultKey()){
            assert Gdx.audio != null;
            return SpireReturn.Continue();
        }
        else {
            File audioFile;
            String storedKey = "";
            if(isWildCardModeEnabled()){
                storedKey = returnRandomFolderForWildCard();
                audioFile = new File(generateWildCardReplacementKey(path, storedKey));
                logger.info("Generated WildCard Song:");
                logger.info(songKey);
                logger.info(audioFile.getAbsolutePath());
            }
            else {
                audioFile = new File(generateReplacementKey(path));
                logger.info("Generated Song:");
                logger.info(songKey);
                logger.info(audioFile.getAbsolutePath());
            }
            if (audioFile.exists()) {
                if (Gdx.audio == null) { new MockMusic();
                } else {
                    try {
                        PrismriverCore.logger.info("Found Custom Audio, Playing: " + songKey + " from musicpack " + (isWildCardModeEnabled() ? storedKey : getCurrentKey()));
                        return SpireReturn.Return(Gdx.audio.newMusic(Gdx.files.absolute(audioFile.getAbsolutePath())));
                    } catch (Exception e) { }
                }
            }
            PrismriverCore.logger.info("Couldn't find Custom Audio, playing Basegame Default instead.");
            return SpireReturn.Continue();
        }
    }

    public static String generateReplacementKey(String path){
        String[] splitPath = path.split("/");
        String audioKey = "";
        for (String s : splitPath) {
            if (!s.equals("audio") && !s.equals("music")) { audioKey = s; }
        }
        songKey = audioKey;
        audioKey = getAudioDir() + getCurrentKey() + "/" + audioKey;
        return audioKey;
    }

    public static String generateWildCardReplacementKey(String path, String proxyKey){
        String[] splitPath = path.split("/");
        String audioKey = "";
        for (String s : splitPath) {
            if (!s.equals("audio") && !s.equals("music")) { audioKey = s; }
        }
        songKey = audioKey;
        audioKey = getAudioDir() + proxyKey + "/" + audioKey;
        return audioKey;
    }

}