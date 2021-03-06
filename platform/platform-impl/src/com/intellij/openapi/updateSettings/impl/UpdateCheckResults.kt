// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.openapi.updateSettings.impl

import com.intellij.ide.externalComponents.ExternalComponentSource
import com.intellij.ide.externalComponents.UpdatableExternalComponent
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.openapi.util.BuildNumber

class UpdateChain internal constructor(val chain: List<BuildNumber>, val size: String?)

class CheckForUpdateResult {
  val state: UpdateStrategy.State
  val newBuild: BuildInfo?
  val updatedChannel: UpdateChannel?
  val patches: UpdateChain?
  val error: Exception?

  internal constructor(newBuild: BuildInfo?, updatedChannel: UpdateChannel?, patches: UpdateChain?) {
    this.state = UpdateStrategy.State.LOADED
    this.newBuild = newBuild
    this.updatedChannel = updatedChannel
    this.patches = patches
    this.error = null
  }

  internal constructor(state: UpdateStrategy.State, error: Exception?) {
    this.state = state
    this.newBuild = null
    this.updatedChannel = null
    this.patches = null
    this.error = error
  }
}

/**
 * [enabled] - new versions of enabled plugins compatible with the specified build
 *
 * [disabled] - new versions of disabled plugins compatible with the specified build
 *
 * [incompatible] - plugins that would become incompatible and don't have updates compatible with the specified build
 */
data class PluginUpdates(
  val enabled: Collection<PluginDownloader>,
  val disabled: Collection<PluginDownloader>,
  val incompatible: Collection<IdeaPluginDescriptor>,
)

data class ExternalUpdate(
  val source: ExternalComponentSource,
  val components: Collection<UpdatableExternalComponent>
)
