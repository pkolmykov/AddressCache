import org.junit.Ignore;
import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AddressCacheImplTest {


    @Test
    public void testCacheCleanup() throws Exception {
        int ttl = 1000;
        AddressCacheImpl addressCache = new AddressCacheImpl(ttl);
        addressCache.offer(mock(InetAddress.class));
        addressCache.offer(mock(InetAddress.class));
        addressCache.cleanup();
        assertEquals(2, addressCache.size());
        Thread.sleep(ttl);
        addressCache.cleanup();
        assertEquals(0, addressCache.size());

        addressCache.offer(mock(InetAddress.class));
        Thread.sleep(ttl);
        addressCache.offer(mock(InetAddress.class));
        assertEquals(2, addressCache.size());
        addressCache.cleanup();
        assertEquals(1, addressCache.size());
    }

    @Test
    public void testUniqueness() throws Exception {
        AddressCacheImpl addressCache = new AddressCacheImpl(1000);
        InetAddress address = mock(InetAddress.class);
        int interval = 100;
        long currentTimeMillis = System.currentTimeMillis();
        addressCache.offer(address);
        Thread.sleep(interval);
        addressCache.offer(address);
        assertEquals(1, addressCache.size());
        assertEquals(1, addressCache.timeInfo.size());
        assertTrue("Time should be updated when offering existing address",
                addressCache.timeInfo.get(address) >= currentTimeMillis + interval);
    }

    @Test
    @Ignore//will take more then 5 secs
    public void testCleaner() throws Exception {
        int ttl = 1000;
        AddressCacheImpl addressCache = new AddressCacheImpl(ttl);
        addressCache.offer(mock(InetAddress.class));
        addressCache.offer(mock(InetAddress.class));
        assertEquals(2, addressCache.size());
        Thread.sleep(AddressCacheImpl.CLEANUP_INTERVAL + 100);
        assertEquals(0, addressCache.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testClose() throws Exception {
        AddressCacheImpl addressCache = new AddressCacheImpl(1000);
        addressCache.offer(mock(InetAddress.class));
        assertFalse(addressCache.isEmpty());
        addressCache.close();
        addressCache.isEmpty();//should throw IllegalStateException
    }

    @Test
    public void testTakeRemovePeek() throws Exception {
        InetAddress first = mock(InetAddress.class);
        InetAddress second = mock(InetAddress.class);
        AddressCacheImpl addressCache = new AddressCacheImpl(1000);
        addressCache.offer(first);
        addressCache.offer(second);
        assertEquals(second, addressCache.peek());
        assertEquals(second, addressCache.take());
        assertEquals(first, addressCache.remove());
        assertNull(addressCache.peek());
    }
}