package com.example.onetour.graphiccontroller;

public final class NavigatorBase {

    private static NavigatorController navigatorController;

    private NavigatorBase() {
    }

    public static void setNavigatorController(NavigatorController controller) {
        navigatorController = controller;
    }

    public static void goTo(String fxmlPath) {
        if (navigatorController == null) {
            throw new IllegalStateException("NavigatorController non inizializzato.");
        }
        navigatorController.setCenter(fxmlPath);
    }

    public static void refreshHeader() {
        if (navigatorController == null) {
            throw new IllegalStateException("NavigatorController non inizializzato.");
        }
        navigatorController.refreshHeader();
    }
    
    public static void hideHeader() {
        if (navigatorController == null) {
            throw new IllegalStateException("NavigatorController non inizializzato.");
        }
        navigatorController.setHeaderVisible(false);
    }

    public static void showHeader() {
        if (navigatorController == null) {
            throw new IllegalStateException("NavigatorController non inizializzato.");
        }
        navigatorController.setHeaderVisible(true);
    }
}
