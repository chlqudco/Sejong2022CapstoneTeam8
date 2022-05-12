/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chlqudco.develop.sejong2022capstoneteam8.Mlkit

import androidx.appcompat.app.AppCompatActivity
import android.preference.PreferenceFragment
import com.chlqudco.develop.sejong2022capstoneteam8.R
import android.os.Bundle
import com.chlqudco.develop.sejong2022capstoneteam8.Mlkit.SettingsActivity.LaunchSource
import java.lang.Exception
import java.lang.RuntimeException

/**
 * Hosts the preference fragment to configure settings for a demo activity that specified by the
 * [LaunchSource].
 */
class SettingsActivity : AppCompatActivity() {
    /** Specifies where this activity is launched from.  */
    // CameraX is only available on API 21+
    enum class LaunchSource(
        val titleResId: Int,
        val prefFragmentClass: Class<out PreferenceFragment?>
    ) {
        CAMERAX_LIVE_PREVIEW(
            R.string.pref_screen_title_camerax_live_preview,
            CameraXLivePreviewPreferenceFragment::class.java
        ),
        CAMERAXSOURCE_DEMO(
            R.string.pref_screen_title_cameraxsource_demo,
            CameraXSourceDemoPreferenceFragment::class.java
        );
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val launchSource = intent.getSerializableExtra(EXTRA_LAUNCH_SOURCE) as LaunchSource?
        val actionBar = supportActionBar
        actionBar?.setTitle(launchSource!!.titleResId)
        try {
            fragmentManager
                .beginTransaction()
                .replace(
                    R.id.settings_container,
                    launchSource!!.prefFragmentClass.getDeclaredConstructor().newInstance()
                )
                .commit()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object {
        const val EXTRA_LAUNCH_SOURCE = "extra_launch_source"
    }
}