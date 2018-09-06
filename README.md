# phonestate
```
compile 'tools.android:phonestate:1.0.3'
```
```
// Usage:
mPhoneWatcher = new PhoneWatcher(this, true, new PhoneChangeListener() {
            @Override
            public void onInit(final PhoneState state) {
                Log.d("PPP", "onInit|" + state);
            }

            @Override
            public void onChange(final PhoneState oldState, final PhoneState newState) {
                Log.d("PPP", "onChange|" + oldState + "|to|" + newState);
            }
        });
```
```
// Usage:
mPhoneWatcher = new PhoneWatcher(this, true, new PhoneUsingListener() {
            @Override
            public void onUse(final PhoneState state) {
                Log.d("PPP", "onUse|" + state);
            }

            @Override
            public void onNotUse() {
                Log.d("PPP", "onNotUse|");
            }
        });
```
