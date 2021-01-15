package com.zubisofts.menuqrgenerator.ui.menu_activity;

import com.zubisofts.menuqrgenerator.model.Restaurant;

public interface PageChangedListener {

    public void onNextPageClicked(boolean next);
    public void onFormCompleted(Restaurant data, boolean done, boolean b);

}
