package com.castiamc.client.hack;

import net.minecraft.client.OptionInstance;
import net.minecraft.util.Mth;
import com.castiamc.client.CastiaClient;
import com.castiamc.client.events.UpdateListener;
import com.castiamc.client.keybinds.KeybindList;
import com.castiamc.client.settings.CheckboxSetting;
import com.castiamc.client.settings.EnumSetting;
import com.castiamc.client.settings.SliderSetting;
import com.castiamc.client.settings.SliderSetting.ValueDisplay;

public final class FullbrightHack extends Hack implements UpdateListener
{
    private final EnumSetting<Method> method = new EnumSetting<>("Method",
        "Gamma works by setting your brightness beyond 100%.\n"
      + "Night Vision works by applying the night vision effect.",
        Method.values(), Method.GAMMA);

    private final CheckboxSetting fade = new CheckboxSetting("Fade",
        "Slowly fades between brightness and darkness.", true);

    private final SliderSetting defaultGamma = new SliderSetting(
        "Default brightness",
        "Fullbright will set your brightness slider back to this value when you turn it off.",
        0.5, 0, 1, 0.01, ValueDisplay.PERCENTAGE);

    private boolean wasGammaChanged;
    private float nightVisionStrength;

    public FullbrightHack()
    {
        super("Fullbright");
        addSetting(method);
        addSetting(fade);
        addSetting(defaultGamma);

        checkGammaOnStartup();
        CastiaClient.INSTANCE.getEventManager().add(UpdateListener.class, this);
    }

    private void checkGammaOnStartup()
    {
        CastiaClient.INSTANCE.getEventManager().add(UpdateListener.class, new UpdateListener() {
            @Override
            public void onUpdate()
            {
                double gamma = CastiaClient.MC.options.gamma().get();
                if(gamma > 1) wasGammaChanged = true;
                else defaultGamma.setValue(gamma);

                // Remove this one-time listener
                CastiaClient.INSTANCE.getEventManager().remove(UpdateListener.class, this);
            }
        });
    }

    @Override
    public void onUpdate()
    {
        updateGamma();
        updateNightVision();
    }

    private void updateGamma()
    {
        if(isChangingGamma())
        {
            setGamma(16);
            return;
        }

        if(wasGammaChanged)
            resetGamma(defaultGamma.getValue());
    }

    private void setGamma(double target)
    {
        wasGammaChanged = true;
        OptionInstance<Double> gammaOption = CastiaClient.MC.options.gamma();
        double oldGammaValue = gammaOption.get();

        if(!fade.isChecked() || Math.abs(oldGammaValue - target) <= 0.5)
        {
            gammaOption.set((float)target);
            return;
        }

        if(oldGammaValue < target)
            gammaOption.set((float)(oldGammaValue + 0.5));
        else
            gammaOption.set((float)(oldGammaValue - 0.5));
    }

    private void resetGamma(double target)
    {
        OptionInstance<Double> gammaOption = CastiaClient.MC.options.gamma();
        double oldGammaValue = gammaOption.get();

        if(!fade.isChecked() || Math.abs(oldGammaValue - target) <= 0.5)
        {
            gammaOption.set((float)target);
            wasGammaChanged = false;
            return;
        }

        if(oldGammaValue < target)
            gammaOption.set((float)(oldGammaValue + 0.5));
        else
            gammaOption.set((float)(oldGammaValue - 0.5));
    }

    private void updateNightVision()
    {
        boolean shouldGiveNightVision = isEnabled() && method.getSelected() == Method.NIGHT_VISION;

        if(fade.isChecked())
        {
            if(shouldGiveNightVision) nightVisionStrength += 0.03125f;
            else nightVisionStrength -= 0.03125f;

            nightVisionStrength = Mth.clamp(nightVisionStrength, 0, 1);
        }
        else
        {
            nightVisionStrength = shouldGiveNightVision ? 1 : 0;
        }
    }

    public boolean isNightVisionActive() { return nightVisionStrength > 0; }
    public float getNightVisionStrength() { return nightVisionStrength; }
    public boolean isChangingGamma() { return isEnabled() && method.getSelected() == Method.GAMMA; }
    public double getDefaultGamma() { return defaultGamma.getValue(); }

    private static enum Method
    {
        GAMMA("Gamma"),
        NIGHT_VISION("Night Vision");

        private final String name;
        private Method(String name) { this.name = name; }
        @Override
        public String toString() { return name; }
    }
}
