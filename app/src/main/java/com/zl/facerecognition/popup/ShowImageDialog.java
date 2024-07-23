package com.zl.facerecognition.popup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zl.facerecognition.R;
import com.zl.facerecognition.utils.ViewUtils;

import androidx.constraintlayout.widget.ConstraintLayout;


public class ShowImageDialog extends Dialog {

    private ConstraintLayout imageLayout;
    private ImageView image;



    public ShowImageDialog(Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_show_image, null);

        setViewHeightByWidth();
    }

    public void onClicked(){
        dismiss();
    }

    public void setImage(String path){
        Picasso.with(getContext())
                .load(path)
                .fit()
                .error(R.drawable.ic_net_error)
                .into(image);
    }

    public void setViewHeightByWidth() {
        ViewTreeObserver vto = image.getViewTreeObserver();
        ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {

                int width = image.getMeasuredWidth();

                android.view.ViewGroup.LayoutParams lp = image.getLayoutParams();
                lp.height = width;
                image.setLayoutParams(lp);

                final ViewTreeObserver vto1 = image.getViewTreeObserver();
                vto1.removeOnPreDrawListener(this);

                return true;
            }
        };
        vto.addOnPreDrawListener(preDrawListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLayout = (ConstraintLayout) findViewById(R.id.image_layout);
        image = (ImageView) findViewById(R.id.image);
    }

    @Override
    public void show() {
        super.show();
        ViewUtils.show(getWindow());
    }
}

