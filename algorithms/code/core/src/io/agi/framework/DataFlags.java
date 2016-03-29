package io.agi.framework;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Flags that modify the persistence behaviour of the framework. The aim is to reduce IO and yield a faster overall
 * update. Since the framework is heavily IO bound, this makes a big difference.
 *
 * Supported flags are:
 *
 * - Lazy persist: This will check for changes and only persist if changed. There is a cost: We need to keep a copy of
 *   the old value of the data, and compare them. If you rarely change a data structure, then add this flag.
 *
 * - Node cache: If you do not expect / allow external writes of the data, you can cache it in the Node. This avoids the
 *   need to load the data every time, but it means you won't see external changes. Use this flag when nothing else will
 *   change your data.
 *
 * - Sparse unit: This reduces the serialized size of the data. Use it whenever you have sparse unit data (i.e. all
 *   values are binary and there are few '1' bits.
 *
 * - Persist only: This data is never fetched. It is written for the consumption of external entities or debugging. The
 *   Entity must be regenerating it from other data.
 *
 * - Persist on flush: The data is only persisted when a flush occurs.
 *
 * Created by dave on 28/03/16.
 */
public class DataFlags {

    public static final String FLAG_LAZY_PERSIST = "lazy-persist"; /// will only persist if the Data has changed. Creates a copy on fetch to check for changes.
    public static final String FLAG_NODE_CACHE = "node-cache"; /// Keeps a copy of the Data in the Node, and assumes it hasn't changed. Reloads from Cache.
    public static final String FLAG_SPARSE_UNIT = "sparse-unit"; /// When writing the Data, it will be converted to sparse unit encoding (reduces size hugely).

    public static final String FLAG_PERSIST_ONLY = "persist-only"; /// since this is only for debug output, never read it. Value will always be replaced.
    public static final String FLAG_PERSIST_ON_FLUSH = "persist-on-flush"; /// will persist once on update where entity flush property is set.

    public HashMap< String, HashSet< String > > _dataFlags = new HashMap< String, HashSet< String > >();

    public DataFlags() {

    }

    public boolean hasFlag( String dataName, String flag ) {
        HashSet< String > flags = _dataFlags.get( dataName );

        if( flags == null ) {
            return false;
        }

        return flags.contains( flag );
    }

    public void putFlag( String dataName, String flag ) {
        HashSet< String > flags = _dataFlags.get( dataName );

        if( flags == null ) {
            flags = new HashSet< String >();
            _dataFlags.put( dataName, flags );
        }

        flags.add( flag );
    }

    public void removeFlag( String dataName, String flag ) {
        HashSet< String > flags = _dataFlags.get( dataName );

        if( flags == null ) {
            return;
        }

        flags.remove(flag);
    }
}
