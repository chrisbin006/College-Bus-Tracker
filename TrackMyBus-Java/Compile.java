import javax.tools.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/** Compiles the project without needing the javac command (works with any JDK). */
public class Compile {
    public static void main(String[] a) throws Exception {
        List<String> files;
        try (Stream<Path> s = Files.walk(Path.of("src"))) { files = s.filter(p -> p.toString().endsWith(".java")).map(Path::toString).collect(Collectors.toList()); }
        List<String> args = new ArrayList<>(List.of("-source", "17", "-target", "17", "-d", "out"));
        args.addAll(files);
        System.exit(ToolProvider.getSystemJavaCompiler().run(null, null, null, args.toArray(new String[0])));
    }
}
