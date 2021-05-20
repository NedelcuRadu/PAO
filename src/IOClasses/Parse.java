package IOClasses;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public interface Parse<T> {
    public T parse(Map<String, String> obj);
    public T insert(T obj);
    default void parseList(List<Map<String, String>> objects)
    {
        Function<Map<String,String>,T> parse = this::parse;
        Consumer<T> insert = this::insert;
        objects.stream().map(parse).forEach(insert);
    }
}
