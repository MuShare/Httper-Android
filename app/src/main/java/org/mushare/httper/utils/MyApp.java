package org.mushare.httper.utils;


import android.app.Application;
import android.database.Cursor;

import org.greenrobot.greendao.database.Database;
import org.mushare.httper.entity.DaoMaster;
import org.mushare.httper.entity.DaoSession;
import org.mushare.httper.entity.RequestRecord;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.mushare.httper.entity.DaoMaster.dropAllTables;

public class MyApp extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.OpenHelper openHelper = new DaoMaster.OpenHelper(this, "httper-db") {
            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {
//                Log.i("upgradeDB", "oldVersion: " + oldVersion + ", newVersion: " + newVersion);
                if (oldVersion < 2) {
                    Cursor cursor = db.rawQuery("select * from REQUEST_RECORD order by CREATE_AT " +
                            "desc", null);
                    Set<RequestRecord> set = new LinkedHashSet<>();
                    while (cursor.moveToNext()) {
                        String body = cursor.getString(7);
                        if (body == null) body = "";
                        set.add(new RequestRecord(cursor.getLong(0), cursor.getLong(1), cursor
                                .getString(2), cursor.getString(3), cursor.getString(4), cursor
                                .getString(5), cursor.getString(6), body));
                    }
                    cursor.close();
                    dropAllTables(db, true);
                    onCreate(db);
                    for (RequestRecord record : set) {
                        db.execSQL("insert into REQUEST_RECORD values(?, ?, ?, ?, ?, ?, ?, ?)",
                                new Object[]{record.getId(), record.getCreateAt(), record
                                        .getMethod(), record.getHttp(), record.getUrl(), record
                                        .getHeaders(), record.getParameters(), record.getBody()});
                    }
                }
            }
        };
        Database db = openHelper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}