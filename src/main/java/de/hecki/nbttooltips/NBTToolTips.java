package de.hecki.nbttooltips;

import com.google.gson.JsonObject;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;

import java.util.List;

public class NBTToolTips extends LabyModAddon {

    private static boolean showShulkerToolTipInstant;

    @Override
    public void onEnable() {

    }

    @Override
    public void loadConfig() {
        showShulkerToolTipInstant = getConfig().has("showShulkerToolTipInstant") &&
                getConfig().get("showShulkerToolTipInstant").getAsBoolean();
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
        list.add(new BooleanElement("Show Shulker Box tooltip immediately", this,
                new ControlElement.IconData(Material.SHULKER_BOX), "showShulkerToolTipInstant",
                showShulkerToolTipInstant).addCallback(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                showShulkerToolTipInstant = aBoolean;
            }
        }));
    }

    public static boolean showShulkerToolTipInstant() {
        return showShulkerToolTipInstant;
    }
}
