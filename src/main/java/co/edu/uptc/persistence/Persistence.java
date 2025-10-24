package co.edu.uptc.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Persistence<T> {
    private final Gson gson;
    private final String filePath;
    private final Type type;

    /**
     * Constructor para persistencia de listas de objetos
     * @param filePath Ruta del archivo JSON
     * @param typeAdapterFactory RuntimeTypeAdapterFactory para manejar polimorfismo (puede ser null)
     */
    public Persistence(String filePath, RuntimeTypeAdapterFactory<?> typeAdapterFactory) {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        
        if (typeAdapterFactory != null) {
            builder.registerTypeAdapterFactory(typeAdapterFactory);
        }
        
        this.gson = builder.create();
        this.filePath = filePath;
        this.type = new TypeToken<List<T>>(){}.getType();
    }

    /**
     * Constructor simple sin TypeAdapter (para clases concretas)
     */
    public Persistence(String filePath) {
        this(filePath, null);
    }

    /**
     * Guarda una lista de objetos en el archivo JSON
     * @param objects Lista de objetos a guardar
     * @throws IOException Si hay error al escribir el archivo
     */
    public void saveList(List<T> objects) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(objects, writer);
        }
    }

    /**
     * Carga una lista de objetos desde el archivo JSON
     * @return Lista de objetos cargados o lista vacía si no existe
     * @throws IOException Si hay error al leer el archivo
     */
    public List<T> loadList() throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            return new ArrayList<>();
        }
        
        try (FileReader reader = new FileReader(filePath)) {
            List<T> list = gson.fromJson(reader, type);
            return list != null ? list : new ArrayList<>();
        }
    }

    /**
     * Elimina el archivo de persistencia
     * @return true si se eliminó exitosamente
     * @throws IOException Si hay error al eliminar el archivo
     */
    public boolean delete() throws IOException {
        return Files.deleteIfExists(Paths.get(filePath));
    }

    /**
     * Verifica si el archivo de persistencia existe
     * @return true si existe el archivo
     */
    public boolean exists() {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Clase interna para manejar polimorfismo con Gson
     * Basada en RuntimeTypeAdapterFactory de Gson extras
     */
    public static class RuntimeTypeAdapterFactory<T> implements com.google.gson.TypeAdapterFactory {
        private final Class<?> baseType;
        private final String typeFieldName;
        private final java.util.Map<String, Class<?>> labelToSubtype = new java.util.LinkedHashMap<>();
        private final java.util.Map<Class<?>, String> subtypeToLabel = new java.util.LinkedHashMap<>();

        private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName) {
            this.baseType = baseType;
            this.typeFieldName = typeFieldName;
        }

        public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
            return new RuntimeTypeAdapterFactory<>(baseType, typeFieldName);
        }

        public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType) {
            return new RuntimeTypeAdapterFactory<>(baseType, "type");
        }

        public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type, String label) {
            labelToSubtype.put(label, type);
            subtypeToLabel.put(type, label);
            return this;
        }

        public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type) {
            return registerSubtype(type, type.getSimpleName());
        }

        @Override
        public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
            if (!baseType.isAssignableFrom(type.getRawType())) {
                return null;
            }

            final TypeAdapter<com.google.gson.JsonElement> jsonElementAdapter = gson.getAdapter(com.google.gson.JsonElement.class);
            final java.util.Map<String, TypeAdapter<?>> labelToDelegate = new java.util.LinkedHashMap<>();
            final java.util.Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new java.util.LinkedHashMap<>();

            for (java.util.Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
                TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
                labelToDelegate.put(entry.getKey(), delegate);
                subtypeToDelegate.put(entry.getValue(), delegate);
            }

            return new TypeAdapter<R>() {
                @Override
                public R read(JsonReader in) throws IOException {
                    com.google.gson.JsonElement jsonElement = jsonElementAdapter.read(in);
                    com.google.gson.JsonElement labelJsonElement = jsonElement.getAsJsonObject().get(typeFieldName);
                    
                    if (labelJsonElement == null) {
                        throw new com.google.gson.JsonParseException("No se encontró el campo '" + typeFieldName + "'");
                    }

                    String label = labelJsonElement.getAsString();
                    @SuppressWarnings("unchecked")
                    TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
                    
                    if (delegate == null) {
                        throw new com.google.gson.JsonParseException("Tipo desconocido: " + label);
                    }

                    return delegate.fromJsonTree(jsonElement);
                }

                @Override
                public void write(JsonWriter out, R value) throws IOException {
                    Class<?> srcType = value.getClass();
                    String label = subtypeToLabel.get(srcType);
                    @SuppressWarnings("unchecked")
                    TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);

                    if (delegate == null) {
                        throw new com.google.gson.JsonParseException("No se puede serializar " + srcType.getName());
                    }

                    com.google.gson.JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
                    
                    if (jsonObject.has(typeFieldName)) {
                        throw new com.google.gson.JsonParseException("Ya existe el campo: " + typeFieldName);
                    }

                    com.google.gson.JsonObject clone = new com.google.gson.JsonObject();
                    clone.add(typeFieldName, new com.google.gson.JsonPrimitive(label));
                    
                    for (java.util.Map.Entry<String, com.google.gson.JsonElement> e : jsonObject.entrySet()) {
                        clone.add(e.getKey(), e.getValue());
                    }

                    jsonElementAdapter.write(out, clone);
                }
            };
        }
    }
}