/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import mozilla.components.support.ktx.android.content.res.resolveAttribute
import org.mozilla.fenix.R
import org.mozilla.fenix.components.toolbar.ToolbarPosition
import org.mozilla.fenix.databinding.FragmentHomeBinding
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.home.toolbar.ToolbarInteractor
import org.mozilla.fenix.utils.ToolbarPopupWindow
import java.lang.ref.WeakReference
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.PopupMenu

/**
 * View class for setting up the home screen toolbar.
 */
class ToolbarView(
    private val binding: FragmentHomeBinding,
    private val context: Context,
    private val interactor: ToolbarInteractor,
) {
    init {
        updateLayout(binding.root)
    }

    /**
     * Setups the home screen toolbar.
     */
    fun build() {
        binding.toolbar.compoundDrawablePadding =
            context.resources.getDimensionPixelSize(R.dimen.search_bar_search_engine_icon_padding)

        binding.toolbarWrapper.setOnClickListener {
            interactor.onNavigateSearch()
        }

        binding.toolbarWrapper.setOnLongClickListener {
            ToolbarPopupWindow.show(
                WeakReference(it),
                handlePasteAndGo = interactor::onPasteAndGo,
                handlePaste = interactor::onPaste,
                copyVisible = false,
            )
            true
        }

        // Setting up menuButton2 to show a popup menu with the two options
        val menuButton2: ImageView = binding.root.findViewById(R.id.menuButton2)
        menuButton2.setOnClickListener {
            showPopupMenu(it)
        }
    }
    fun showPopupMenu(view: View) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.login_list, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.userguide2 -> {
                    val googleUrl = "https://lagomphone.com/pages/user-guide"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleUrl))
                    view.context.startActivity(intent)
                    true
                }
                R.id.contact2 -> {
                    val youtubeUrl = "https://lagomphone.com/pages/contact"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
                    view.context.startActivity(intent)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun updateLayout(view: View) {
        when (context.settings().toolbarPosition) {
            ToolbarPosition.TOP -> {
                binding.toolbarLayout.layoutParams = CoordinatorLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ).apply {
                    gravity = Gravity.TOP
                }

                ConstraintSet().apply {
                    clone(binding.toolbarLayout)
                    clear(binding.bottomBar.id, ConstraintSet.BOTTOM)
                    clear(binding.bottomBarShadow.id, ConstraintSet.BOTTOM)
                    connect(
                        binding.bottomBar.id,
                        ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.TOP,
                    )
                    connect(
                        binding.bottomBarShadow.id,
                        ConstraintSet.TOP,
                        binding.bottomBar.id,
                        ConstraintSet.BOTTOM,
                    )
                    connect(
                        binding.bottomBarShadow.id,
                        ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.BOTTOM,
                    )
                    applyTo(binding.toolbarLayout)
                }

                binding.bottomBar.background = AppCompatResources.getDrawable(
                    view.context,
                    view.context.theme.resolveAttribute(R.attr.bottomBarBackgroundTop),
                )

                binding.homeAppBar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin =
                        context.resources.getDimensionPixelSize(R.dimen.home_fragment_top_toolbar_header_margin)
                }
            }

            ToolbarPosition.BOTTOM -> {}
        }
    }
}
