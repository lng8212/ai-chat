package com.longkd.chatai.ui.main.setting

import androidx.fragment.app.viewModels
import com.longkd.base_android.base.BaseFragment
import com.longkd.base_android.ktx.onClickAnim
import com.longkd.base_android.ktx.showToast
import com.longkd.base_android.utils.DialogUtils
import com.longkd.chatai.R
import com.longkd.chatai.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * @Author: longkd
 * @Since: 20:48 - 12/08/2023
 */

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>() {
    override val viewModel: SettingViewModel by viewModels()

    override val enableAnimationFragment: Boolean
        get() = true

    override fun initView() {
        binding.run {
            btnThirdFragment.setOnClickListener {
                viewModel.navigateToThirdFragment()
            }
            btnShowDialog.onClickAnim {
                com.longkd.chatai.ui.DialogTest.newInstance {
                    context?.showToast(getString(R.string.text_showed))
                }.show(childFragmentManager, null)
            }
            btnShowUtilDialog.onClickAnim {
                activity?.let {
                    DialogUtils.showCustomDialog(it) {
                        context?.showToast(getString(R.string.text_showed))
                    }
                }
            }
            btnShowBottomSheet.onClickAnim {
                activity?.let {
                    com.longkd.chatai.ui.BottomSheetTest.newInstance {
                        context?.showToast(getString(R.string.text_showed))
                    }.show(childFragmentManager, null)
                }
            }

        }
    }
}