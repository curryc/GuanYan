// Generated by view binder compiler. Do not edit!
package com.curry.toolt.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.curry.toolt.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityTopToolbarBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final FrameLayout contentContainer;

  @NonNull
  public final Toolbar titleToolbar;

  private ActivityTopToolbarBinding(@NonNull CoordinatorLayout rootView,
      @NonNull FrameLayout contentContainer, @NonNull Toolbar titleToolbar) {
    this.rootView = rootView;
    this.contentContainer = contentContainer;
    this.titleToolbar = titleToolbar;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityTopToolbarBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityTopToolbarBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_top_toolbar, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityTopToolbarBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.content_container;
      FrameLayout contentContainer = ViewBindings.findChildViewById(rootView, id);
      if (contentContainer == null) {
        break missingId;
      }

      id = R.id.title_toolbar;
      Toolbar titleToolbar = ViewBindings.findChildViewById(rootView, id);
      if (titleToolbar == null) {
        break missingId;
      }

      return new ActivityTopToolbarBinding((CoordinatorLayout) rootView, contentContainer,
          titleToolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
