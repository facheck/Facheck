package com.fzu.facheck.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * @date: 2019/4/23
 * @author: wyz
 * @version:
 * @description: snackBar 工具
 */
public class SnackbarUtil {
    public static void showMessage(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }
}
