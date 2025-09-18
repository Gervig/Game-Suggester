package app.daos;

import java.util.List;

public interface IDAO<T,I>
{
    T create(T type);
    T read(I i);
    List<T> readAll();
    T update(T type);
    void delete(I i);
}