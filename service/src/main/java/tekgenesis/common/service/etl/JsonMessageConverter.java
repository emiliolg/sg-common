
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.etl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import tekgenesis.common.collections.Seq;
import tekgenesis.common.json.JsonMapping;
import tekgenesis.common.media.MediaType;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.collections.Colls.seq;

/**
 * Message converter for reading and writing Json objects.
 */
public class JsonMessageConverter extends AbstractMessageConverter<Object> {

    //~ Instance Fields ..............................................................................................................................

    private final ObjectMapper mapper;

    //~ Constructors .................................................................................................................................

    /** Create default {@link JsonMessageConverter}. */
    public JsonMessageConverter() {
        this(JsonMapping.shared());
    }

    /** Create {@link JsonMessageConverter} with specified {@link ObjectMapper}. */
    public JsonMessageConverter(final ObjectMapper mapper) {
        super(MediaType.APPLICATION_JSON);
        this.mapper = mapper;
    }

    //~ Methods ......................................................................................................................................

    @Override public Object read(Class<?> responseType, Type genericType, MediaType contentType, InputStream stream)
        throws IOException
    {
        if (genericType instanceof ParameterizedType && isList((ParameterizedType) genericType)) {
            final Type[] arguments = ((ParameterizedType) genericType).getActualTypeArguments();
            if (arguments.length == 1 && isClass(arguments[0])) return read(responseType, stream, (Class<?>) arguments[0]);
            else {  // It is a List<T>
                final Type type = ((ParameterizedType) genericType).getRawType();
                if (isClass(type)) {
                    final Class<?> rawListType = cast(type);
                    return read(rawListType, stream, responseType);
                }
            }
        }
        return mapper.readValue(stream, responseType);
    }

    @Override public void write(Object content, MediaType contentType, OutputStream stream)
        throws IOException
    {
        mapper.writeValue(stream, content);
    }

    @Override protected boolean supports(Class<?> clazz) {
        return true;
    }

    private Object read(Class<?> responseType, InputStream stream, Class<?> argument)
        throws IOException
    {
        final CollectionType list   = TypeFactory.defaultInstance().constructCollectionType(List.class, argument);
        final List<?>        result = mapper.readValue(stream, list);
        return responseType.equals(Seq.class) ? seq(result) : result;
    }

    private boolean isClass(Type rawType) {
        return rawType instanceof Class;
    }

    private boolean isList(ParameterizedType genericType) {
        final Type rawType = genericType.getRawType();
        if (rawType instanceof Class) {
            final Class<?> rawClass = (Class<?>) rawType;
            return rawClass.equals(Seq.class) || rawClass.equals(List.class);
        }
        return false;
    }
}
