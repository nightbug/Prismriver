package Prismriver.patch;

import Prismriver.core.PrismriverCore;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.audio.MockMusic;
import com.megacrit.cardcrawl.audio.TempMusic;

import java.io.File;
import java.net.URL;
import java.util.stream.StreamSupport;

@SpirePatch(
        clz = MainMusic.class,
        method = "newMusic",
        paramtypez={
                String.class
        }
)
public class MusicPatch2 {
    @SpirePrefixPatch
    public static SpireReturn<Music> musicPatch(String key) {
        if (PrismriverCore.currentKey != PrismriverCore.defaultKey) {
            String[] keys = key.split("/");
            String definitiveKey = "";
            for (String s : keys) {
                if (!s.equals("audio") && !s.equals("music")) {
                    definitiveKey = s;
                }
            }
            System.out.println(definitiveKey);
            File audioFile = new File(PrismriverCore.AUDIO_DIR + PrismriverCore.currentKey + "/" + definitiveKey);
            PrismriverCore.logger.info(audioFile.exists());
            if (audioFile.exists()) {
                if (Gdx.audio == null) {
                    new MockMusic();
                } else {
                    try {
                        PrismriverCore.logger.info("Found Custom Audio, PLaying: " + definitiveKey + " from musicpack " + PrismriverCore.currentKey);
                        return SpireReturn.Return(Gdx.audio.newMusic(Gdx.files.absolute(audioFile.getAbsolutePath())));
                    } catch (Exception e) {
                        PrismriverCore.logger.info("Couldn't find Custom Audio, playing Basegame Default instead.");
                        return SpireReturn.Continue();
                    }
                }
            }
        } else {
            assert Gdx.audio != null;
            return SpireReturn.Continue();
        }
    }
}