/*
 * Copyright (C) 2017-2019 Manbang Group
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

package com.wlqq.phantom.gradle

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.wlqq.phantom.gradle.debugger.PhantomDebugger
import com.wlqq.phantom.gradle.dependency.ComparableVersion
import com.wlqq.phantom.gradle.dependency.ProvidedDependenciesFileGenerator
import com.wlqq.phantom.gradle.utils.Log
import com.wlqq.phantom.gradle.utils.VersionUtils
import org.gradle.api.Plugin
import org.gradle.api.Project


class PhantomPluginPlugin implements Plugin<Project> {
    def static TAG = Constant.PLUGIN_TAG

    @Override
    void apply(Project project) {
        Log.i(TAG, "apply plugin")

        if (project.plugins.hasPlugin(AppPlugin)) {
            def extensions = project.extensions

            extensions.create(Constant.USER_CONFIG, PhantomPluginConfig)

            PhantomPluginConfig config = extensions.getByName(Constant.USER_CONFIG)

            def android = extensions.getByType(AppExtension)

            def version = new ComparableVersion(VersionUtils.getAgpVersion())
            Log.i(TAG, "Your Gradle Android Plugin version is: ${version}")
            project.extensions.extraProperties[Constant.AGP_VERSION] = version

            android.applicationVariants.all { variant ->
                // AGP 8+ compatible: use variant name directly
                def variantName = variant.name.capitalize()

                if (config.genProvidedDeps) {
                    // provided_dependencies_v2.txt generate task
                    def generateProvidedDependenciesTaskName = "generate${variantName}ProvidedDependencies"
                    def generateProvidedDependenciesTask = project.task(generateProvidedDependenciesTaskName)
                    generateProvidedDependenciesTask.group = Constant.TASKS_GROUP

                    // depends on mergeAssets Task - AGP 8+ compatible
                    def mergeAssetsTaskName = "merge${variantName}Assets"
                    def mergeAssetsTask = project.tasks.findByName(mergeAssetsTaskName)
                    
                    if (mergeAssetsTask) {
                        generateProvidedDependenciesTask.doLast {
                            // AGP 8+: use variant.mergeAssetsProvider.get().outputDir
                            def outputDir = variant.mergeAssetsProvider.get().outputDir.get().asFile
                            new ProvidedDependenciesFileGenerator(project, variant, outputDir, 'provided_dependencies_v2.txt').generateFile()
                        }

                        generateProvidedDependenciesTask.dependsOn mergeAssetsTask
                        mergeAssetsTask.finalizedBy generateProvidedDependenciesTask
                    } else {
                        Log.w(TAG, "mergeAssetsTask not found for variant: ${variant.name}")
                    }
                }

                PhantomDebugger pluginDebugger = new PhantomDebugger(project, config, variant)

                def assembleTask = variant.assembleProvider.get()

                // AGP 8+ compatible task naming
                def installPluginTaskName = "phInstallPlugin${variantName}"
                def installPluginTask = project.task(installPluginTaskName)

                installPluginTask.doLast {
                    pluginDebugger.init()

                    pluginDebugger.checkConfig()

                    pluginDebugger.checkHostRunning()

                    pluginDebugger.install()
                    // 等待宿主异步安装插件完成
                    Thread.sleep(5000)

                    pluginDebugger.forceStopHostApp()

                    Thread.sleep(1000)

                    pluginDebugger.startHostApp()
                }
                installPluginTask.group = Constant.TASKS_GROUP

                if (assembleTask) {
                    installPluginTask.dependsOn assembleTask
                }
            }

            // AGP 8+: Transform API has been removed
            // ExcludeClassesTransform and ReplaceSuperTransform cannot be used
            // 
            // Alternative solutions:
            // 1. Manual ProGuard rules for excluding classes
            // 2. Use Artifact API for class transformation (requires significant refactoring)
            // 3. Use bytecode manipulation at build time with custom tasks
            //
            // For now, these features are disabled. Users should:
            // - Use 'compileOnly' for dependencies provided by host
            // - Manually configure ProGuard rules to exclude common libraries
            // - Ensure plugin classes extend correct base classes
            
            Log.i(TAG, "=".repeat(80))
            Log.i(TAG, "NOTICE: Transform API features are disabled in AGP 8+")
            Log.i(TAG, "  - ExcludeClassesTransform: Use 'compileOnly' + ProGuard rules instead")
            Log.i(TAG, "  - ReplaceSuperTransform: Ensure plugin classes extend correct base classes")
            Log.i(TAG, "  - See documentation for manual configuration guide")
            Log.i(TAG, "=".repeat(80))
        }
    }

}

