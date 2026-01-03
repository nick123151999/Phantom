package com.wlqq.phantom.library.pm;

import java.util.Map;

/**
 * 插件安装异常： 宿主提供的公共库与插件依赖的希望宿主提供的公共库不匹配
 */

public class SharedLibraryDependenciesMismatchException extends Exception {
    public final Map<String, String> hostCompileDependencies;
    public final Map<String, String> pluginProvidedDependencies;

    public SharedLibraryDependenciesMismatchException(String message, Map<String, String> hostCompileDependencies,
                                                      Map<String, String> pluginProvidedDependencies) {
        super(message);
        this.hostCompileDependencies = hostCompileDependencies;
        this.pluginProvidedDependencies = pluginProvidedDependencies;
    }

    @SuppressWarnings({"PMD.ConsecutiveAppendsShouldReuse", "PMD.InsufficientStringBufferDeclaration",
            "PMD.ConsecutiveLiteralAppends"})
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SharedLibraryDependenciesMismatchException{");
        sb.append("message=").append(getLocalizedMessage());
        sb.append(", hostCompileDependencies=").append(hostCompileDependencies);
        sb.append(", pluginProvidedDependencies=").append(pluginProvidedDependencies);
        sb.append('}');
        return sb.toString();
    }
}
