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

package com.wlqq.phantom.gradle.dependency

import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.wlqq.phantom.gradle.Constant
import com.wlqq.phantom.gradle.PhantomPluginConfig
import com.wlqq.phantom.gradle.utils.Log
import org.gradle.api.Project
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult

/**
 * AGP 8+ compatible version - uses only public Gradle APIs
 * Generates provided_dependencies_v2.txt for plugin application
 */
class ProvidedDependenciesFileGenerator extends FileGenerator {
    private static final def COMPILE_ONLY_FILTER = { String gav ->
        !gav.startsWith('com.wlqq.phantom:phantom-plugin-lib:') &&
                !gav.startsWith('com.android.tools.build:gradle:') &&
                !gav.startsWith('com.google.android:android:') &&
                !gav.startsWith('com.android.support:support-annotations:')
    }

    ProvidedDependenciesFileGenerator(Project project, ApplicationVariantImpl variant, File outputFileDir, String outputFileName) {
        super(Constant.PLUGIN_TAG, project, variant, outputFileDir, outputFileName)
    }

    @Override
    protected String getContent() {
        return resolveProvidedDependencies().join('\n')
    }


    private Set<String> findDependencies(String type) {
        Set<String> dependencies = []
        project.rootProject.allprojects.each {
            try {
                it.configurations.getByName(type)
            } catch (UnknownConfigurationException e) {
                return
            }

            Iterator<Dependency> iterator = it.configurations.getByName(type).dependencies.iterator()
            while (iterator.hasNext()) {
                Dependency dependency = iterator.next()
                if (null != dependency.group && "unspecified" != dependency.version) {
                    dependencies.add(String.join(":", dependency.group, dependency.name, dependency.version))
                }
            }
        }

        return dependencies
    }

    private Set<String> resolveProvidedDependencies() {

        Set<String> providedLibs = findDependencies('compileOnly').findAll(COMPILE_ONLY_FILTER)
        providedLibs.addAll(findDependencies('provided').findAll(COMPILE_ONLY_FILTER))

        def excludeLibs = [] as Set<String>

        PhantomPluginConfig config = project.extensions.findByName(Constant.USER_CONFIG)
        if (config != null) {
            Set<String> compileLibs = getCompileArtifacts()
            // 若 compile/api/implementation 配置和 provided/compileOnly 配置重复，则以 compile/api/implementation 为准
            providedLibs.removeAll(compileLibs)

            for (PhantomPluginConfig.ExcludeConfig excludeLib : config.excludeLibs) {
                for (String compileLib : compileLibs) {
                    if (compileLib == excludeLib.name) {
                        excludeLibs.add(String.join(":", excludeLib.groupId, excludeLib.artifactId, excludeLib.versionRequirement))
                        // 若 excludeLib 配置与 provided/compileOnly 配置重复，则以 excludeLib 为准确
                        providedLibs.remove(excludeLib.name)
                    }
                }
            }
        }

        providedLibs.addAll(excludeLibs)

        return providedLibs
    }

    /**
     * Get compile artifacts using public Gradle API (AGP 8+ compatible)
     * Uses variant's compileClasspath configuration
     */
    private Set<String> getCompileArtifacts() {
        Set<String> compileLibs = new HashSet<>()

        try {
            // Get the appropriate compile configuration
            Configuration configuration = getCompileConfiguration()
            
            if (configuration != null && configuration.isCanBeResolved()) {
                Log.i(tag, "Resolving dependencies from configuration: " + configuration.name)
                
                // Get all resolved components
                Set<ResolvedComponentResult> components = 
                    configuration.incoming.resolutionResult.allComponents

                for (ResolvedComponentResult component : components) {
                    def moduleVersion = component.moduleVersion
                    if (moduleVersion != null && moduleVersion.version != "unspecified") {
                        String dependency = String.join(":", 
                            moduleVersion.group, 
                            moduleVersion.name, 
                            moduleVersion.version)
                        compileLibs.add(dependency)
                    }
                }
            } else {
                Log.w(tag, "Compile configuration not found or not resolvable")
            }
        } catch (Exception e) {
            Log.e(tag, "Error getting compile artifacts: " + e.message)
            e.printStackTrace()
        }

        return compileLibs
    }

    /**
     * Get the appropriate compile configuration for the variant
     * Tries multiple configuration names for compatibility across AGP versions
     */
    private Configuration getCompileConfiguration() {
        String variantName = applicationVariant.name
        
        // Try different configuration names in order of preference
        String[] configNames = [
            "${variantName}CompileClasspath",           // AGP 3.0+
            "${variantName}RuntimeClasspath",           // Fallback
            "implementation",                            // AGP 3.0+
            "compile",                                   // AGP 2.x (deprecated)
            "compileClasspath"                          // Generic
        ]

        for (String configName : configNames) {
            try {
                Configuration config = project.configurations.findByName(configName)
                if (config != null) {
                    Log.i(tag, "Using configuration: " + configName)
                    return config
                }
            } catch (Exception e) {
                // Continue to next configuration name
            }
        }

        Log.w(tag, "No suitable compile configuration found for variant: " + variantName)
        return null
    }
}
