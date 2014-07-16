package com.cloupix.fennec.business;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by AlonsoUSA on 30/06/14.
 *
 */
public class SessionManager {

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    // A managed pool of background download threads
    private final ThreadPoolExecutor mThreadPool;

    // A single instance of PhotoManager, used to implement the singleton pattern
    private static final SessionManager sInstance;

    static {

        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        sInstance = new SessionManager();
    }

    private SessionManager() {

        mThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());

    }

    public static SessionManager getInstance() {

        return sInstance;
    }

    public void addSession(Socket sourceSocket) throws IOException {
        Session session = new Session(sourceSocket);
        mThreadPool.execute(session);
    }
}