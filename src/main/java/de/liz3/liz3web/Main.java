package de.liz3.liz3web;

import de.liz3.liz3web.gui.GuiManager;

/**
 * Created by yannh on 29.11.2016.
 */
public class Main {

    public static boolean isDebug = false;


    public static void main(String[] args) {
        System.out.println("Start of the Program");
        for(String str : args) {

            if(str.equalsIgnoreCase("-debug")) {

                isDebug = true;
                System.out.println("Debug mode on");
            }
        }

           dPrint("Starting Gui");

        GuiManager.setupGui();



    }

    public static void dPrint(String msg) {
        if(isDebug) {
            System.out.println(msg);
        }
    }
}
