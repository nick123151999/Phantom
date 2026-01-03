package com.wlqq.phantom.library;

import com.wlqq.phantom.communication.PhantomService;

/**
 * 提供 Phantom Core version code 信息
 * <p>
 * 插件可在其 <code>AndroidManifest.xml</code> 中声明其需要宿主集成的 Phantom Core 最低版本
 * <pre>{@code
 * <!-- 该插件对宿主 Phantom Core 的最小版本要求是 3.0.0 -->
 * <meta-data
 *     android:name="phantom.service.import.PhantomVersionService"
 *     android:value="30000" />
 * }
 * </pre>
 */
@PhantomService(name = "PhantomVersionService", version = BuildConfig.VERSION_CODE)
public class PhantomVersionService {
}
