package app;

import a.api.PublicApi;
import b.api.OtherApi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {
    public static void main(String[] args) throws Exception {
        // public api -> ok
        PublicApi.hello();
        OtherApi.ping();

        // resource -> no way to encapsulate without hacks
        try (final var res = Thread.currentThread().getContextClassLoader().getResourceAsStream("secret.txt")) {
            System.out.println("resource not encapsulated: " + loadRes(res));
        }

        // resource -> no way to encapsulate without hacks
        // == you can load all encapsulated code from any module
        try (final var res = Thread.currentThread().getContextClassLoader().getResourceAsStream("a/internal/Secret.class")) {
            System.out.println("class bytecode encapsulated: " + (res == null));
        }
        try (final var res = Thread.currentThread().getContextClassLoader().getResourceAsStream("a/internal/secret.txt")) {
            System.out.println("resource encapsulated in encapsulated package: " + loadRes(res));
        }

        final var aModule = ModuleLayer.boot()
                .findModule("lib.a")
                .orElseThrow();

        try (final var res = aModule.getResourceAsStream("a/internal/Secret.class")) {
            System.out.println("class bytecode encapsulated via module api: " + (res == null));
        }
        try (final var res = aModule.getResourceAsStream("a/internal/secret.txt")) {
            System.out.println("resource encapsulated via module api: " + loadRes(res));
        }

        // hacky but still modules are unencapsulated
        try (final var cl = new URLClassLoader(
                new URL[]{PublicApi.class.getProtectionDomain().getCodeSource().getLocation()},
                Thread.currentThread().getContextClassLoader());
             final var res = cl.getResourceAsStream("a/internal/secret.txt")) {
            System.out.println("resource well encapsulated: " + loadRes(res));
        }
    }

    private static String loadRes(final InputStream res) {
        try {
            return res == null ? "true" : ("false (" + new String(res.readAllBytes()).strip() + ")");
        } catch (IOException e) {
            return String.valueOf(res == null);
        }
    }
}