package com.rarestardev.vibeplayer.Model;

public class ViewModeLayoutModel {

    private String viewModeName;
    private int viewModeIcon;

    public ViewModeLayoutModel(String viewModeName, int viewModeIcon) {
        this.viewModeName = viewModeName;
        this.viewModeIcon = viewModeIcon;
    }

    public String getViewModeName() {
        return viewModeName;
    }

    public void setViewModeName(String viewModeName) {
        this.viewModeName = viewModeName;
    }

    public int getViewModeIcon() {
        return viewModeIcon;
    }

    public void setViewModeIcon(int viewModeIcon) {
        this.viewModeIcon = viewModeIcon;
    }
}
