package com.lazyhedgehog.simplecleanbutton;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.lazyhedgehog.simplecleanbutton", "com.lazyhedgehog.simplecleanbutton.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.lazyhedgehog.simplecleanbutton", "com.lazyhedgehog.simplecleanbutton.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.lazyhedgehog.simplecleanbutton.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.cachecleaner.CacheCleaner _cc = null;
public static anywheresoftware.b4a.objects.Timer _t = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _bannerad = null;
public de.donmanfred.FancyBtn _fancybtn1 = null;
public de.donmanfred.MarkViewWrapper _markview1 = null;
public static int _s = 0;
public static int _active = 0;
public anywheresoftware.b4a.objects.collections.List _l = null;
public com.lazyhedgehog.simplecleanbutton.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
int _height = 0;
 //BA.debugLineNum = 40;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 42;BA.debugLine="cc.initialize(\"cc\")";
_cc.initialize("cc",processBA);
 //BA.debugLineNum = 45;BA.debugLine="s = 1";
_s = (int) (1);
 //BA.debugLineNum = 47;BA.debugLine="Activity.Title = \"Simple Clean Button\"";
mostCurrent._activity.setTitle((Object)("Simple Clean Button"));
 //BA.debugLineNum = 48;BA.debugLine="Activity.Color = Colors.RGB(48,63,159)";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (48),(int) (63),(int) (159)));
 //BA.debugLineNum = 50;BA.debugLine="FancyBtn1.Initialize(\"fb\",\"\", 0, 1, True)";
mostCurrent._fancybtn1.Initialize(processBA,"fb","",(int) (0),(Object)(1),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 51;BA.debugLine="Activity.AddView(FancyBtn1, 50%x-80dip, 50%y-80di";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._fancybtn1.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (80))),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (80))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (160)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (160)));
 //BA.debugLineNum = 52;BA.debugLine="FancyBtn1.TextSize = 20";
mostCurrent._fancybtn1.setTextSize((int) (20));
 //BA.debugLineNum = 53;BA.debugLine="FancyBtn1.Radius = 100dip";
mostCurrent._fancybtn1.setRadius(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)));
 //BA.debugLineNum = 54;BA.debugLine="FancyBtn1.BackgroundColor = Colors.RGB(63,81,181)";
mostCurrent._fancybtn1.setBackgroundColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (63),(int) (81),(int) (181)));
 //BA.debugLineNum = 55;BA.debugLine="FancyBtn1.Text = \"Scan!\"";
mostCurrent._fancybtn1.setText("Scan!");
 //BA.debugLineNum = 57;BA.debugLine="MarkView1.Initialize(\"mw\")";
mostCurrent._markview1.Initialize(processBA,"mw");
 //BA.debugLineNum = 58;BA.debugLine="MarkView1.RingColor = Colors.RGB(48,63,159)";
mostCurrent._markview1.setRingColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (48),(int) (63),(int) (159)));
 //BA.debugLineNum = 59;BA.debugLine="MarkView1.RingWidth = 20dip";
mostCurrent._markview1.setRingWidth((float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))));
 //BA.debugLineNum = 60;BA.debugLine="MarkView1.TextColor = Colors.Transparent";
mostCurrent._markview1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 61;BA.debugLine="MarkView1.StrokeColors = Array As Int(Colors.RGB(";
mostCurrent._markview1.setStrokeColors(new int[]{anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (68),(int) (138),(int) (255)),anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (68),(int) (138),(int) (255))});
 //BA.debugLineNum = 62;BA.debugLine="MarkView1.Max = 100";
mostCurrent._markview1.setMax((int) (100));
 //BA.debugLineNum = 63;BA.debugLine="MarkView1.Mark = 100";
mostCurrent._markview1.setMark((int) (100));
 //BA.debugLineNum = 64;BA.debugLine="Activity.AddView(MarkView1, 50%x-85dip, 50%y-85di";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._markview1.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (85))),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (85))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (170)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (170)));
 //BA.debugLineNum = 66;BA.debugLine="BannerAd.Initialize2(\"BannerAd\", \"ca-app-pub-7621";
mostCurrent._bannerad.Initialize2(mostCurrent.activityBA,"BannerAd","ca-app-pub-7621578077964403/1439130577",mostCurrent._bannerad.SIZE_SMART_BANNER);
 //BA.debugLineNum = 68;BA.debugLine="Dim height As Int";
_height = 0;
 //BA.debugLineNum = 69;BA.debugLine="If GetDeviceLayoutValues.ApproximateScreenSize";
if (anywheresoftware.b4a.keywords.Common.GetDeviceLayoutValues(mostCurrent.activityBA).getApproximateScreenSize()<6) { 
 //BA.debugLineNum = 71;BA.debugLine="If 100%x > 100%y Then height = 32dip Else heig";
if (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)>anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)) { 
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32));}
else {
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50));};
 }else {
 //BA.debugLineNum = 74;BA.debugLine="height = 90dip";
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (90));
 };
 //BA.debugLineNum = 77;BA.debugLine="Activity.AddView(BannerAd, 0dip, 100%y - heigh";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._bannerad.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-_height),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),_height);
 //BA.debugLineNum = 78;BA.debugLine="BannerAd.LoadAd";
mostCurrent._bannerad.LoadAd();
 //BA.debugLineNum = 80;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 174;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 176;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 170;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 172;BA.debugLine="End Sub";
return "";
}
public static String  _cc_oncleancompleted(long _cachesize) throws Exception{
long _si = 0L;
 //BA.debugLineNum = 159;BA.debugLine="Sub cc_onCleanCompleted (CacheSize As Long)";
 //BA.debugLineNum = 160;BA.debugLine="Dim si As Long";
_si = 0L;
 //BA.debugLineNum = 161;BA.debugLine="si = CacheSize/1000000";
_si = (long) (_cachesize/(double)1000000);
 //BA.debugLineNum = 162;BA.debugLine="ToastMessageShow (si & \" MB Cache cleared!\", True";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.NumberToString(_si)+" MB Cache cleared!",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 163;BA.debugLine="FancyBtn1.Text = \"Scan!\"";
mostCurrent._fancybtn1.setText("Scan!");
 //BA.debugLineNum = 164;BA.debugLine="s = 1";
_s = (int) (1);
 //BA.debugLineNum = 165;BA.debugLine="MarkView1.Max = 100";
mostCurrent._markview1.setMax((int) (100));
 //BA.debugLineNum = 166;BA.debugLine="MarkView1.Mark = 100";
mostCurrent._markview1.setMark((int) (100));
 //BA.debugLineNum = 167;BA.debugLine="t.Enabled = False";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 168;BA.debugLine="End Sub";
return "";
}
public static String  _cc_oncleanstarted() throws Exception{
 //BA.debugLineNum = 154;BA.debugLine="Sub cc_onCleanStarted";
 //BA.debugLineNum = 155;BA.debugLine="t.Initialize(\"Timer\", 30)";
_t.Initialize(processBA,"Timer",(long) (30));
 //BA.debugLineNum = 156;BA.debugLine="t.Enabled = True";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 157;BA.debugLine="End Sub";
return "";
}
public static String  _cc_onscancompleted(Object _appslist) throws Exception{
 //BA.debugLineNum = 124;BA.debugLine="Sub cc_onScanCompleted (AppsList As Object)";
 //BA.debugLineNum = 126;BA.debugLine="l = AppsList";
mostCurrent._l.setObject((java.util.List)(_appslist));
 //BA.debugLineNum = 127;BA.debugLine="ToastMessageShow (\"Cache of \" & l.Size & \" Apps f";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Cache of "+BA.NumberToString(mostCurrent._l.getSize())+" Apps found. Clean now!",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 128;BA.debugLine="MarkView1.Max = l.Size";
mostCurrent._markview1.setMax(mostCurrent._l.getSize());
 //BA.debugLineNum = 129;BA.debugLine="FancyBtn1.Text = \"Clean now!\"";
mostCurrent._fancybtn1.setText("Clean now!");
 //BA.debugLineNum = 130;BA.debugLine="s = 2";
_s = (int) (2);
 //BA.debugLineNum = 131;BA.debugLine="MarkView1.Mark = l.Size";
mostCurrent._markview1.setMark(mostCurrent._l.getSize());
 //BA.debugLineNum = 132;BA.debugLine="t.Enabled = False";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 133;BA.debugLine="active = 1";
_active = (int) (1);
 //BA.debugLineNum = 152;BA.debugLine="End Sub";
return "";
}
public static String  _cc_onscanprogress(int _current,int _total) throws Exception{
 //BA.debugLineNum = 111;BA.debugLine="Sub cc_onScanProgress (Current As Int, Total As In";
 //BA.debugLineNum = 113;BA.debugLine="End Sub";
return "";
}
public static String  _cc_onscanstarted() throws Exception{
 //BA.debugLineNum = 104;BA.debugLine="Sub cc_OnScanStarted";
 //BA.debugLineNum = 105;BA.debugLine="MarkView1.Max = 100";
mostCurrent._markview1.setMax((int) (100));
 //BA.debugLineNum = 106;BA.debugLine="MarkView1.Mark = active";
mostCurrent._markview1.setMark(_active);
 //BA.debugLineNum = 107;BA.debugLine="t.Initialize(\"Timer\", 40)";
_t.Initialize(processBA,"Timer",(long) (40));
 //BA.debugLineNum = 108;BA.debugLine="t.Enabled = True";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 109;BA.debugLine="End Sub";
return "";
}
public static String  _fb_click(Object _v,Object _tag) throws Exception{
de.donmanfred.FancyBtn _fb = null;
 //BA.debugLineNum = 82;BA.debugLine="Sub fb_Click(v As Object, Tag As Object)";
 //BA.debugLineNum = 84;BA.debugLine="If s = 1 Then";
if (_s==1) { 
 //BA.debugLineNum = 85;BA.debugLine="If v Is FancyBtn Then";
if (_v instanceof com.div.FancyButton) { 
 //BA.debugLineNum = 86;BA.debugLine="MarkView1.Mark = 0";
mostCurrent._markview1.setMark((int) (0));
 //BA.debugLineNum = 87;BA.debugLine="cc.ScanCache";
_cc.ScanCache();
 //BA.debugLineNum = 88;BA.debugLine="Dim fb As FancyBtn = v";
_fb = new de.donmanfred.FancyBtn();
_fb.setObject((com.div.FancyButton)(_v));
 //BA.debugLineNum = 89;BA.debugLine="fb.Text = \"Scanning...\"";
_fb.setText("Scanning...");
 };
 };
 //BA.debugLineNum = 93;BA.debugLine="If s = 2 Then";
if (_s==2) { 
 //BA.debugLineNum = 94;BA.debugLine="If v Is FancyBtn Then";
if (_v instanceof com.div.FancyButton) { 
 //BA.debugLineNum = 95;BA.debugLine="MarkView1.Mark = 0";
mostCurrent._markview1.setMark((int) (0));
 //BA.debugLineNum = 96;BA.debugLine="cc.CleanCache";
_cc.CleanCache();
 //BA.debugLineNum = 97;BA.debugLine="Dim fb As FancyBtn = v";
_fb = new de.donmanfred.FancyBtn();
_fb.setObject((com.div.FancyButton)(_v));
 //BA.debugLineNum = 98;BA.debugLine="fb.Text = \"Cleaning...\"";
_fb.setText("Cleaning...");
 };
 };
 //BA.debugLineNum = 102;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 26;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 29;BA.debugLine="Private BannerAd As AdView";
mostCurrent._bannerad = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private FancyBtn1 As FancyBtn";
mostCurrent._fancybtn1 = new de.donmanfred.FancyBtn();
 //BA.debugLineNum = 31;BA.debugLine="Private MarkView1 As MarkView";
mostCurrent._markview1 = new de.donmanfred.MarkViewWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim s As Int";
_s = 0;
 //BA.debugLineNum = 35;BA.debugLine="Private active As Int = 1";
_active = (int) (1);
 //BA.debugLineNum = 37;BA.debugLine="Dim l As List";
mostCurrent._l = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 22;BA.debugLine="Dim cc As CacheCleaner";
_cc = new anywheresoftware.b4a.cachecleaner.CacheCleaner();
 //BA.debugLineNum = 23;BA.debugLine="Dim t As Timer";
_t = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 24;BA.debugLine="End Sub";
return "";
}
public static String  _timer_tick() throws Exception{
 //BA.debugLineNum = 115;BA.debugLine="Sub Timer_Tick";
 //BA.debugLineNum = 116;BA.debugLine="If active < MarkView1.Max Then";
if (_active<mostCurrent._markview1.getMax()) { 
 //BA.debugLineNum = 117;BA.debugLine="active = active + 1";
_active = (int) (_active+1);
 }else {
 //BA.debugLineNum = 119;BA.debugLine="t.Enabled = False";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 121;BA.debugLine="MarkView1.Mark = active";
mostCurrent._markview1.setMark(_active);
 //BA.debugLineNum = 122;BA.debugLine="End Sub";
return "";
}
}
