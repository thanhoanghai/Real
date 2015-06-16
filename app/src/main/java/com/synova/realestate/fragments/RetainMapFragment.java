
package com.synova.realestate.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.dynamic.d;
import com.google.android.gms.dynamic.e;
import com.google.android.gms.dynamic.f;
import com.google.android.gms.internal.jx;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.IMapFragmentDelegate;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.internal.w;
import com.google.android.gms.maps.internal.x;
import com.google.android.gms.maps.model.RuntimeRemoteException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ducth on 5/23/15.
 */
public class RetainMapFragment extends Fragment {

    /**
     * This code block use to call Fragment.onPause() method directly to bypass SupportMapFragment
     * behaviour in onPause.
     */
    /*
     * @Override public void onPause() { setInstanceValue(this, "mCalled", true); } public static
     * void setInstanceValue(final Object classInstance, final String fieldName, final Object
     * newValue) { try { Field field = classInstance.getClass().getSuperclass().getSuperclass()
     * .getDeclaredField(fieldName); field.setAccessible(true); field.set(classInstance, newValue);
     * } catch (Exception e) { e.printStackTrace(); } }
     */

    private final b alD = new b(this);
    private GoogleMap akS;

    public static RetainMapFragment newInstance() {
        return new RetainMapFragment();
    }

    public static RetainMapFragment newInstance(GoogleMapOptions options) {
        RetainMapFragment var1 = new RetainMapFragment();
        Bundle var2 = new Bundle();
        var2.putParcelable("MapOptions", options);
        var1.setArguments(var2);
        return var1;
    }

    public RetainMapFragment() {
    }

    protected IMapFragmentDelegate nN() {
        this.alD.nO();
        return this.alD.je() == null ? null : ((a) this.alD.je()).nN();
    }

    @Deprecated
    public final GoogleMap getMap() {
        IMapFragmentDelegate var1 = this.nN();
        if (var1 == null) {
            return null;
        } else {
            IGoogleMapDelegate var2;
            try {
                var2 = var1.getMap();
            } catch (RemoteException var4) {
                throw new RuntimeRemoteException(var4);
            }

            if (var2 == null) {
                return null;
            } else {
                try {
                    if (this.akS == null) {
                        Constructor constructor = GoogleMap.class
                                .getDeclaredConstructor(IGoogleMapDelegate.class);
                        this.akS = (GoogleMap) constructor.newInstance(var1);
                    } else {
                        Method method = this.akS.getClass().getDeclaredMethod("nC", new Class[] {});
                        if (((IGoogleMapDelegate) method.invoke(this.akS, new Object[] {}))
                                .asBinder() != var1.asBinder()) {
                            Constructor constructor = GoogleMap.class
                                    .getDeclaredConstructor(IGoogleMapDelegate.class);
                            this.akS = (GoogleMap) constructor.newInstance(var1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return this.akS;
            }
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.alD.setActivity(activity);
    }

    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        this.alD.setActivity(activity);
        GoogleMapOptions var4 = GoogleMapOptions.createFromAttributes(activity, attrs);
        Bundle var5 = new Bundle();
        var5.putParcelable("MapOptions", var4);
        this.alD.onInflate(activity, var5, savedInstanceState);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.alD.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.alD.onCreateView(inflater, container, savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        this.alD.onResume();
    }

    public void onPause() {
        // this.alD.onPause();
        super.onPause();
    }

    public void onDestroyView() {
        // this.alD.onDestroyView();
        super.onDestroyView();
    }

    public void onDestroy() {
        // this.alD.onDestroy();
        super.onDestroy();
    }

    public void onLowMemory() {
        // this.alD.onLowMemory();
        super.onLowMemory();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(RetainMapFragment.class.getClassLoader());
        }

        super.onActivityCreated(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.setClassLoader(RetainMapFragment.class.getClassLoader());
        }

        super.onSaveInstanceState(outState);
        this.alD.onSaveInstanceState(outState);
    }

    public void getMapAsync(OnMapReadyCallback callback) {
        jx.aU("getMapAsync must be called on the main thread.");
        this.alD.getMapAsync(callback);
    }

    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    static class b extends com.google.android.gms.dynamic.a<a> {
        private final Fragment Mx;
        protected f<a> akW;
        private Activity nB;
        private final List<OnMapReadyCallback> akX = new ArrayList();

        b(Fragment var1) {
            this.Mx = var1;
        }

        protected void a(f<a> var1) {
            this.akW = var1;
            this.nO();
        }

        public void nO() {
            if (this.nB != null && this.akW != null && this.je() == null) {
                try {
                    MapsInitializer.initialize(this.nB);
                    IMapFragmentDelegate var1 = x.S(this.nB).j(e.k(this.nB));
                    this.akW.a(new a(this.Mx, var1));
                    Iterator var2 = this.akX.iterator();

                    while (var2.hasNext()) {
                        OnMapReadyCallback var3 = (OnMapReadyCallback) var2.next();
                        ((a) this.je()).getMapAsync(var3);
                    }

                    this.akX.clear();
                } catch (RemoteException var4) {
                    throw new RuntimeRemoteException(var4);
                } catch (GooglePlayServicesNotAvailableException var5) {
                    ;
                }
            }

        }

        private void setActivity(Activity activity) {
            this.nB = activity;
            this.nO();
        }

        public void getMapAsync(OnMapReadyCallback callback) {
            if (this.je() != null) {
                ((a) this.je()).getMapAsync(callback);
            } else {
                this.akX.add(callback);
            }

        }
    }

    static class a implements MapLifecycleDelegate {
        private final Fragment Mx;
        private final IMapFragmentDelegate akT;

        public a(Fragment var1, IMapFragmentDelegate var2) {
            this.akT = (IMapFragmentDelegate) jx.i(var2);
            this.Mx = (Fragment) jx.i(var1);
        }

        public void onInflate(Activity activity, Bundle attrs, Bundle savedInstanceState) {
            GoogleMapOptions var4 = (GoogleMapOptions) attrs.getParcelable("MapOptions");

            try {
                this.akT.onInflate(e.k(activity), var4, savedInstanceState);
            } catch (RemoteException var6) {
                throw new RuntimeRemoteException(var6);
            }
        }

        public void onCreate(Bundle savedInstanceState) {
            try {
                if (savedInstanceState == null) {
                    savedInstanceState = new Bundle();
                }

                Bundle var2 = this.Mx.getArguments();
                if (var2 != null && var2.containsKey("MapOptions")) {
                    w.a(savedInstanceState, "MapOptions", var2.getParcelable("MapOptions"));
                }

                this.akT.onCreate(savedInstanceState);
            } catch (RemoteException var3) {
                throw new RuntimeRemoteException(var3);
            }
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            d var4;
            try {
                var4 = this.akT.onCreateView(e.k(inflater), e.k(container), savedInstanceState);
            } catch (RemoteException var6) {
                throw new RuntimeRemoteException(var6);
            }

            return (View) e.f(var4);
        }

        public void onStart() {
        }

        public void onResume() {
            try {
                this.akT.onResume();
            } catch (RemoteException var2) {
                throw new RuntimeRemoteException(var2);
            }
        }

        public void onPause() {
            try {
                this.akT.onPause();
            } catch (RemoteException var2) {
                throw new RuntimeRemoteException(var2);
            }
        }

        public void onStop() {
        }

        public void onDestroyView() {
            try {
                this.akT.onDestroyView();
            } catch (RemoteException var2) {
                throw new RuntimeRemoteException(var2);
            }
        }

        public void onDestroy() {
            try {
                this.akT.onDestroy();
            } catch (RemoteException var2) {
                throw new RuntimeRemoteException(var2);
            }
        }

        public void onLowMemory() {
            try {
                this.akT.onLowMemory();
            } catch (RemoteException var2) {
                throw new RuntimeRemoteException(var2);
            }
        }

        public void onSaveInstanceState(Bundle outState) {
            try {
                this.akT.onSaveInstanceState(outState);
            } catch (RemoteException var3) {
                throw new RuntimeRemoteException(var3);
            }
        }

        public IMapFragmentDelegate nN() {
            return this.akT;
        }

        public void getMapAsync(final OnMapReadyCallback callback) {
            try {
                this.akT.getMapAsync(new com.google.android.gms.maps.internal.m.a() {
                    public void a(IGoogleMapDelegate var1) throws RemoteException {
                        try {
                            Constructor constructor = GoogleMap.class
                                    .getDeclaredConstructor(IGoogleMapDelegate.class);
                            constructor.setAccessible(true);
                            callback.onMapReady((GoogleMap) constructor.newInstance(var1));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (RemoteException var3) {
                throw new RuntimeRemoteException(var3);
            }
        }
    }

}
