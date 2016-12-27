package com.illum.MafiaRising;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontTextView extends TextView {
    public CustomFontTextView(Context context) {
        super(context);
        CustomFontUtils.applyCustomFont(this, context, null);
    }

    public CustomFontTextView(Context context, AttributeSet attribs)
    {
        super(context, attribs);
        CustomFontUtils.applyCustomFont(this, context, attribs);
    }

    public CustomFontTextView(Context context, AttributeSet attribs, int defStyle)
    {
        super(context, attribs, defStyle);
        CustomFontUtils.applyCustomFont(this, context, attribs);
    }

    public String getFont() {
        Context context = this.getContext();

        TypedArray attribArray = context.obtainStyledAttributes(R.styleable.CustomFontTextView);

        String fontName = attribArray.getString(R.styleable.CustomFontTextView_font);

        attribArray.recycle();

        return fontName;
    }

    public void setFont(String fontName) {
        Context context = this.getContext();

        TypedArray attribArray = context.obtainStyledAttributes(R.styleable.CustomFontTextView);

        int textStyle = attribArray.getInt(R.styleable.CustomFontTextView_textStyle, Typeface.NORMAL);

        Typeface customFont = CustomFontUtils.selectTypeface(context, fontName, textStyle);
        this.setTypeface(customFont);

        attribArray.recycle();
    }
}
