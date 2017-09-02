package com.rolandoasmat.nvelope.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by rolandoasmat on 9/2/17.
 */

public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        String recipeTitle = intent.getStringExtra(NvelopeWidget.INTENT_EXTRA_PAYMENT_METHOD);
        return (new PaymentMethodsListRemoteViewsFactory(getApplicationContext(), recipeTitle));
    }
}
// todo add to manifest