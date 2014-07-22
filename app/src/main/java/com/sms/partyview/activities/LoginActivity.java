package com.sms.partyview.activities;

import android.graphics.Typeface;
import android.widget.TextView;

import com.parse.ui.ParseLoginDispatchActivity;
import com.sms.partyview.R;

// This class uses the ParseLoginUI module to generate sign-up and login activities.
public class LoginActivity extends ParseLoginDispatchActivity {
    @Override
    protected Class<?> getTargetClass() {
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTextColor(getResources().getColor(R.color.white));
        yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
        yourTextView.setPadding(0,0,0,5);
        yourTextView.setTextSize(22);
        return HomeActivity.class;
    }
}
