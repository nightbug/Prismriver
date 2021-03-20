package Prismriver.core;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;

import java.io.File;
import java.io.IOException;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class PrismriverCore implements EditStringsSubscriber, PostInitializeSubscriber {

    public static final String modID = "prismriver";
    public static String MOD_DIR = "prismriver/";

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }


    public PrismriverCore() {
        BaseMod.subscribe(this);
    }

    public static String makePath(String resourcePath) {
        return modID + "Resources/" + resourcePath;
    }

    public static String makeImagePath(String resourcePath) {
        return modID + "Resources/images/" + resourcePath;
    }


    public static void initialize() {
        PrismriverCore thismod = new PrismriverCore();
    }

    public void receivePostInitialize(){
        File file = new File(MOD_DIR);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdir();
        }
    }
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, modID + "Resources/localization/eng/Cardstrings.json");

        BaseMod.loadCustomStringsFile(RelicStrings.class, modID + "Resources/localization/eng/Relicstrings.json");

        BaseMod.loadCustomStringsFile(CharacterStrings.class, modID + "Resources/localization/eng/Charstrings.json");

        BaseMod.loadCustomStringsFile(PowerStrings.class, modID + "Resources/localization/eng/Powerstrings.json");
    }

}
