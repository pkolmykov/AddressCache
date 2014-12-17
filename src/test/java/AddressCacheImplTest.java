import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
}