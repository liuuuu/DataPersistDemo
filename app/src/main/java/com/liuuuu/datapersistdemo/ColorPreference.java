package com.liuuuu.datapersistdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

public class ColorPreference extends DialogPreference {

    private static final int DEFAULT_COLOR = Color.WHITE;
    /* 当前颜色设置的本地副本 */
    private int mCurrentColor;
    /* 设置颜色组件的滑块 */
    private SeekBar mRedLevel, mGreenLevel, mBlueLevel;

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        // 创建对话框的内容视图
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.preference_color, null);
        mRedLevel = rootView.findViewById(R.id.select_red);
        mGreenLevel = rootView.findViewById(R.id.select_green);
        mBlueLevel = rootView.findViewById(R.id.select_blue);
        mRedLevel.setProgress(Color.red(mCurrentColor));
        mGreenLevel.setProgress(Color.green(mCurrentColor));
        mBlueLevel.setProgress(Color.blue(mCurrentColor));

        // 附加内容视图
        builder.setView(rootView);
        super.onPrepareDialogBuilder(builder);
    }

    /**
     * 当用户点击按钮关闭对话框时调用
     *
     * @param positiveResult
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // 按下OK键时，获得并保存颜色值
            int color = Color.rgb(mRedLevel.getProgress(), mGreenLevel.getProgress(), mBlueLevel.getProgress());
            setCurrentValue(color);
        }
    }

    /**
     * 由框架调用，用于获得传入首选项 XML 定义的默认值
     *
     * @param a
     * @param index
     * @return
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // 作为颜色 int 值返回来自 XML 的默认值
        ColorStateList value = a.getColorStateList(index);
        if (value == null) {
            return DEFAULT_COLOR;
        }
        return value.getDefaultColor();
    }

    /**
     * 由框架调用，设置首选项的初始值
     * 该值来自默认值或上一次持久化的值
     *
     * @param restorePersistedValue
     * @param defaultValue
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setCurrentValue(restorePersistedValue ? getPersistedInt(DEFAULT_COLOR) : (Integer) defaultValue);
    }

    @Override
    public CharSequence getSummary() {
        // 使用 16进制的颜色构造概要
        int color = getPersistedInt(DEFAULT_COLOR);
        String content = String.format("Current Value is 0x%02X%02X%02X",
                Color.red(color), Color.green(color), Color.blue(color));
        // 将概要文本返回 Spannable，根据选择确定颜色
        Spannable summary = new SpannableString(content);
        summary.setSpan(new ForegroundColorSpan(color), 0, summary.length(), 0);
        return summary;
    }

    private void setCurrentValue(int value) {
        // 更新最新值
        mCurrentColor = value;

        // 保存新值
        persistInt(value);
        // 通知首选项侦听器
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

}
