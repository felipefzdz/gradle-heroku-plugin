package ratpack.example.java;

import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public class MyApp {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(s -> s
                .serverConfig(c -> c.baseDir(BaseDir.find()))
                .handlers(chain -> chain
                        .path("version", ctx -> ctx.render("{\n" +
                                "  \"commitId\": \" 12345678\",\n" +
                                "  \"buildNumber\": \"666\"\n" +
                                "}"))
                )
        );
    }
}
