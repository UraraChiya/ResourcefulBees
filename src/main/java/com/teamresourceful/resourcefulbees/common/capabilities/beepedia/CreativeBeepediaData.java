package com.teamresourceful.resourcefulbees.common.capabilities.beepedia;

public class CreativeBeepediaData extends BeepediaData {

    public static final CreativeBeepediaData INSTANCE = new CreativeBeepediaData();

    private CreativeBeepediaData() {
        // Singleton
    }

    @Override
    public boolean hasBee(String id) {
        return true;
    }
}
