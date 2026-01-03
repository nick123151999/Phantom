package com.wlqq.phantom.library.proxy;

import android.app.Activity;

/**
 * 插件 Activity 的基类（编译时存根类）
 * 
 * <p>
 * 这是一个编译时存根类，用于让插件在编译时可以继承。
 * 实际的实现在 phantom-host-lib 中，运行时会使用真实的实现类。
 * </p>
 * 
 * <p>
 * <b>重要说明：</b>
 * 由于这是一个纯 Java 库中的存根类，它继承自 {@link Activity}。
 * 但在运行时，phantom-host-lib 中的实现继承自 {@code androidx.fragment.app.FragmentActivity}。
 * 因此，插件可以在运行时使用 Fragment 相关的功能。
 * </p>
 * 
 * <p>
 * <b>使用 Fragment 的方法：</b>
 * 如果你的插件需要使用 Fragment，请确保：
 * <ol>
 *   <li>在插件的 build.gradle 中添加：{@code implementation 'androidx.fragment:fragment:1.8.5'}</li>
 *   <li>在代码中，你可以安全地调用 {@code getSupportFragmentManager()}，
 *       尽管编译时这个方法不可见，但运行时会正常工作</li>
 * </ol>
 * </p>
 * 
 * <p>
 * <b>编译时解决方案：</b>
 * 如果编译器提示找不到 {@code getSupportFragmentManager()} 方法，
 * 可以使用以下方式之一：
 * <pre><code>
 * // 方式1：使用反射（推荐）
 * try {
 *     FragmentManager fm = (FragmentManager) getClass()
 *         .getMethod("getSupportFragmentManager")
 *         .invoke(this);
 * } catch (Exception e) {
 *     e.printStackTrace();
 * }
 * 
 * // 方式2：在 phantom-host-lib 中添加辅助方法
 * </code></pre>
 * </p>
 */
public class PluginInterceptActivity extends Activity {
    // 这是一个存根类，实际实现在 phantom-host-lib 中
    // 插件在编译时使用这个类，在运行时会被替换为 phantom-host-lib 中的实现
}

