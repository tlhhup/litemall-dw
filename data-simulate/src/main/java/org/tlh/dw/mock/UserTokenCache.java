package org.tlh.dw.mock;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author 离歌笑
 * @desc
 * @date 2020-12-14
 */
public class UserTokenCache implements Delayed {

    public static final long TOKEN_EXPIRE_SECONDS = 30 * 60;

    private int userId;
    private long delay;

    public UserTokenCache() {
    }

    public UserTokenCache(int userId, long delay) {
        this.userId = userId;
        this.delay = System.currentTimeMillis() + delay * 1000;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.delay - System.currentTimeMillis(), TimeUnit.SECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == this) {
            return 0;
        }
        if (o instanceof UserTokenCache) {
            long l = this.delay - ((UserTokenCache) o).delay;
            if (l < 0) {
                return -1;
            } else if (l > 0) {
                return 1;
            }
        }
        return 0;
    }

    public int getUserId() {
        return userId;
    }
}
