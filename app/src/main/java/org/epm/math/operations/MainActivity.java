package org.epm.math.operations;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;

import org.epm.math.base.Math;
import org.epm.math.settings.SettingsActivity;

import java.util.Locale;
import java.util.MissingResourceException;

import org.epm.math.app.R;

public final class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    @Nullable
    private Operations operations = null;

    private final Button[] btns = new Button[10];

    @Nullable
    private MediaPlayer mpVictory = null;
    @Nullable
    private MediaPlayer mpLose = null;

    private final Handler handler = new Handler();

    @IdRes
    private final static int[] textSwitchersOp = {
            R.id.textView1_1, R.id.textView1_2, R.id.textView1_sign,
            R.id.textView2_1, R.id.textView2_2, R.id.textView2_sign,
            R.id.textView2_signPrefixed
    };
    @IdRes
    private final static int[] textSwitchersRes = {
            R.id.textView3_1, R.id.textView3_2, R.id.textView3_3
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Math.hideSystemUI(getWindow().getDecorView());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LoaderManager.getInstance(this)
                .initLoader(0, null, this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        operations = new Operations(this);

        for (final @IdRes int tvid : textSwitchersOp) {
            findViewById(tvid).setOnClickListener(newOpOnClickListener);
            initTextSwitcher(tvid, textSwitcherFactoryOps);
        }
        for (final @IdRes int tvid : textSwitchersRes) {
            findViewById(tvid).setOnClickListener(deleteClickListener);
            initTextSwitcher(tvid, textSwitcherFactoryOps);
        }

        initTextSwitcher(R.id.textSwitcherPoints, textSwitcherFactoryPoints);

        btns[0] = findViewById(R.id.button0);
        btns[1] = findViewById(R.id.button1);
        btns[2] = findViewById(R.id.button2);
        btns[3] = findViewById(R.id.button3);
        btns[4] = findViewById(R.id.button4);
        btns[5] = findViewById(R.id.button5);
        btns[6] = findViewById(R.id.button6);
        btns[7] = findViewById(R.id.button7);
        btns[8] = findViewById(R.id.button8);
        btns[9] = findViewById(R.id.button9);
        for (final Button b : btns) {
            b.setOnClickListener(buttonNumberOnClickListener);
        }
        findViewById(R.id.buttonDelete).setOnClickListener(deleteClickListener);
        findViewById(R.id.buttonSpace).setOnClickListener(deleteClickListener);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        handler.postDelayed(() -> newOperation() , 500);
    }

    private void setAccButtonStyle(@IdRes final int bid) {
        final Button b = findViewById(bid);
        setAccButtonStyle(b);
    }

    private void setAccButtonStyle(final Button b) {
        //b.setBackgroundColor(Color.BLACK);
        b.setTextColor(Color.YELLOW);
        b.setBackgroundResource(R.drawable.accrect);
    }

    private void updateAppStyle(){
        final SharedPreferences defs = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean hc = defs.getBoolean(Math.DEFKEY_BOOL_HIGH_CONTRAST, true);
        if (hc) {
            findViewById(R.id.toolbar).setBackgroundColor(Color.BLACK);
            ((TextView)findViewById(R.id.textCurrentOperation)).setTextColor(Color.YELLOW);
            ((TextView)findViewById(R.id.textCurrentOperation)).setBackgroundColor(Color.BLACK);
            for (final Button b : btns) {
                setAccButtonStyle(b);
            }
            setAccButtonStyle(R.id.buttonDelete);
            setAccButtonStyle(R.id.buttonSpace);
            ((FrameLayout)findViewById(R.id.layoutMainArea)).setBackgroundColor(Color.BLACK);
            findViewById(R.id.imageViewBg).setVisibility(View.GONE);

            colorDigits = Color.CYAN;
            colorSelectedDigits = Color.GREEN;
            colorQuestionMarks = Color.CYAN;

        } else {
            int colorPrim = ContextCompat.getColor(this, R.color.colorPrimary);
            int colorPrimDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            findViewById(R.id.toolbar).setBackgroundColor(colorPrim);

            ((TextView)findViewById(R.id.textCurrentOperation)).setTextColor(Color.DKGRAY);
            ((TextView)findViewById(R.id.textCurrentOperation)).setBackgroundColor(Color.WHITE);
            for (final Button b : btns) {
                b.setBackgroundColor(colorPrim);
                b.setTextColor(Color.WHITE);
            }
            Button b = findViewById(R.id.buttonDelete);
            b.setBackgroundColor(colorPrim);
            b.setTextColor(Color.WHITE);
            b = findViewById(R.id.buttonSpace);
            b.setBackgroundColor(colorPrim);
            b.setTextColor(Color.WHITE);

            FrameLayout fl = ((FrameLayout)findViewById(R.id.layoutMainArea));
            fl.setBackgroundColor(Color.WHITE);
            findViewById(R.id.imageViewBg).setVisibility(View.VISIBLE);
            colorDigits = colorPrimDark;
            colorSelectedDigits = ContextCompat.getColor(this, R.color.colorAccent);
            colorQuestionMarks = Color.LTGRAY;
        }
        findViewById(R.id.viewHLine).setBackgroundColor(colorDigits);
        updateUI();
    }

    private int colorDigits = Color.CYAN;
    private int colorQuestionMarks = Color.LTGRAY;
    private int colorSelectedDigits = Color.GREEN;

    @Override
    protected void onResume() {
        setTitle("");
        updateAppStyle();
        super.onResume();
    }

    //region Loaders

    /**
     * @see <a href="https://developer.android.com/guide/components/loaders.html">Loaders</a>
     */
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new DataLoader(this);
    }


    @Override
    public void onLoadFinished(Loader loader, Object data) {
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final float actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        final float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final float volume = actualVolume / maxVolume;

        final DataLoader l = (DataLoader)loader;
        mpLose = l.mpLose;
        mpVictory = l.mpVictory;

        if (mpVictory!=null)
            mpVictory.setVolume(0.5f*volume,0.5f*volume);
        if (mpLose!=null)
            mpLose.setVolume(0.5f*volume,0.5f*volume);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
    //endregion


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId()==R.id.menu_preferences) {
            showPreferences();
            return true;
        } else if (item.getItemId()==R.id.menu_changeop) {
            operations.changeOpType();
            newOperation();

        }
        return super.onOptionsItemSelected(item);
    }

    private final static int ACTIVITY_SETTINGS_RES_CODE = 2323;

    private void showPreferences() {
        final Intent i = new Intent(this, SettingsActivity.class);
        this.startActivityForResult(i, ACTIVITY_SETTINGS_RES_CODE);
    }

    private final ViewSwitcher.ViewFactory textSwitcherFactoryOps = new ViewSwitcher.ViewFactory() {

        public View makeView() {
            final TextView myText = new TextView(MainActivity.this);
            FrameLayout.LayoutParams layoutparams1 = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            myText.setLayoutParams(layoutparams1);

            myText.setGravity(Gravity.CENTER_VERTICAL);
            TextViewCompat.setTextAppearance(myText, R.style.BigFont);
            myText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
            return myText;
        }
    };
    private final ViewSwitcher.ViewFactory textSwitcherFactoryPoints = new ViewSwitcher.ViewFactory() {

        public View makeView() {
            final TextView myText = new TextView(MainActivity.this);
            FrameLayout.LayoutParams layoutparams1 = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            myText.setLayoutParams(layoutparams1);

            myText.setGravity(Gravity.CENTER_VERTICAL);
            TextViewCompat.setTextAppearance(myText, R.style.TextAppearance_AppCompat_Large);
            myText.setTextColor(Color.WHITE);
            return myText;
        }
    };

    private void initTextSwitcher(@IdRes final int tsId, final ViewSwitcher.ViewFactory vf) {
        final TextSwitcher ts = findViewById(tsId);

        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        ts.setFactory(vf);

        // Declare the in and out animations and initialize them
        final Animation in = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        final Animation out = AnimationUtils.loadAnimation(this,android.R.anim.fade_out);

        // set the animation type of textSwitcher
        ts.setInAnimation(in);
        ts.setOutAnimation(out);
    }

    private final View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            result = RESULT_NOT_SET;
            updateUI();
        }
    };
    private final View.OnClickListener newOpOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newOperation();
        }
    };

    @UiThread
    private void newOperation() {
        if (operations!=null) {
            result = RESULT_NOT_SET;
            operations.newOp();
            findViewById(R.id.textCurrentOperation).announceForAccessibility(
                    operations.op1+operations.getSignForAccessibility(this)+operations.op2
            );
            findViewById(R.id.operandfirst).setContentDescription(Integer.toString(operations.op1));
            final boolean isSub = operations.getOpType()==Operations.OP_SUB_1 || operations.getOpType()==Operations.OP_SUB_2;
            findViewById(R.id.operandsecond).setContentDescription((isSub?"-":"+")+" "+operations.op2);
            updateUI();
            /*
            new AlertDialog.Builder(this)
                    .setMessage(operations.op1+" op "+operations.op2 + " = "+operations.result)
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
                    .show();
                    */
        }
    }

    private final static int RESULT_NOT_SET = -1;
    private int result = RESULT_NOT_SET;

    private final View.OnClickListener buttonNumberOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonPressed((Button)v);
        }
    };

    @UiThread
    private void buttonPressed(@NonNull final Button b) {
        if (operations==null)
            return;
        final int number = Integer.parseInt((String) b.getTag());
        findViewById(R.id.textCurrentOperation).announceForAccessibility((String)b.getTag());
        if (result==RESULT_NOT_SET)
            result = 0;
        if (operations.isOneDigit()) {
            result = result * 10 + number;
        } else {
            final int numDigits = result == 0 ? 0 : (int) java.lang.Math.log10(result) + 1;
            result = result + number * (int) java.lang.Math.pow(10, numDigits);
            findViewById(R.id.textCurrentOperation).announceForAccessibility((String)b.getTag());
        }
        updateUI();
        final int l1 = (int)java.lang.Math.log10(result)+1;
        final int l2 = (int)java.lang.Math.log10(operations.result)+1;
        if (result==operations.result) {
            victory();
        } else if (l1>=l2) {
            lose();
        } else {

        }
    }

    @UiThread
    private void victory()
    {
        if (mpVictory!=null)
            mpVictory.start();
        findViewById(R.id.textCurrentOperation).announceForAccessibility(
                getString(R.string.exact, operations.op1, operations.getSignForAccessibility(this), operations.op2, operations.result)
        );
        final Math math = Math.getInstance(this);
        math.increasePoints(this);
        ((TextSwitcher)findViewById(R.id.textSwitcherPoints)).setText(Integer.toString(math.getPoints()));
        handler.postDelayed( () -> findViewById(R.id.imgViewSmileOk).setVisibility(View.VISIBLE), 500);
        handler.postDelayed( () -> findViewById(R.id.imgViewSmileOk).setVisibility(View.GONE), 3000);
        handler.postDelayed(() -> newOperation(), 3500);
    }

    @UiThread
    private void lose()
    {
        if (mpLose!=null)
            mpLose.start();
        //final Math math = Math.getInstance(this);
        //math.increasePoints(this);
        //((TextSwitcher)findViewById(R.id.textSwitcherPoints)).setText(Integer.toString(math.getPoints()));
        findViewById(R.id.textCurrentOperation).announceForAccessibility(
                getString(R.string.wrong, operations.op1, operations.getSignForAccessibility(this), operations.op2, operations.result)
        );
        handler.postDelayed(() -> findViewById(R.id.imgViewSmileNo).setVisibility(View.VISIBLE), 500);
        handler.postDelayed(() -> findViewById(R.id.imgViewSmileNo).setVisibility(View.GONE), 2500);
        handler.postDelayed(() -> newOperation(), 3000);
    }


    private void showSigns() {
        final SharedPreferences defs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean  itNotat = false;
        if (defs.contains(Math.DEFKEY_BOOL_IT_NOTATION)) {
            itNotat = defs.getBoolean(Math.DEFKEY_BOOL_IT_NOTATION, false);
        } else {
            final Locale current = getResources().getConfiguration().locale;
            try {
                final String cd = current.getISO3Country();
                itNotat = cd.equalsIgnoreCase("ITA");
                //defs.edit().putBoolean(Math.DEFKEY_BOOL_IT_NOTATION, itNotat).apply();
            } catch (MissingResourceException e) {
                e.printStackTrace();
            }
        }
        if (itNotat) {
            findViewById(R.id.textView1_sign).setVisibility(View.VISIBLE);
            findViewById(R.id.textView2_sign).setVisibility(View.VISIBLE);
            findViewById(R.id.textView2_signPrefixed).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.textView1_sign).setVisibility(View.INVISIBLE);
            findViewById(R.id.textView2_sign).setVisibility(View.INVISIBLE);
            findViewById(R.id.textView2_signPrefixed).setVisibility(View.VISIBLE);
        }

        String sign;
        if (operations.getOpType()==Operations.OP_SUB_1 ||
                operations.getOpType()==Operations.OP_SUB_2 ) {
            sign = "-";
        } else {
            sign = "+";
        }
        ((TextView)findViewById(R.id.textCurrentOperation)).setText(Operations.OPNAMES[operations.getOpType()]);
        ((TextSwitcher)findViewById(R.id.textView1_sign)).setText(sign);
        ((TextSwitcher)findViewById(R.id.textView2_signPrefixed)).setText(sign);
        ((TextSwitcher)findViewById(R.id.textView2_sign)).setText("=");
    }


    @UiThread
    private void updateUI()
    {
        if (operations==null)
            return;

        showSigns();


        final boolean isOneDigitOperation = operations.isOneDigit();


        setNumber(operations.op1, -1, R.id.textView1_2, R.id.textView1_1, false);
        setNumber(operations.op2, -1, R.id.textView2_2, R.id.textView2_1, false);
        setNumber(result, R.id.textView3_3, R.id.textView3_2, R.id.textView3_1, (result==RESULT_NOT_SET));

        final Math math = Math.getInstance(this);
        ((TextSwitcher)findViewById(R.id.textSwitcherPoints)).setText(Integer.toString(math.getPoints()));

        // Colors
        for (final @IdRes int tvid: textSwitchersOp) {
            final TextSwitcher ts = findViewById(tvid);
            ((TextView)ts.getCurrentView()).setTextColor(colorDigits);
            ((TextView)ts.getNextView()).setTextColor(colorDigits);
        }
        for (final @IdRes int tvid: textSwitchersRes) {
            final TextSwitcher ts = findViewById(tvid);
            ((TextView)ts.getCurrentView()).setTextColor(colorDigits);
            ((TextView)ts.getNextView()).setTextColor(colorDigits);
        }

        final int numExpectedDigits = ((int) java.lang.Math.log10(operations.result)) + 1;
        final int numWrittenDigits = result==RESULT_NOT_SET?0:((int) java.lang.Math.log10(result)) + 1;
        if (!isOneDigitOperation) {
            @IdRes int tv1, tv2;
            TextSwitcher ts;
            if (result==RESULT_NOT_SET){
                tv1 = R.id.textView1_1;
                tv2 = R.id.textView2_1;
            } else {
                tv1 = R.id.textView1_2;
                tv2 = R.id.textView2_2;
            }
            ts = findViewById(tv1);
            ((TextView) ts.getCurrentView()).setTextColor(colorSelectedDigits);
            ts = findViewById(tv2);
            ((TextView) ts.getCurrentView()).setTextColor(colorSelectedDigits);
        }

        // Question Marks
        final TextSwitcher tsRight = findViewById(R.id.textView3_1);
        final TextSwitcher tsCenter = findViewById(R.id.textView3_2);
        final TextSwitcher tsLeft = findViewById(R.id.textView3_3);
        if (operations.getOpType()==Operations.OP_ADD_1 && numExpectedDigits>1 && numWrittenDigits==1) {
            setTextAndColor( tsCenter, Integer.toString(result),colorDigits);
            setTextAndColor( tsRight, QM, colorQuestionMarks);
        } else {
            if (numWrittenDigits == 0) {
                setTextAndColor(tsRight, QM, colorQuestionMarks);
            }
            if (numExpectedDigits >= 2 && numWrittenDigits < 2) {
                setTextAndColor(tsCenter, QM, colorQuestionMarks);
            }
            if (numExpectedDigits >= 3 && numWrittenDigits < 3) {
                setTextAndColor(tsLeft, QM, colorQuestionMarks);
            }
        }
    }

    private void setTextAndColor(TextSwitcher ts, String txt, int color){
        TextView tv = ((TextView) ts.getNextView());
        tv.setTextColor(color);
        tv.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        ts.setText(txt);
        ts.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
    }
    private static final String QM = "?";

    private void setNumber(final int number, @IdRes final int tvLeft, @IdRes final int tvCenter,
                           @IdRes final int tvRight, final boolean allEmpty) {
        if (allEmpty) {
            if (tvLeft>0)
                setEmpty(tvLeft);
            setEmpty(tvCenter);
            setEmpty(tvRight);
        } else {
            setDigit(tvRight, 0, number);
            setDigit(tvCenter, 1, number);
            if (tvLeft>0)
                setDigit(tvLeft, 2, number);
        }

    }


    private void setDigit(@IdRes final int tvId, final int digit, final int number){
        final int n0 = number / (int)java.lang.Math.pow(10, digit);
        final int n1 = n0 % 10;
        if (n0==0 && n1==0 && digit>0) {
            setEmpty(tvId);
        } else {
            final String s = Integer.toString(n1);
            final TextSwitcher ts = findViewById(tvId);
            ts.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            ts.getNextView().setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            ts.setText(s);
        }

    }
    private void setEmpty(@IdRes final int tvId){
        final TextSwitcher ts = findViewById(tvId);
        ts.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        ts.getNextView().setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        ts.setText(" ");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
