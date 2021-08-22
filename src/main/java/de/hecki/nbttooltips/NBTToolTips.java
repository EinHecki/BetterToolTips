package de.hecki.nbttooltips;

import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;

import java.util.List;

public class NBTToolTips extends LabyModAddon {

    private static boolean showShulkerToolTipInstant;
    private static boolean showMapToolTip;

    @Override
    public void onEnable() {

    }

    @Override
    public void loadConfig() {
        showShulkerToolTipInstant = getConfig().has("showShulkerToolTipInstant") &&
                getConfig().get("showShulkerToolTipInstant").getAsBoolean();
        showMapToolTip = !getConfig().has("showMapToolTip") || getConfig().get("showMapToolTip").getAsBoolean();
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
        list.add(new BooleanElement("Show Shulker Box tooltip immediately", this,
                new ControlElement.IconData(Material.SHULKER_BOX), "showShulkerToolTipInstant",
                showShulkerToolTipInstant).addCallback(aBoolean -> showShulkerToolTipInstant = aBoolean));
        list.add(new BooleanElement("Show map tooltip", this,
                new ControlElement.IconData(Material.FILLED_MAP), "showMapToolTip",
                showMapToolTip).addCallback(aBoolean -> showMapToolTip = aBoolean));
    }

    public static boolean showShulkerToolTipInstant() {
        return showShulkerToolTipInstant;
    }
    public static boolean showMapToolTip() {
        return showMapToolTip;
    }
}
