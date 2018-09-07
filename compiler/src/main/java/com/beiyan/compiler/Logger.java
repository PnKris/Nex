package com.beiyan.compiler;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class Logger {

    public static void info(Messager messager, String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    public static void warn(Messager messager, String msg) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    public static void error(Messager messager, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }
}
