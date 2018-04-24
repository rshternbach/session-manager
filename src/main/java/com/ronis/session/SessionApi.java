package com.ronis.session;

/**
 * Interface provides methods exposed by session api
 */

public interface SessionApi {
    /**
     * create a new {@link Session}
     *
     * @return session
     */
    Session CreateSession();

    /**
     * closes {@link Session}
     *
     * @param Session
     *            session to close
     * @return boolean indicates if session closed successfully
     */
    boolean closeSession(Session session);

    /**
     * keep alive an expired session {@link Session}
     *
     * @param Session
     *            session to keep alive
     * @return boolean indicates if session keep alive successfully
     */
    boolean keepAlive(Session session);
}
