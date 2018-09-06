package tools.android.phonestate;

import android.content.Context;
import android.content.IntentFilter;

public class PhoneWatcher {

    private PhoneInterfaceWatcher phoneWatcher;
    private PhoneState saveState;
    private Object lock = new Object();

    public PhoneWatcher(Context context) {
        init(context, false);
    }

    public PhoneWatcher(Context context, boolean enableLogcat) {
        init(context, enableLogcat);
    }

    public PhoneWatcher(Context context, PhoneChangeListener listener) {
        setPhoneChangeListener(listener);
        init(context, false);
    }

    public PhoneWatcher(Context context, boolean enableLogcat, PhoneChangeListener listener) {
        setPhoneChangeListener(listener);
        init(context, enableLogcat);
    }

    public PhoneWatcher(Context context, PhoneUsingListener listener) {
        setPhoneUsingListener(listener);
        init(context, false);
    }

    public PhoneWatcher(Context context, boolean enableLogcat, PhoneUsingListener listener) {
        setPhoneUsingListener(listener);
        init(context, enableLogcat);
    }

    private void init(final Context context, boolean enableLogcat) {
        phoneWatcher = new PhoneInterfaceWatcher(enableLogcat) {
            @Override
            protected void phoneNotInUse() {
                super.phoneNotInUse();
                PhoneState oldState = saveState;
                PhoneState newState = getCurrentPhoneState(context);
                saveState = newState;
                synchronized (lock) {
                    try {
                        if (mPhoneUsingListener != null) {
                            mPhoneUsingListener.onNotUse();
                        } else {
                            onNotUse();
                        }
                    } catch (Throwable t) {
                    }
                }
                if (oldState != newState) {
                    synchronized (lock) {
                        try {
                            if (mPhoneChangeListener != null) {
                                mPhoneChangeListener.onChange(oldState, newState);
                            } else {
                                onChange(oldState, newState);
                            }
                        } catch (Throwable t) {
                        }
                    }
                }
            }

            @Override
            protected void phoneReceiving() {
                super.phoneReceiving();
                PhoneState oldState = saveState;
                PhoneState newState = getCurrentPhoneState(context);
                saveState = newState;
                synchronized (lock) {
                    try {
                        if (mPhoneUsingListener != null) {
                            mPhoneUsingListener.onUse(newState);
                        } else {
                            onUse(newState);
                        }
                    } catch (Throwable t) {
                    }
                }
                if (oldState != newState) {
                    synchronized (lock) {
                        try {
                            if (mPhoneChangeListener != null) {
                                mPhoneChangeListener.onChange(oldState, newState);
                            } else {
                                onChange(oldState, newState);
                            }
                        } catch (Throwable t) {
                        }
                    }
                }
            }

            @Override
            protected void phoneSending() {
                super.phoneSending();
                PhoneState oldState = saveState;
                PhoneState newState = getCurrentPhoneState(context);
                saveState = newState;
                synchronized (lock) {
                    try {
                        if (mPhoneUsingListener != null) {
                            mPhoneUsingListener.onUse(newState);
                        } else {
                            onUse(newState);
                        }
                    } catch (Throwable t) {
                    }
                }
                if (oldState != newState) {
                    synchronized (lock) {
                        try {
                            if (mPhoneChangeListener != null) {
                                mPhoneChangeListener.onChange(oldState, newState);
                            } else {
                                onChange(oldState, newState);
                            }
                        } catch (Throwable t) {
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        try {
            context.registerReceiver(phoneWatcher, filter);
            phoneWatcher.say("PhoneWatcher", "regist");
        } catch (Throwable t) {
            t.printStackTrace();
        }
        saveState = PhoneInterfaceWatcher.getCurrentPhoneState(context);
        synchronized (lock) {
            try {
                if (mPhoneChangeListener != null) {
                    mPhoneChangeListener.onInit(saveState);
                } else {
                    onInit(saveState);
                }

            } catch (Throwable t) {
            }

        }
        synchronized (lock) {
            try {
                if (mPhoneUsingListener != null) {
                    if (PhoneState.NOT_IN_USE == saveState) {
                        mPhoneUsingListener.onNotUse();
                    } else {
                        mPhoneUsingListener.onUse(saveState);
                    }
                } else {
                    if (PhoneState.NOT_IN_USE == saveState) {
                        onNotUse();
                    } else {
                        onUse(saveState);
                    }
                }
            } catch (Throwable t) {
            }
        }
    }

    public PhoneState getState() {
        return saveState;
    }

    private PhoneUsingListener mPhoneUsingListener;

    private void setPhoneUsingListener(PhoneUsingListener l) {
        this.mPhoneUsingListener = l;
    }

    private PhoneChangeListener mPhoneChangeListener;

    private void setPhoneChangeListener(PhoneChangeListener l) {
        this.mPhoneChangeListener = l;
    }

    public void pause(Context context) {
        if (phoneWatcher != null) {
            phoneWatcher.pause();
        }
    }

    public void resume(Context context) {
        if (phoneWatcher != null) {
            phoneWatcher.resume();
        }
    }

    public void release(Context context) {
        if (phoneWatcher != null) {
            try {
                context.unregisterReceiver(phoneWatcher);
                phoneWatcher.release();
            } catch (Exception e) {
            }
        }
        this.phoneWatcher = null;
        this.mPhoneUsingListener = null;
        this.mPhoneChangeListener = null;
    }

    /**
     * Phone state of initialize
     */
    protected void onInit(PhoneState state) {
    }

    /**
     * Phone change from 'oldState' to 'newState'
     */
    protected void onChange(PhoneState oldState, PhoneState newState) {
    }

    /**
     * Phone is in use
     */
    protected void onUse(PhoneState state) {
    }

    /**
     * Phone is not in use
     */
    protected void onNotUse() {
    }
}
