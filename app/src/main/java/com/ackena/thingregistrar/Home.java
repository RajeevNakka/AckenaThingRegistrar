package com.ackena.thingregistrar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ackena.thingregistrar.entities.User;
import com.ackena.thingregistrar.other.TaskResult;
import com.ackena.thingregistrar.other.Util;

import org.jivesoftware.smack.AbstractConnectionClosedListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        UserFragment.OnListFragmentInteractionListener {

    private TextView mUserIdTextView;
    private UserFragment mFragment;
    private TextView mNavConnectionStatusTextView;
    private ImageView mConnectionErrorImageView;
    private TextView mOverAllStatus;
    private View mStatusContainer;
    AckenaProgressDialog progressDialog;

    private AbstractConnectionClosedListener connectionListener = new AbstractConnectionClosedListener() {
        @Override
        public void connectionTerminated() {
            Log.i("ATR", "Connection terminated...");
            reloginAsync();
        }
    };
    private boolean mLoggingIn = false;

    private void reloginAsync() {
        Session.loggedIn = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNavConnectionStatusTextView.setText("Connecting....");
            }
        });
        loginAsync();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        Session.loadPreferences(this);
        if (Session.mUsername == null || Session.mPassword == null || Session.mServer == null) {
            Intent intent = new Intent(this, UserAccountActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        progressDialog = new AckenaProgressDialog(this);
        setupFragment();

        mUserIdTextView = (TextView) header.findViewById(R.id.textView_user);
        mNavConnectionStatusTextView = (TextView) header.findViewById(R.id.textView_nav_connection_status);

        mOverAllStatus = (TextView) findViewById(R.id.text_view_overall_status);
        mStatusContainer = findViewById(R.id.status_container);

        mConnectionErrorImageView = (ImageView) header.findViewById(R.id.imageConnectionError);

        mConnectionErrorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConnectionErrorImageView.setVisibility(View.INVISIBLE);
                loginAsync();
            }
        });

        mUserIdTextView.setText(Session.mUsername + "@" + Session.mServer);

        if (Session.loggedIn) {
            mNavConnectionStatusTextView.setText("Online");
            mOverAllStatus.setText("Loading Contacts.....");
            Session.connection.addConnectionListener(connectionListener);
        } else {
            loginAsync();
        }
    }

    private void setupFragment() {
        mFragment = UserFragment.newInstance(1);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_home, mFragment).commit();
    }

    private void loginAsync() {
        if (Session.loggedIn == false || Session.connection == null) {
            UserLoginTask userLoginTask = new UserLoginTask(this, Session.mUsername, Session.mPassword, Session.mServer) {
                @Override
                protected void onPreExecute() {
                    mLoggingIn = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStatusContainer.setVisibility(View.VISIBLE);
                            mNavConnectionStatusTextView.setText("Connecting....");
                            mOverAllStatus.setText("Connecting....");
                        }
                    });
                }

                @Override
                protected void onPostExecute(final TaskResult<Void> result) {
                /*dialog.dismiss();*/
                    mLoggingIn = false;
                    if (progressDialog != null)
                        progressDialog.close();
                    if (result.IsSuccess) {
                        Log.i("ATR", "Login result");
                        if (Session.loggedIn) {
                            /*runOnUiThread(new Runnable() {
                                @Override
                                public void run() {*/

                            Session.connection.removeConnectionListener(connectionListener);
                            Session.connection.addConnectionListener(connectionListener);

                            mNavConnectionStatusTextView.setText("Online");
                            mConnectionErrorImageView.setVisibility(View.INVISIBLE);
                               /* }
                            });*/
                        }
                    } else {
                        mNavConnectionStatusTextView.setText("(Connection Error..)");
                        mConnectionErrorImageView.setVisibility(View.VISIBLE);
                        mStatusContainer.setVisibility(GONE);
                        if (result.exception != null) {
                            String message = result.exception.getMessage();
                            if (message.contains("not-authorized")) {
                                Toast.makeText(Home.this, getString(R.string.error_authorization_failed), Toast.LENGTH_LONG).show();
                            } else if (message.contains("Unable to resolve host")) {
                                if (Util.isNetworkAvailable(Home.this)) {
                                    Toast.makeText(Home.this, getString(R.string.error_invalid_username), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Home.this, "Network not available", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(Home.this, "Trouble logging in, please try again", Toast.LENGTH_LONG).show();
                        }

                    }

                }

                @Override
                protected void onCancelled() {
                    /*dialog.dismiss();*/
                    mNavConnectionStatusTextView.setText("(Login cancelled..)");
                    mConnectionErrorImageView.setVisibility(View.VISIBLE);
                    Toast.makeText(Home.this, "Login canceled...", Toast.LENGTH_SHORT).show();
                }
            };
            Log.i("ATR", "attempt logging in");
            AsyncTaskCompat.executeParallel(userLoginTask);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_sign_out) {

            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure, you want to sign out?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Session.clearPreferences(Home.this);
                            Intent intent = new Intent(Home.this, UserAccountActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onUserListFragmentInteraction_Click(User item) {
        if (Session.loggedIn) {
            Intent intent = new Intent(this, ATR.class);
            intent.putExtra(ATR.USER_ID, item.id);
            startActivityForResult(intent, ATR.REQUEST_CODE);
        } else {
            if (Util.isNetworkAvailable(this)) {
                Toast.makeText(Home.this, "Connection lost, try after reconnection", Toast.LENGTH_SHORT).show();
                progressDialog.show();
                reloginAsync();

            } else {
                Toast.makeText(Home.this, "Network not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ATR.REQUEST_CODE && resultCode == ATR.RESULT_CODE_CONNECTION_RECREATED){
            if (Session.loggedIn) {
                if (Session.connection != null) {
                    Session.connection.removeConnectionListener(connectionListener);
                    Session.connection.addConnectionListener(connectionListener);
                    mNavConnectionStatusTextView.setText("Online");
                    mConnectionErrorImageView.setVisibility(View.GONE);
                }
            } else {
                if (!mLoggingIn) {
                    mNavConnectionStatusTextView.setText("(Connection Error..)");
                    mConnectionErrorImageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onUserListFragmentInteraction_DataLoaded(Object source, Object dataSource) {
        mStatusContainer.setVisibility(GONE);
    }

    @Override
    public void onUserListFragmentInteraction_DataLoading(Object source, Object dataSource) {
        mStatusContainer.setVisibility(VISIBLE);
        mOverAllStatus.setText("Loading contacts....");
    }

}
