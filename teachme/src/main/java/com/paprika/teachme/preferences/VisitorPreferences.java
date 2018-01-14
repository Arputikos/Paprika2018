package com.paprika.teachme.preferences;

import android.content.Context;

import com.indoorway.android.common.sdk.tools.settings.Settings;
import com.indoorway.android.common.sdk.tools.settings.source.SharedPreferencesStorage;

public class VisitorPreferences extends Settings {

    public final BooleanEntry logged = new BooleanEntry("logged");
    public final StringEntry token = new StringEntry("token");
    public final StringEntry lastBuildingUuid = new StringEntry("last-building-uuid");
    public final StringEntry lastMapUuid = new StringEntry("last-map-uuid");

    public VisitorPreferences(Context context) {
        super(new SharedPreferencesStorage(context, "visitor"));
    }

}
