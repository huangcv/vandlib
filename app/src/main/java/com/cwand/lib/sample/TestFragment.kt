package com.cwand.lib.sample

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import com.cwand.lib.ktx.extensions.mediaProjectionManager
import com.cwand.lib.ktx.extensions.onClick
import com.cwand.lib.ktx.services.GlobalWindowService
import com.cwand.lib.ktx.ui.BaseTitleFragment
import kotlinx.android.synthetic.main.fragment_test.*


class TestFragment : BaseTitleFragment() {

    private val REQUEST_MEDIA_PROJECTION = 1

    companion object {
        fun newInstance(): TestFragment {
            val args = Bundle()
            val fragment = TestFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun isShowToolbar(): Boolean {
        return false
    }

    override fun titleTextRes(): Int {
        return R.string.app_name
    }

    override fun bindLayout(): Int {
        return R.layout.fragment_test
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initViews(savedInstanceState: Bundle?, contentView: View) {
//        lightning.onClick {
//            if (!lightning.isRunning) {
//                lightning.startRotate(3000)
//            } else {
//                lightning.stopRotate()
//            }
//        }
        btn_screen_capture_service?.onClick {
            context?.let {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(it)) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                        intent.data = Uri.parse("package:" + it.packageName)
                        startActivityForResult(intent, 0)
                    } else {
                        val intent = Intent(it, GlobalWindowService::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        it.startService(intent)
                    }
                }
//                val intent = Intent(it, GlobalWindowService::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                it.startService(intent)
            }
        }
        btn_screen_capture?.onClick {
            context?.let {
                startActivityForResult(it.mediaProjectionManager?.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION)
            }
        }
    }

    override fun onMenuClicked(menu: MenuItem, menuId: Int, title: CharSequence) {
        startActivity(Intent(requireContext(), Test3::class.java))
    }

    override fun initListeners() {
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun lazyInit() {
//        toast("懒加载数据")
//        context?.let {
//            startActivityForResult(it.mediaProjectionManager?.createScreenCaptureIntent(),
//                REQUEST_MEDIA_PROJECTION)
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                return
            } else if (data != null && resultCode != 0) {
                //Service1.mResultCode = resultCode;
                //Service1.mResultData = data;
                GlobalWindowService.resultCode = resultCode
                GlobalWindowService.intentData = data
                context?.let {
                    val intent = Intent(it, GlobalWindowService::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    it.startService(intent)
                }
            }
        }
    }
}