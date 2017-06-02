package org.mushare.httper.utils;


import android.app.Application;

import org.greenrobot.greendao.database.Database;
import org.mushare.httper.entity.DaoMaster;
import org.mushare.httper.entity.DaoSession;

public class MyApp extends Application {
    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    public static final boolean ENCRYPTED = false;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ?
                "httper-db-encrypted" : "httper-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("password") : helper
                .getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}