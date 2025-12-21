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

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.wlqq.phantom.gradle.dependency.ComparableVersion
import com.wlqq.phantom.gradle.dependency.CompileDependenciesFileGenerator
import com.wlqq.phantom.gradle.utils.Log
import com.wlqq.phantom.gradle.utils.VersionUtils
import org.gradle.api.Plugin
import org.gradle.api.Project


class PhantomHostPlugin implements Plugin<Project> {
    def static TAG = Constant.HOST_TAG

    @Override
    void apply(Project project) {
        Log.i(TAG, "apply plugin")

        if (project.plugins.hasPlugin(AppPlugin)) {
            def android = project.extensions.getByType(AppExtension)

            def version = new ComparableVersion(VersionUtils.getAgpVersion())
            Log.i(TAG, "Your Gradle Android Plugin version is: ${version}")
            project.extensions.extraProperties[Constant.AGP_VERSION] = version

            android.applicationVariants.all { variant ->
                // AGP 8+ compatible: use variant name directly instead of scope
                def variantName = variant.name.capitalize()

                // builtin_plugin_list.csv generate task
                def generateCompileDependenciesTaskName = "generate${variantName}CompileDependencies"
                def generateCompileDependenciesTask = project.task(generateCompileDependenciesTaskName)
                generateCompileDependenciesTask.group = Constant.TASKS_GROUP

                // depends on mergeAssets Task - AGP 8+ compatible
                def mergeAssetsTaskName = "merge${variantName}Assets"
                def mergeAssetsTask = project.tasks.findByName(mergeAssetsTaskName)
                
                if (mergeAssetsTask) {
                    generateCompileDependenciesTask.doLast {
                        // AGP 8+: use variant.mergeAssetsProvider.get().outputDir
                        def outputDir = variant.mergeAssetsProvider.get().outputDir.get().asFile
                        new CompileDependenciesFileGenerator(project, variant, outputDir, 'compile_dependencies.txt').generateFile()
                    }

                    generateCompileDependenciesTask.dependsOn mergeAssetsTask
                    mergeAssetsTask.finalizedBy generateCompileDependenciesTask
                } else {
                    Log.w(TAG, "mergeAssetsTask not found for variant: ${variant.name}")
                }
            }
        }
    }

}

