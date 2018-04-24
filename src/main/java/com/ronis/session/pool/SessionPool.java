package com.ronis.session.pool;

import com.ronis.session.Session;

/**
 * Interface provides methods exposed by session pool
 */

public interface SessionPool {

    /**
     * request a valid {@link Session} from the session pool
     *
     * @return valid session for the pool
     */
    Session getSession();

    /**
     * release a {link@ Session} after have been used back to the session pool
     *
     * @param Session
     *            session
     */

    void releaseSession(Session session);

}
