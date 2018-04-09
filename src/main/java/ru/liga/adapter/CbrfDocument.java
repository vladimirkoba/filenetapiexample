package ru.liga.adapter;

import java.io.InputStream;


/**
 * Класс для API FnAdapter. Представляет документ в следующем виде
 */
public class CbrfDocument {
    private final String name;
    private final long size;
    private final InputStream byteStream;

    /**
     * @param name       имя документа с расширением
     * @param size       размер документа (при загрузке документа можно опустить)
     * @param byteStream поток на чтение документа
     */
    public CbrfDocument(String name, long size, InputStream byteStream) {
        this.name = name;
        this.size = size;
        this.byteStream = byteStream;
    }

    public CbrfDocument(String name, InputStream byteStream) {
        this.name = name;
        this.size = -1;
        this.byteStream = byteStream;
    }

    public String name() {
        return name;
    }

    public long size() {
        return size;
    }

    public InputStream byteStream() {
        return byteStream;
    }
}
