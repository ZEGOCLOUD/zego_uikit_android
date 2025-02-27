package com.zegocloud.uikit.components.message;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.zegocloud.uikit.R;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitTranslationText;
import com.zegocloud.uikit.utils.KeyboardUtils;
import com.zegocloud.uikit.utils.Utils;

public class ZegoInRoomMessageInput extends FrameLayout {

    private ZegoInRoomMessageInputSubmitListener submitListener;
    private String placeHolder;
    private Drawable iconSend;
    private ViewGroup viewGroup;
    private boolean showKeyboardWhenShow = false;

    public ZegoInRoomMessageInput(@NonNull Context context) {
        super(context);
        initView();
    }

    public ZegoInRoomMessageInput(@NonNull Context context, boolean showKeyboardWhenShow) {
        super(context);
        this.showKeyboardWhenShow = showKeyboardWhenShow;
        initView();
    }


    public ZegoInRoomMessageInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ZegoInRoomMessageInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        viewGroup = (ViewGroup) LayoutInflater.from(getContext())
            .inflate(R.layout.zego_uikit_layout_message_input, this, true);
        EditText editText = viewGroup.findViewById(R.id.edit_message_input);
        if (showKeyboardWhenShow) {
            KeyboardUtils.requestInputWindow(editText);
        }
        UIKitTranslationText translationText = UIKitCore.getInstance().getTranslationText();
        if (translationText != null) {
            setPlaceHolder(translationText.UIKit_MessageInput_Hint);
        }
        ImageView sendBtn = viewGroup.findViewById(R.id.send_message);
        sendBtn.setEnabled(false);

        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{android.R.attr.state_enabled},
            ContextCompat.getDrawable(getContext(), R.drawable.zego_uikit_icon_send_normal));
        sld.addState(new int[]{}, ContextCompat.getDrawable(getContext(), R.drawable.zego_uikit_icon_send_disable));
        sendBtn.setImageDrawable(sld);

        sendBtn.setOnClickListener(v -> {
            String message = editText.getText().toString();
            ZegoUIKit.sendInRoomMessage(message);
            if (submitListener != null) {
                submitListener.onSubmit(message);
            }
            editText.setText("");
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendBtn.setEnabled(editText.getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (!TextUtils.isEmpty(placeHolder)) {
            editText.setHint(placeHolder);
        }

        int corner = Utils.dp2px(8f, getContext().getResources().getDisplayMetrics());
        float[] outerR = new float[]{corner, corner, corner, corner, corner, corner, corner, corner};
        RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(Color.parseColor("#1AFFFFFF"));
        editText.setBackground(shapeDrawable);
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
        if (viewGroup != null) {
            EditText editText = viewGroup.findViewById(R.id.edit_message_input);
            editText.setHint(placeHolder);
        }
    }

    public Drawable getIconSend() {
        return iconSend;
    }

    public void setIconSend(Drawable iconSend) {
        this.iconSend = iconSend;
        if (viewGroup != null) {
            ImageView sendBtn = viewGroup.findViewById(R.id.send_message);
            sendBtn.setImageDrawable(iconSend);
        }
    }

    public void setSubmitListener(ZegoInRoomMessageInputSubmitListener listener) {
        this.submitListener = listener;
    }

}
