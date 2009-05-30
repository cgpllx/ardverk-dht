package com.ardverk.dht.message;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.ardverk.utils.StringUtils;

public class BencodingOutputStream extends FilterOutputStream {

    public BencodingOutputStream(OutputStream out) {
        super(out);
    }
    
    @SuppressWarnings("unchecked")
    public void writeObject(Object obj) throws IOException {
        if (obj instanceof byte[]) {
            byte[] bytes = (byte[])obj;
            byte[] length = Integer.toString(bytes.length)
                                .getBytes(StringUtils.UTF_8);
            
            write(length);
            write(':');
            write(bytes);
            
        } else if (obj instanceof Boolean) {
            if (((Boolean)obj).booleanValue()) {
                writeObject(Integer.valueOf(1));
            } else {
                writeObject(Integer.valueOf(0));
            }
            
        } else if (obj instanceof Number) {
            String num = ((Number)obj).toString();
            write('i');
            write(num.getBytes(StringUtils.UTF_8));
            write('e');
            
        } else if (obj instanceof String) {
            writeObject(((String)obj).getBytes(StringUtils.UTF_8));
        
        } else if (obj instanceof List) {
            List<?> list = (List<?>)obj;
            write('l');
            for (Object o : list) {
                writeObject(o);
            }
            write('e');
        
        } else if (obj instanceof Map) {
            Map<String, ?> map = (Map<String, ?>)obj;
            if (!(map instanceof SortedMap)) {
                map = new TreeMap<String, Object>(map);
            }
            
            write('d');
            for (Object key : map.keySet()) {
                if (!(key instanceof String)) {
                    throw new IOException("Key must be a String: " + key);
                }
                
                Object value = map.get(key);
                writeObject(key);
                writeObject(value);
            }
            write('e');
            
        } else {
            throw new IOException("Cannot bencode " + obj);
        }
    }
}