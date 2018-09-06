package tools.android.phonestate;

public interface PhoneChangeListener {
    /**
     * Phone state of initialize
     */
    void onInit(PhoneState state);

    /**
     * Phone change from 'oldState' to 'newState'
     */
    void onChange(PhoneState oldState, PhoneState newState);
}
