package app;

import a.api.PublicApi;
import b.api.OtherApi;

public class Main {
    public static void main(String[] args) throws Exception {
        // public api -> ok
        PublicApi.hello();
        OtherApi.ping();

        // resource -> no way to encapsulate without hacks
        try (final var res = Thread.currentThread().getContextClassLoader().getResourceAsStream("secret.txt")) {
            System.out.println("resource not encapsulated: " + new String(res.readAllBytes()));
        }

        // resource -> no way to encapsulate without hacks
        // == you can load all encapsulated code from any module
        try (final var res = Thread.currentThread().getContextClassLoader().getResourceAsStream("a/internal/Secret.class")) {
            System.out.println("class bytecode not encapsulated: " + res.readAllBytes().length);
        }
    }
}