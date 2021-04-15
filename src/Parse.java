import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public interface Parse<T> {
    public T parse(List<String> obj);
    public T insert(T obj);
    default void parseList(List<List<String>> objects)
    {
        Function<List<String>,T> parse = this::parse;
        Consumer<T> insert = this::insert;
        objects.stream().map(parse).forEach(insert);
    }
}
