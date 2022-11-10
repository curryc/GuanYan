// Generated by view binder compiler. Do not edit!
package com.curry.toolt.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import androidx.viewpager2.widget.ViewPager2;
import com.curry.toolt.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final DrawerLayout rootView;

  @NonNull
  public final DrawerLayout drawer;

  @NonNull
  public final NavigationView drawerNav;

  @NonNull
  public final CardView mainNavContainer;

  @NonNull
  public final AppBarLayout mainScroll;

  @NonNull
  public final Toolbar titleToolbar;

  @NonNull
  public final CollapsingToolbarLayout toolbarLayout;

  @NonNull
  public final ViewPager2 viewPager;

  private ActivityMainBinding(@NonNull DrawerLayout rootView, @NonNull DrawerLayout drawer,
      @NonNull NavigationView drawerNav, @NonNull CardView mainNavContainer,
      @NonNull AppBarLayout mainScroll, @NonNull Toolbar titleToolbar,
      @NonNull CollapsingToolbarLayout toolbarLayout, @NonNull ViewPager2 viewPager) {
    this.rootView = rootView;
    this.drawer = drawer;
    this.drawerNav = drawerNav;
    this.mainNavContainer = mainNavContainer;
    this.mainScroll = mainScroll;
    this.titleToolbar = titleToolbar;
    this.toolbarLayout = toolbarLayout;
    this.viewPager = viewPager;
  }

  @Override
  @NonNull
  public DrawerLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      DrawerLayout drawer = (DrawerLayout) rootView;

      id = R.id.drawer_nav;
      NavigationView drawerNav = ViewBindings.findChildViewById(rootView, id);
      if (drawerNav == null) {
        break missingId;
      }

      id = R.id.main_nav_container;
      CardView mainNavContainer = ViewBindings.findChildViewById(rootView, id);
      if (mainNavContainer == null) {
        break missingId;
      }

      id = R.id.main_scroll;
      AppBarLayout mainScroll = ViewBindings.findChildViewById(rootView, id);
      if (mainScroll == null) {
        break missingId;
      }

      id = R.id.title_toolbar;
      Toolbar titleToolbar = ViewBindings.findChildViewById(rootView, id);
      if (titleToolbar == null) {
        break missingId;
      }

      id = R.id.toolbar_layout;
      CollapsingToolbarLayout toolbarLayout = ViewBindings.findChildViewById(rootView, id);
      if (toolbarLayout == null) {
        break missingId;
      }

      id = R.id.view_pager;
      ViewPager2 viewPager = ViewBindings.findChildViewById(rootView, id);
      if (viewPager == null) {
        break missingId;
      }

      return new ActivityMainBinding((DrawerLayout) rootView, drawer, drawerNav, mainNavContainer,
          mainScroll, titleToolbar, toolbarLayout, viewPager);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
