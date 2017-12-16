package com.zzia.graduationproject.ui.activity.rongyun;

import android.net.Uri;

import com.google.gson.reflect.TypeToken;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.model.User;
import com.zzia.graduationproject.utils.SharePreferenceUtils;

import java.util.List;

import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.widget.provider.PrivateConversationProvider;

/**
 * Created by fyc on 2017/12/16.
 */
@ConversationProviderTag(
        conversationType = "private",
        portraitPosition = 1
)
public class MyPrivateConversationProvider extends PrivateConversationProvider {
    @Override
    public String getTitle(String userId) {
        String title = "";
        List<User> users = (List<User>) SharePreferenceUtils.get(App.getInstance().getApplicationContext(), "friends", new TypeToken<List<User>>() {
        }.getType());
        if (users != null && users.size() > 0) {
            for (User model : users) {
                if (model.getUserId().equals(userId)) {
                    title = model.getRemark();
                    break;
                }
            }
        } else {
            title = super.getTitle(userId);
        }
        return title;
    }

    @Override
    public Uri getPortraitUri(String userId) {
        Uri portraitUri = null;
        List<User> users = (List<User>) SharePreferenceUtils.get(App.getInstance().getApplicationContext(), "friends", new TypeToken<List<User>>() {
        }.getType());
        if (users != null && users.size() > 0) {
            for (User model : users) {
                if (model.getUserId().equals(userId)) {
                    portraitUri = Uri.parse(model.getAvatar());
                    break;
                }
            }
        } else {
            portraitUri = super.getPortraitUri(userId);
        }
        return portraitUri;
    }
}
