package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.User;
import com.example.investmentportfoliorebalancingtool.domain.Widget;

public interface WidgetService {
    boolean addWidgetToDashboard(User user, Widget widget);

    // update, when -
    // user logs in and asset values are updated
    // user adds a new account
    // user manually adds an asset (collection) to existing account
    // user adds/removes/edits a template
    boolean updateWidgetOnDashboard(User user, Widget widget);
    boolean removeWidgetFromDashboard(User user, Widget widget);

}


