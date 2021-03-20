package Prismriver.core;

import basemod.*;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class PrismriverCore implements EditStringsSubscriber, PostInitializeSubscriber {

    public static final String modID = "prismriver";
    public static String AUDIO_DIR = "prismriver/";
    public static String defaultKey = "Default";
    public static Boolean wildCardMode = false;
    public static String currentKey = "";
    public ArrayList<String> keyList = new ArrayList<>();
    private static SpireConfig modConfig = null;
    private float xPos = 350f, yPos = 750f, orgYPos = 750f;
    private ModPanel settingsPanel;
    private int curPage = 1;

    public static final Logger logger = LogManager.getLogger(PrismriverCore.class.getName());
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
        try {
            Properties defaults = new Properties();
            defaults.put("currentKey", defaultKey);
            defaults.put("wildCard", wildCardMode);
            modConfig = new SpireConfig("PrismRiver", "Config", defaults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receivePostInitialize(){
        File file = new File(AUDIO_DIR);
        if (!file.exists() || !file.isDirectory()) { file.mkdir(); }
        File directoryPath = new File(AUDIO_DIR);
        File[] filesList = directoryPath.listFiles();
        assert filesList != null;
        for(File f : filesList) { if(f.isDirectory() && !f.getName().equals(defaultKey)) { keyList.add(f.getName()); } }
        UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(makeID("OptionsMenu"));
        String[] TEXT = UIStrings.TEXT;
        settingsPanel = new ModPanel();
        ModLabeledToggleButton defaultButton = new ModLabeledToggleButton(TEXT[0], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showDefault(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setString("currentKey", defaultKey);
                        saveConfig();
                    }
                });
        registerUIElement(defaultButton, true);
        ModLabeledToggleButton wildCardButton = new ModLabeledToggleButton(TEXT[1], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showDefault(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("wildCard", wildCardMode);
                        saveConfig();
                    }
                });
        registerUIElement(wildCardButton, true);
        for(String key: keyList){
            ModLabeledToggleButton keyButton = new ModLabeledToggleButton(String.format(TEXT[2], key), xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showKey(key), settingsPanel, l -> {
            },
                    button ->
                    {
                        if (modConfig != null) {
                            modConfig.setString("currentKey", key);
                            saveConfig();
                        }
                    });
            registerUIElement(keyButton, true);
        }
        if (pages.size() > 1) {
            ModLabeledButton FlipPageBtn = new ModLabeledButton(TEXT[3], xPos + 450f, orgYPos + 45f, Settings.CREAM_COLOR, Color.WHITE, FontHelper.cardEnergyFont_L, settingsPanel,
                    button ->
                    {
                        if (pages.containsKey(curPage + 1)) {
                            changePage(curPage + 1);
                        } else {
                            changePage(1);
                        }
                    });
            settingsPanel.addUIElement(FlipPageBtn);
        }
        currentKey = modConfig.getString("currentKey");
        BaseMod.registerModBadge(ImageMaster.loadImage(modID + "Resources/images/modBadge.png"), modID, "squeeny", "", settingsPanel);

    }

    public static boolean showDefault() {
        if (modConfig == null) { return false; }
        return modConfig.getString("currentKey").equals(defaultKey);
    }

    public static boolean showKey(String key) {
        if (modConfig == null) { return false; }
        return modConfig.getString("currentKey").equals(key);
    }

    private final float pageOffset = 12000f;
    private HashMap<Integer, ArrayList<IUIElement>> pages = new HashMap<Integer, ArrayList<IUIElement>>() {{
        put(1, new ArrayList<>());
    }};
    private float elementSpace = 50f;
    private float yThreshold = yPos - elementSpace * 12;

    private void registerUIElement(IUIElement elem, boolean decrement) {
        settingsPanel.addUIElement(elem);

        int page = pages.size() + (yThreshold == yPos ? 1 : 0);
        if (!pages.containsKey(page)) {
            pages.put(page, new ArrayList<>());
            yPos = orgYPos;
            elem.setY(yPos);
        }
        if (page > curPage) {
            elem.setX(elem.getX() + pageOffset);
        }
        pages.get(page).add(elem);

        if (decrement) {
            yPos -= elementSpace;
        }
    }

    private void changePage(int i) {
        for (IUIElement e : pages.get(curPage)) {
            e.setX(e.getX() + pageOffset);
        }

        for (IUIElement e : pages.get(i)) {
            e.setX(e.getX() - pageOffset);
        }
        curPage = i;
    }

    private void saveConfig() {
        try {
            currentKey = modConfig.getString("currentKey");
            wildCardMode = modConfig.getBool("wildCard");
            modConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(UIStrings.class, modID + "Resources/localization/eng/ui.json");
    }

}
