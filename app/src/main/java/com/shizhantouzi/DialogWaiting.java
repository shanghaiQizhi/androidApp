package com.shizhantouzi;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class DialogWaiting extends Dialog {

    private static int default_width = 150; // 默认宽度
    private static int default_height = 150;// 默认高度

    public DialogWaiting(Context context) {
        this(context, default_width, default_height);
    }

    public DialogWaiting(Context context, int width, int height) {
        super(context, R.style.my_progress);

        // set content
        setContentView(R.layout.activity_dialog_waiting);

        // set window params
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        // set width,height by density and gravity
        float density = getDensity(context);
        params.width = (int) (width * density);
        params.height = (int) (height * density);
        params.gravity = Gravity.CENTER;

        window.setAttributes(params);
        this.setCanceledOnTouchOutside(false);
    }

    public static DialogWaiting show(Context context) {
        DialogWaiting progress = new DialogWaiting(context);
        progress.show();
        return progress;
    }

    public static DialogWaiting show(Context context, CharSequence message) {
        DialogWaiting progress = new DialogWaiting(context);
        //((TextView) progress.findViewById(R.id.text1)).setText(message);
        progress.show();
        return progress;
    }

    public void dimiss() {
        this.cancel();
    }

    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }
}
