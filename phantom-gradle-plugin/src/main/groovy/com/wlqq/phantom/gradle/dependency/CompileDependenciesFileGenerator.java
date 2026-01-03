

package com.wlqq.phantom.gradle.dependency;

import com.android.build.gradle.internal.api.ApplicationVariantImpl;
import com.wlqq.phantom.gradle.Constant;
import com.wlqq.phantom.gradle.utils.Log;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.artifacts.result.ResolvedComponentResult;

import java.io.File;
import java.util.HashSet;
import java.util.Set;


/**
 * AGP 8+ compatible version - uses only public Gradle APIs
 * Generates compile_dependencies.txt for host application
 */
public class CompileDependenciesFileGenerator extends FileGenerator {
    private static final String TAG = Constant.HOST_TAG;

    public CompileDependenciesFileGenerator(Project project, ApplicationVariantImpl variant, File outputFileDir,
            String outputFileName) {
        super(TAG, project, variant, outputFileDir, outputFileName);
        this.project = project;
        this.applicationVariant = variant;
    }

    @Override
    protected String getContent() {
        Set<String> dependencies = getCompileArtifacts();
        Log.i(TAG, "Found " + dependencies.size() + " compile dependencies");
        return String.join("\n", dependencies);
    }

    /**
     * Get compile artifacts using public Gradle API (AGP 8+ compatible)
     * Uses variant's compileClasspath configuration
     */
    private Set<String> getCompileArtifacts() {
        Set<String> compileLibs = new HashSet<>();

        try {
            // AGP 8+: Use variant's compile configuration
            // Try multiple configuration names for compatibility
            Configuration configuration = getCompileConfiguration();
            
            if (configuration != null && configuration.isCanBeResolved()) {
                Log.i(TAG, "Resolving dependencies from configuration: " + configuration.getName());
                
                // Get all resolved components
                Set<ResolvedComponentResult> components = 
                    configuration.getIncoming().getResolutionResult().getAllComponents();

                for (ResolvedComponentResult component : components) {
                    ModuleVersionIdentifier moduleVersion = component.getModuleVersion();
                    if (moduleVersion != null && !"unspecified".equals(moduleVersion.getVersion())) {
                        String dependency = String.join(":", 
                            moduleVersion.getGroup(), 
                            moduleVersion.getName(), 
                            moduleVersion.getVersion());
                        compileLibs.add(dependency);
                        Log.i(TAG, "  - " + dependency);
                    }
                }
            } else {
                Log.w(TAG, "Compile configuration not found or not resolvable");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting compile artifacts: " + e.getMessage());
            e.printStackTrace();
        }

        return compileLibs;
    }

    /**
     * Get the appropriate compile configuration for the variant
     * Tries multiple configuration names for compatibility across AGP versions
     */
    private Configuration getCompileConfiguration() {
        String variantName = applicationVariant.getName();
        
        // Try different configuration names in order of preference
        String[] configNames = {
            variantName + "CompileClasspath",           // AGP 3.0+
            variantName + "RuntimeClasspath",           // Fallback
            "implementation",                            // AGP 3.0+
            "compile",                                   // AGP 2.x (deprecated)
            "compileClasspath"                          // Generic
        };

        for (String configName : configNames) {
            try {
                Configuration config = project.getConfigurations().findByName(configName);
                if (config != null) {
                    Log.i(TAG, "Using configuration: " + configName);
                    return config;
                }
            } catch (Exception e) {
                // Continue to next configuration name
            }
        }

        Log.w(TAG, "No suitable compile configuration found for variant: " + variantName);
        return null;
    }
}
