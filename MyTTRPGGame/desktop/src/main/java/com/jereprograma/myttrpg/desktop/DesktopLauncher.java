package com.jereprograma.myttrpg.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.jereprograma.myttrpg.core.GameApp;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("My TTRPG Sandbox");
        config.setWindowedMode(800, 600);
        new Lwjgl3Application(new GameApp(), config);
    }
}
