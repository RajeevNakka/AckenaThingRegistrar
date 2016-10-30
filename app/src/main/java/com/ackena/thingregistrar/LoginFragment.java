package com.ackena.thingregistrar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ackena.thingregistrar.other.TaskResult;
import com.ackena.thingregistrar.other.Util;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mJidView;
    private EditText mPasswordView;
    private AckenaProgressDialog progressDialog;

    public LoginFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LoginFragment newInstance(int sectionNumber) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_login, container, false);

        // Set up the login form.
        mJidView = (AutoCompleteTextView) rootView.findViewById(R.id.jid);

        mPasswordView = (EditText) rootView.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mJidSignInButton = (Button) rootView.findViewById(R.id.jid_sign_in_button);
        mJidSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        progressDialog = new AckenaProgressDialog(getActivity());

        return rootView;
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mJidView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String jid = mJidView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid jid address.
        if (TextUtils.isEmpty(jid)) {
            mJidView.setError(getString(R.string.error_field_required));
            focusView = mJidView;
            cancel = true;
        } else if (!isJidValid(jid)) {
            mJidView.setError(getString(R.string.error_invalid_username));
            focusView = mJidView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(getActivity(), jid, password) {
                @Override
                protected void onPostExecute(final TaskResult<Void> result) {
                    mAuthTask = null;
                    showProgress(false);

                    if (result.IsSuccess) {
                        Intent intent = new Intent(getActivity(),Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        if (result.exception != null) {
                            String message = result.exception.getMessage();
                            if (message.contains("not-authorized")) {
                                mJidView.setError(getString(R.string.error_authorization_failed));
                                mPasswordView.setError(getString(R.string.error_authorization_failed));
                            } else if(message.contains("Unable to resolve host")){
                                if(Util.isNetworkAvailable(getActivity()))
                                {
                                    mJidView.setError(getString(R.string.error_invalid_username));
                                    mJidView.requestFocus();
                                }else {
                                    Toast.makeText(getActivity(),"Network is not available",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        else {
                            Toast.makeText(getActivity(),"Trouble logging in, please try again",Toast.LENGTH_LONG).show();
                        }

                    }
                }

                @Override
                protected void onCancelled() {
                    mAuthTask = null;
                    showProgress(false);
                }
            };

            mAuthTask.execute((Void) null);
        }
    }

    private boolean isJidValid(String jid) {
        //TODO: Replace this with your own logic
        return jid.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress dialog
     */

    private void showProgress(final boolean show) {
        if(show)
            progressDialog.show();
        else
            progressDialog.close();
    }
}
