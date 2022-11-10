// Generated by view binder compiler. Do not edit!
package com.curry.toolt.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.curry.toolt.R;
import com.curry.util.view.FlowLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class HolderFuncCatalogBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final FlowLayout catalogFlow;

  @NonNull
  public final TextView catalogHint;

  @NonNull
  public final FrameLayout catalogHintContainer;

  @NonNull
  public final ImageView catalogIcon;

  @NonNull
  public final ImageView catalogRight;

  @NonNull
  public final TextView catalogTitle;

  @NonNull
  public final ConstraintLayout catalogUpper;

  private HolderFuncCatalogBinding(@NonNull LinearLayout rootView, @NonNull FlowLayout catalogFlow,
      @NonNull TextView catalogHint, @NonNull FrameLayout catalogHintContainer,
      @NonNull ImageView catalogIcon, @NonNull ImageView catalogRight,
      @NonNull TextView catalogTitle, @NonNull ConstraintLayout catalogUpper) {
    this.rootView = rootView;
    this.catalogFlow = catalogFlow;
    this.catalogHint = catalogHint;
    this.catalogHintContainer = catalogHintContainer;
    this.catalogIcon = catalogIcon;
    this.catalogRight = catalogRight;
    this.catalogTitle = catalogTitle;
    this.catalogUpper = catalogUpper;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static HolderFuncCatalogBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static HolderFuncCatalogBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.holder_func_catalog, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static HolderFuncCatalogBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.catalog_flow;
      FlowLayout catalogFlow = ViewBindings.findChildViewById(rootView, id);
      if (catalogFlow == null) {
        break missingId;
      }

      id = R.id.catalog_hint;
      TextView catalogHint = ViewBindings.findChildViewById(rootView, id);
      if (catalogHint == null) {
        break missingId;
      }

      id = R.id.catalog_hint_container;
      FrameLayout catalogHintContainer = ViewBindings.findChildViewById(rootView, id);
      if (catalogHintContainer == null) {
        break missingId;
      }

      id = R.id.catalog_icon;
      ImageView catalogIcon = ViewBindings.findChildViewById(rootView, id);
      if (catalogIcon == null) {
        break missingId;
      }

      id = R.id.catalog_right;
      ImageView catalogRight = ViewBindings.findChildViewById(rootView, id);
      if (catalogRight == null) {
        break missingId;
      }

      id = R.id.catalog_title;
      TextView catalogTitle = ViewBindings.findChildViewById(rootView, id);
      if (catalogTitle == null) {
        break missingId;
      }

      id = R.id.catalog_upper;
      ConstraintLayout catalogUpper = ViewBindings.findChildViewById(rootView, id);
      if (catalogUpper == null) {
        break missingId;
      }

      return new HolderFuncCatalogBinding((LinearLayout) rootView, catalogFlow, catalogHint,
          catalogHintContainer, catalogIcon, catalogRight, catalogTitle, catalogUpper);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
