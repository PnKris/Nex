package com.beiyan.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.yuupo.nex.annotation.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class NexProcessor extends AbstractProcessor {
    public String filePath;

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    Map<String, String> providerMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Component.class.getCanonicalName());
        return annotations;


    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            collectInfo(roundEnvironment);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeToFile();
        return true;
    }

    public void collectInfo(RoundEnvironment roundEnvironment) throws IOException {

        for (Element element : roundEnvironment.getElementsAnnotatedWith(Component.class)) {
            System.out.println("------------------------------");
            // 判断元素的类型为Class
            if (element.getKind() == ElementKind.CLASS) {
                // 显示转换元素类型
                TypeElement typeElement = (TypeElement) element;
                // 输出元素名称
                System.out.println(typeElement.getQualifiedName());
                // 输出注解属性值
                System.out.println(typeElement.getAnnotation(Component.class).path());
                String path = typeElement.getAnnotation(Component.class).path();

                if (!path.startsWith("/")) {
                    Logger.error(messager, "path 必须以/开头");
                    return;
                }

                String[] arrays = path.split("/");
                if (arrays.length == 0) {
                    Logger.error(messager, "path 设置错误");
                    return;
                }

                if (filePath == null || filePath.trim().isEmpty()) {
                    filePath = arrays[1] + "Provider";
                    Logger.info(messager, "生成文件名：" + filePath);
                }

                if (providerMap.containsKey(path)) {
                    Logger.error(messager, "path：" + path + "已经被设置, "
                            + "类名：" + typeElement.getQualifiedName().toString());
                    return;
                }
                providerMap.put(path, typeElement.getQualifiedName().toString());

            }
            System.out.println("------------------------------");
        }

        // 创建main方法
        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("initComponent")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

                .returns(void.class);

        for (Map.Entry<String, String> entry : providerMap.entrySet()) {
            //Nex.register(path, Service.build(path, com.beiyan.booklist.BookListActivity.class));
            methodSpecBuilder.addStatement("com.yuupo.nex.Nex.register(\"" + entry.getKey() + "\", Service.build(\"" + entry.getKey() + "\", " + entry.getValue() + ".class));");
        }
        MethodSpec initMethod = methodSpecBuilder.build();


        TypeSpec provider = TypeSpec.classBuilder(filePath)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get("com.yuupo.nex", "IProvider"))
                .addMethod(initMethod)
                .build();

        JavaFile.builder("com.yuupo.nex.provider",
                provider).build().writeTo(filer);

    }

    public void writeToFile() {

    }


}
