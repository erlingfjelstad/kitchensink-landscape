package com.ciscowebex.androidsdk.kitchensink.setup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.ciscowebex.androidsdk.kitchensink.BaseActivity
import com.ciscowebex.androidsdk.kitchensink.R
import com.ciscowebex.androidsdk.kitchensink.WebexRepository
import com.ciscowebex.androidsdk.kitchensink.databinding.ActivitySetupBinding
import com.ciscowebex.androidsdk.phone.Phone

class SetupActivity: BaseActivity() {

    enum class CameraCap {
        Front,
        Back,
        Close
    }

    lateinit var binding: ActivitySetupBinding
    private var cameraCap: CameraCap = CameraCap.Close
    private lateinit var callCap: WebexRepository.CallCap
    private lateinit var streamMode: Phone.VideoStreamMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tag = "SetupActivity"

        DataBindingUtil.setContentView<ActivitySetupBinding>(this, R.layout.activity_setup)
                .also { binding = it }
                .apply {
                    cameraCap = getDefaultCamera()

                    callCap = webexViewModel.callCapability

                    when (callCap) {
                        WebexRepository.CallCap.Audio_Only -> {
                            audioCallOnly.isChecked = true
                        }
                        WebexRepository.CallCap.Audio_Video -> {
                            audioVideoCall.isChecked = true
                        }
                    }

                    streamMode = webexViewModel.streamMode

                    when (streamMode) {
                        Phone.VideoStreamMode.COMPOSITED -> {
                            composited.isChecked = true
                        }
                        Phone.VideoStreamMode.AUXILIARY -> {
                            multiStream.isChecked = true
                        }
                    }

                    callCapabilityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                        when (checkedId) {
                            R.id.audioCallOnly -> {
                                webexViewModel.callCapability = WebexRepository.CallCap.Audio_Only
                            }
                            R.id.audioVideoCall -> {
                                webexViewModel.callCapability = WebexRepository.CallCap.Audio_Video
                            }
                        }
                    }

                    enableBgStreamToggle.isChecked = webexViewModel.enableBgStreamtoggle

                    enableBgStreamToggle.setOnCheckedChangeListener { _, checked ->
                        webexViewModel.enableBgStreamtoggle = checked
                        webexViewModel.enableBackgroundStream(checked)
                    }

                    enableHWAccelToggle.isChecked = webexViewModel.enableHWAcceltoggle

                    enableHWAccelToggle.setOnCheckedChangeListener { _, checked ->
                        webexViewModel.enableHWAcceltoggle = checked
                        webexViewModel.setHardwareAccelerationEnabled(checked)
                    }

                    streamModeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                        when (checkedId) {
                            R.id.composited -> {
                                webexViewModel.streamMode = Phone.VideoStreamMode.COMPOSITED
                            }
                            R.id.multiStream -> {
                                webexViewModel.streamMode = Phone.VideoStreamMode.AUXILIARY
                            }
                        }

                        webexViewModel.setVideoStreamMode(webexViewModel.streamMode)
                    }

                    enableBgConnectionToggle.isChecked = webexViewModel.enableBgConnectiontoggle

                    enableBgConnectionToggle.setOnCheckedChangeListener { _, checked ->
                        webexViewModel.enableBgConnectiontoggle = checked
                        webexViewModel.enableBackgroundConnection(checked)
                    }

                    enablePhonePermissionToggle.isChecked = webexViewModel.enablePhoneStatePermission

                    enablePhonePermissionToggle.setOnCheckedChangeListener { _, checked ->
                        webexViewModel.enablePhoneStatePermission = checked
                        webexViewModel.enableAskingReadPhoneStatePermission(checked)
                    }

                    logLevelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }

                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                            webexViewModel.logFilter = resources.getStringArray(R.array.logFilterArray)[position]
                            webexViewModel.setLogLevel(webexViewModel.logFilter)
                            Log.d(tag, "selected logLevel ${webexViewModel.logFilter}")
                        }
                    }

                    logLevelSpinner.setSelection(resources.getStringArray(R.array.logFilterArray).indexOf(webexViewModel.logFilter))

                    switchConsoleLog.setOnCheckedChangeListener { _ , checked ->
                        webexViewModel.isConsoleLoggerEnabled = checked
                        webexViewModel.enableConsoleLogger(webexViewModel.isConsoleLoggerEnabled)
                        Log.d(tag, "enable console logger ${webexViewModel.isConsoleLoggerEnabled}")
                    }
                    switchConsoleLog.isChecked = webexViewModel.isConsoleLoggerEnabled

                    cameraOptions.setOnClickListener {
                        startActivity(Intent(this@SetupActivity, SetupCameraActivity::class.java))
                    }
                }
    }

    private fun getDefaultCamera(): CameraCap {
        if (cameraCap == CameraCap.Close) {
            return cameraCap
        }

        return if (webexViewModel.getDefaultFacingMode() == Phone.FacingMode.USER) {
            CameraCap.Front
        } else {
            CameraCap.Back
        }
    }
}