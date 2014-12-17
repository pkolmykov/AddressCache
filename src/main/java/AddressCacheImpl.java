import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by pkolmykov on 17.12.2014.
 */
public class AddressCacheImpl implements AddressCache {

    protected static final int CLEANUP_INTERVAL = 5000;
    protected final int ttl;
    protected Map<InetAddress, Long> timeInfo = Collections.synchronizedMap(new WeakHashMap<>());
    protected LinkedBlockingDeque<InetAddress> deque = new LinkedBlockingDeque<>();

    public AddressCacheImpl(int ttl) {
        this.ttl = ttl;
        //todo auto cleanup
    }

    @Override
    public boolean offer(InetAddress address) {
        timeInfo.put(address, System.currentTimeMillis());
        deque.remove(address);
        return deque.offer(address);
    }

    @Override
    public boolean contains(InetAddress address) {
        return deque.contains(address);
    }

    @Override
    public boolean remove(InetAddress address) {
        return deque.remove(address);
    }

    @Override
    public InetAddress peek() {
        return deque.peekLast();
    }

    @Override
    public InetAddress remove() {
        return deque.pollLast();
    }

    @Override
    public InetAddress take() throws InterruptedException {
        return deque.takeLast();
    }

    @Override
    public void close() {
        deque.clear();
        timeInfo.clear();
        //todo
    }

    @Override
    public int size() {
        return deque.size();
    }

    @Override
    public boolean isEmpty() {
        return deque.isEmpty();
    }

    protected void cleanup() {
        Iterator<InetAddress> iterator = deque.iterator();
        while (iterator.hasNext()) {
            InetAddress address = iterator.next();
            if (timeInfo.get(address) + ttl <= System.currentTimeMillis()) {
                iterator.remove();
            } else {
                return;
            }
        }

    }


}
