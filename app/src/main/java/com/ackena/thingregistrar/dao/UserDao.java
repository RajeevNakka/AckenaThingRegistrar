package com.ackena.thingregistrar.dao;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.ackena.thingregistrar.Session;
import com.ackena.thingregistrar.entities.User;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample status for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class UserDao {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<User> ITEMS = new ArrayList<User>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, User> ITEM_MAP = new HashMap<String, User>();

    /*private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }*/

    private final static Handler handler;

    static {
        handler = new Handler(Looper.getMainLooper());
    }

    public static final DataChangeNotifier<User> USER_DATA_CHANGE_NOTIFIER;
    private static List<IDataChangeListener<User>> mRosterChangeListeners;

    static {
        mRosterChangeListeners = new ArrayList<IDataChangeListener<User>>();
        USER_DATA_CHANGE_NOTIFIER = new DataChangeNotifier<User>(mRosterChangeListeners);
    }

    public static Roster roster;
    static RosterListener rosterListener = new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> addresses) {
            Log.i("ATR-Roster-add", TextUtils.join(",", addresses));

            for (String entry : addresses) {
                if (!ITEM_MAP.containsKey(entry)) {
                    final User user = new User(entry);
                    addItem(user);
                    //Notify using UI thread
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            USER_DATA_CHANGE_NOTIFIER.notifyItemInserted(null, ITEMS, user, ITEMS.size() - 1);
                        }
                    });
                }
            }
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            Log.i("ATR-Roster-update", TextUtils.join(",", addresses));
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            Log.i("ATR-Roster-delete", TextUtils.join(",", addresses));
            for (String entry : addresses) {
                if (ITEM_MAP.containsKey(entry)) {
                    final User user = ITEM_MAP.get(entry);
                    final int pos = ITEMS.indexOf(user);
                    ITEMS.remove(user);
                    ITEM_MAP.remove(entry);

                    //Notify using UI thread
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            USER_DATA_CHANGE_NOTIFIER.notifyItemDeleted(null, ITEMS, user, pos);
                        }
                    });
                }
            }
        }

        @Override
        public void presenceChanged(Presence presence) {
            String entry = presence.getFrom();

            int endIndex = entry.indexOf('/');
            if (endIndex != -1)
                entry = entry.substring(0, endIndex);
            Log.i("ATR-Roster-presence", presence.getStatus() + "" + entry);
            if (ITEM_MAP.containsKey(entry)) {
                final User user = ITEM_MAP.get(entry);
                final int pos = ITEMS.indexOf(user);
                user.isAvailable = presence.isAvailable();
                user.status = user.isAvailable ? presence.getMode().name() : presence.getType().name();

                //Notify using UI thread
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        USER_DATA_CHANGE_NOTIFIER.notifyItemChanged(null, ITEMS, user, pos);
                    }
                });
            }
        }
    };

    private static RosterLoadedListener rosterLoadedListener = new RosterLoadedListener() {
        @Override
        public void onRosterLoaded(final Roster roster) {
            roster.getEntriesAndAddListener(rosterListener, new RosterEntries() {
                @Override
                public void rosterEntires(Collection<RosterEntry> rosterEntries) {
                    Presence presence;

                    int position = 0;
                    for (RosterEntry entry : rosterEntries) {
                        presence = roster.getPresence(entry.getUser());

                        final User user = new User(entry.getUser());
                        addItem(user);

                        System.out.println(entry.getUser());
                        System.out.println(presence.getType().name());
                        System.out.println(presence.getStatus());
                    }

                    //Notify using UI thread
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            USER_DATA_CHANGE_NOTIFIER.notifyDataSetLoaded(null, ITEMS);
                            USER_DATA_CHANGE_NOTIFIER.notifyDataSetChanged(null, ITEMS);
                        }
                    });
                }
            });
        }
    };

    public static void reLoadRoster() {

        clearDataSet();
        handler.post(new Runnable() {
            @Override
            public void run() {
                USER_DATA_CHANGE_NOTIFIER.notifyDataSetLoading(null, ITEMS);
            }
        });

        try {
            roster.reload();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        /*if (roster.isLoaded() == false) {
            try {
                roster.reloadAndWait();

            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        Collection<RosterEntry> entries = roster.getEntries();
        Presence presence;*/

        /*int position = 0;*/
        /*for (RosterEntry entry : entries) {
            presence = roster.getPresence(entry.getUser());

            User user = new User(String.valueOf(position++), entry.getUser(), presence.getType().name(), presence.isAvailable());
            addItem(user);

            System.out.println(entry.getUser());
            System.out.println(presence.getType().name());
            System.out.println(presence.getStatus());
        }*/
    }

    private static void clearDataSet() {
        ITEMS.clear();
        ITEM_MAP.clear();

        handler.post(new Runnable() {
            @Override
            public void run() {
                USER_DATA_CHANGE_NOTIFIER.notifyDataSetChanged(null, ITEMS);
            }
        });
    }

    public static void connect(Context context, String mUserName, String mPassword, String mServer) throws XMPPException, SmackException, IOException {
        Session.connection = new XMPPTCPConnection(mUserName, mPassword, mServer);
        Session.connection.setPacketReplyTimeout(30000);

        if (roster != null) {
            roster.removeRosterLoadedListener(rosterLoadedListener);
        }

        clearDataSet();

        handler.post(new Runnable() {
            @Override
            public void run() {
                USER_DATA_CHANGE_NOTIFIER.notifyDataSetLoading(null, ITEMS);
            }
        });

        Log.i("ATR", "Loading friends/users....");
        roster = Roster.getInstanceFor(Session.connection);
        roster.addRosterLoadedListener(rosterLoadedListener);

        Session.connection.connect().login();
        Session.loggedIn = true;
        Session.saveToPreferences(context, mUserName, mPassword, mServer);
    }

    private static void addItem(User item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static User createDummyItem(int position) {
        return new User(String.valueOf(position), "Item " + position, makeDetails(position), false);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public static void addOnUserChangeListener(IDataChangeListener listener) {
        if (null != listener && !mRosterChangeListeners.contains(listener))
            mRosterChangeListeners.add(listener);
    }

    public static void removeOnUserChangeListener(IDataChangeListener listener) {
        if (null != listener && mRosterChangeListeners.contains(listener))
            mRosterChangeListeners.remove(listener);
    }
}
