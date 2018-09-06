package com.yuupo.nex.plugin

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project;

/**
 * 插桩实现类
 */
public class MyInjects {
    private final static ClassPool pool = ClassPool.getDefault();

    /**
     * 插桩具体实现
     * @param path
     * @param project
     */
    public static void inject(String path, Project project) {
        //添加当前路径到classPool中
        pool.appendClassPath(path);
        //project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        pool.appendClassPath(project.android.bootClasspath[0].toString())
        //引入android.os.Bundle包，因为onCreate方法参数有Bundle
        pool.importPackage("android.os.Bundle")

        File dir = new File(path);
        if(dir.isDirectory()) {
            dir.eachFileRecurse {
                File file ->
                    String filePath = file.absolutePath
                    println("filePath = " + filePath)
                    if (file.getName().equals("MainActivity.class")) {
                        CtClass ctClass = pool.getCtClass("com.yuupo.nex.MainActivity")
                        println("ctClass = " + ctClass)

                        if(ctClass.isFrozen()) {
                            ctClass.defrost()
                        }

                        CtMethod ctMethod = ctClass.getDeclaredMethod("initComponents")

                        println("方法名 = " + ctMethod)

                        String insetBeforeStr =
                                """
                                android.widget.Toast.makeText(this, "inject code success",android.widget.Toast.LENGTH_SHORT).show(); 
                                """
                        ctMethod.insertBefore(insetBeforeStr)
                        ctClass.writeFile(path)
                        ctClass.detach()
                    }

            }
        }

    }

}
