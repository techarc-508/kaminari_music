package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.kaminarimusic.service.playback.EqualizerManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Kaminari Music", appName)
  }

  @Test
  fun `equalizer preset profiles apply correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val equalizerManager = EqualizerManager(context)

    // Test Jazz preset
    equalizerManager.applyPreset("Jazz")
    assertEquals("Jazz", equalizerManager.currentPreset.value)
    var bands = equalizerManager.bands.value
    assertTrue(bands.isNotEmpty())
    assertEquals(3.5f, bands[0].levelDb, 0.01f)

    // Test Rock preset
    equalizerManager.applyPreset("Rock")
    assertEquals("Rock", equalizerManager.currentPreset.value)
    bands = equalizerManager.bands.value
    assertEquals(5.0f, bands[0].levelDb, 0.01f)

    // Test Classical preset
    equalizerManager.applyPreset("Classical")
    assertEquals("Classical", equalizerManager.currentPreset.value)
    bands = equalizerManager.bands.value
    assertEquals(4.0f, bands[0].levelDb, 0.01f)

    // Test custom band level
    equalizerManager.setBandLevel(0, 2.5f)
    assertEquals("Custom", equalizerManager.currentPreset.value)
    assertEquals(2.5f, equalizerManager.bands.value[0].levelDb, 0.01f)
  }
}
