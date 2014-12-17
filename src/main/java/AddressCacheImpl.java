import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by pkolmykov on 17.12.2014.
 */
public class AddressCacheImpl implements AddressCache {

    protected static final int CLEANUP_INTERVAL = 5000;
    protected final int ttl;
    protected Map<InetAddress, Long> timeInfo = Collections.synchronizedMap(new WeakHashMap<>());
    protected LinkedBlockingDeque<InetAddress> deque = new LinkedBlockingDeque<>();
    protected final Timer cleaner = new Timer(true);
    private boolean closed;

    public AddressCacheImpl(int ttl) {
        this.ttl = ttl;
    }

    {
        cleaner.schedule(new TimerTask() {
            @Override
            public void run() {
                cleanup();
            }

        }, CLEANUP_INTERVAL);
    }

    @Override
    public boolean offer(InetAddress address) {
        ensureOpen();
        timeInfo.put(address, System.currentTimeMillis());
        deque.remove(address);
        return deque.offer(address);
    }

    @Override
    public boolean contains(InetAddress address) {
        ensureOpen();
        return deque.contains(address);
    }

    @Override
    public boolean remove(InetAddress address) {
        ensureOpen();
        return deque.remove(address);
    }

    @Override
    public InetAddress peek() {
        ensureOpen();
        return deque.peekLast();
    }

    @Override
    public InetAddress remove() {
        ensureOpen();
        return deque.pollLast();
    }

    @Override
    public InetAddress take() throws InterruptedException {
        ensureOpen();
        return deque.takeLast();
    }

    @Override
    public void close() {
        deque = null;
        timeInfo = null;
        cleaner.cancel();
        closed = true;
    }

    private void ensureOpen() {
        if (closed)
            throw new IllegalStateException("AddressCache closed");
    }


    @Override
    public int size() {
        ensureOpen();
        return deque.size();
    }

    @Override
    public boolean isEmpty() {
        ensureOpen();
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
