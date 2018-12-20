package com.google.example.games.basegameutils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotContents;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SnapshotCoordinator implements Snapshots {
    private static final String TAG = "SnapshotCoordinator";
    private static final SnapshotCoordinator theInstance = new SnapshotCoordinator();
    private final Set<String> closing = new HashSet();
    private final Map<String, CountDownLatch> opened = new HashMap();

    private interface ResultListener {
        void onResult(Result result);
    }

    private class CoordinatedPendingResult<T extends Result> extends PendingResult<T> {
        PendingResult<T> innerResult;
        ResultListener listener;

        /* renamed from: com.google.example.games.basegameutils.SnapshotCoordinator$CoordinatedPendingResult$1 */
        class C05211 implements Result {
            C05211() {
            }

            public Status getStatus() {
                return new Status(16);
            }
        }

        public CoordinatedPendingResult(PendingResult<T> result, ResultListener listener) {
            this.innerResult = result;
            this.listener = listener;
        }

        @NonNull
        public T await() {
            T retval = this.innerResult.await();
            if (this.listener != null) {
                this.listener.onResult(retval);
            }
            return retval;
        }

        @NonNull
        public T await(long l, @NonNull TimeUnit timeUnit) {
            T retval = this.innerResult.await(l, timeUnit);
            if (this.listener != null) {
                this.listener.onResult(retval);
            }
            return retval;
        }

        public void cancel() {
            if (this.listener != null) {
                this.listener.onResult(new C05211());
            }
            this.innerResult.cancel();
        }

        public boolean isCanceled() {
            return this.innerResult.isCanceled();
        }

        public void setResultCallback(@NonNull ResultCallback<? super T> resultCallback) {
            final ResultCallback<? super T> theCallback = resultCallback;
            this.innerResult.setResultCallback(new ResultCallback<T>() {
                public void onResult(@NonNull T t) {
                    if (CoordinatedPendingResult.this.listener != null) {
                        CoordinatedPendingResult.this.listener.onResult(t);
                    }
                    theCallback.onResult(t);
                }
            });
        }

        public void setResultCallback(@NonNull ResultCallback<? super T> resultCallback, long l, @NonNull TimeUnit timeUnit) {
            final ResultCallback<? super T> theCallback = resultCallback;
            this.innerResult.setResultCallback(new ResultCallback<T>() {
                public void onResult(@NonNull T t) {
                    if (CoordinatedPendingResult.this.listener != null) {
                        CoordinatedPendingResult.this.listener.onResult(t);
                    }
                    theCallback.onResult(t);
                }
            }, l, timeUnit);
        }
    }

    private class CountDownPendingResult extends PendingResult<Result> {
        private final Status Canceled = new Status(16);
        private final Status Success = new Status(0);
        private boolean canceled;
        private final CountDownLatch latch;

        /* renamed from: com.google.example.games.basegameutils.SnapshotCoordinator$CountDownPendingResult$1 */
        class C05241 implements Result {
            C05241() {
            }

            public Status getStatus() {
                return CountDownPendingResult.this.Canceled;
            }
        }

        /* renamed from: com.google.example.games.basegameutils.SnapshotCoordinator$CountDownPendingResult$2 */
        class C05252 implements Result {
            C05252() {
            }

            public Status getStatus() {
                return CountDownPendingResult.this.canceled ? CountDownPendingResult.this.Canceled : CountDownPendingResult.this.Success;
            }
        }

        /* renamed from: com.google.example.games.basegameutils.SnapshotCoordinator$CountDownPendingResult$3 */
        class C05263 implements Result {
            C05263() {
            }

            public Status getStatus() {
                return CountDownPendingResult.this.Canceled;
            }
        }

        /* renamed from: com.google.example.games.basegameutils.SnapshotCoordinator$CountDownPendingResult$4 */
        class C05274 implements Result {
            C05274() {
            }

            public Status getStatus() {
                return CountDownPendingResult.this.canceled ? CountDownPendingResult.this.Canceled : CountDownPendingResult.this.Success;
            }
        }

        /* renamed from: com.google.example.games.basegameutils.SnapshotCoordinator$CountDownPendingResult$6 */
        class C05306 implements Result {
            C05306() {
            }

            public Status getStatus() {
                return CountDownPendingResult.this.canceled ? CountDownPendingResult.this.Canceled : CountDownPendingResult.this.Success;
            }
        }

        /* renamed from: com.google.example.games.basegameutils.SnapshotCoordinator$CountDownPendingResult$8 */
        class C05338 implements Result {
            C05338() {
            }

            public Status getStatus() {
                return CountDownPendingResult.this.canceled ? CountDownPendingResult.this.Canceled : CountDownPendingResult.this.Success;
            }
        }

        public CountDownPendingResult(CountDownLatch latch) {
            this.latch = latch;
            this.canceled = false;
        }

        @NonNull
        public Result await() {
            if (!(this.canceled || this.latch == null)) {
                try {
                    this.latch.await();
                } catch (InterruptedException e) {
                    return new C05241();
                }
            }
            return new C05252();
        }

        @NonNull
        public Result await(long l, @NonNull TimeUnit timeUnit) {
            if (!(this.canceled || this.latch == null)) {
                try {
                    this.latch.await(l, timeUnit);
                } catch (InterruptedException e) {
                    return new C05263();
                }
            }
            return new C05274();
        }

        public void cancel() {
            this.canceled = true;
        }

        public boolean isCanceled() {
            return this.canceled;
        }

        @SuppressLint("StaticFieldLeak")
        public void setResultCallback(@NonNull final ResultCallback<? super Result> resultCallback) {
            if (this.canceled || this.latch == null) {
                resultCallback.onResult(new C05306());
                return;
            }
            new AsyncTask<Object, Object, Void>() {

                /* renamed from: com.google.example.games.basegameutils.SnapshotCoordinator$CountDownPendingResult$5$1 */
                class C05281 implements Result {

                    C05281() {
                    }

                    @Override
                    public com.google.android.gms.common.api.Status getStatus() {
                        return CountDownPendingResult.this.canceled ? CountDownPendingResult.this.Canceled : CountDownPendingResult.this.Success;
                    }
                }

                /* renamed from: com.google.example.games.basegameutils.SnapshotCoordinator$CountDownPendingResult$5$2 */
                class C05292 implements Result {
                    C05292() {
                    }

                    public com.google.android.gms.common.api.Status getStatus() {
                        return CountDownPendingResult.this.Canceled;
                    }
                }

                protected Void doInBackground(Object... params) {
                    try {
                        CountDownPendingResult.this.latch.await();
                        resultCallback.onResult(new C05281());
                    } catch (InterruptedException e) {
                        resultCallback.onResult(new C05292());
                    }
                    return null;
                }
            }.execute(new Object[]{this.latch});
        }

        @SuppressLint("StaticFieldLeak")
        public void setResultCallback(@NonNull ResultCallback<? super Result> resultCallback, long l, @NonNull TimeUnit timeUnit) {
            if (this.canceled || this.latch == null) {
                resultCallback.onResult(new C05338());
                return;
            }
            final long j = l;
            final TimeUnit timeUnit2 = timeUnit;
            final ResultCallback<? super Result> resultCallback2 = resultCallback;
            new AsyncTask<Object, Object, Void>() {

                /* renamed from: com.google.example.games.basegameutils.SnapshotCoordinator$CountDownPendingResult$7$1 */
                class C05311 implements Result {
                    C05311() {
                    }

                    @Override
                    public com.google.android.gms.common.api.Status getStatus() {
                        return CountDownPendingResult.this.canceled ? CountDownPendingResult.this.Canceled : CountDownPendingResult.this.Success;
                    }
                }

                /* renamed from: com.google.example.games.basegameutils.SnapshotCoordinator$CountDownPendingResult$7$2 */
                class C05322 implements Result {
                    C05322() {
                    }

                    @Override
                    public com.google.android.gms.common.api.Status getStatus() {
                        return CountDownPendingResult.this.Canceled;
                    }
                }

                protected Void doInBackground(Object... params) {
                    try {
                        CountDownPendingResult.this.latch.await(j, timeUnit2);
                        resultCallback2.onResult(new C05311());
                    } catch (InterruptedException e) {
                        resultCallback2.onResult(new C05322());
                    }
                    return null;
                }

            }.execute(new Object[]{this.latch});
        }
    }

    public static SnapshotCoordinator getInstance() {
        return theInstance;
    }

    private SnapshotCoordinator() {
    }

    public synchronized boolean isAlreadyOpen(String filename) {
        return this.opened.containsKey(filename);
    }

    public synchronized boolean isAlreadyClosing(String filename) {
        return this.closing.contains(filename);
    }

    private synchronized void setIsClosing(String filename) {
        this.closing.add(filename);
    }

    private synchronized void setClosed(String filename) {
        this.closing.remove(filename);
        CountDownLatch l = (CountDownLatch) this.opened.remove(filename);
        if (l != null) {
            l.countDown();
        }
    }

    private synchronized void setIsOpening(String filename) {
        this.opened.put(filename, new CountDownLatch(1));
    }

    public PendingResult<Result> waitForClosed(String filename) {
        CountDownLatch l;
        synchronized (this) {
            l = (CountDownLatch) this.opened.get(filename);
        }
        return new CountDownPendingResult(l);
    }

    public int getMaxDataSize(GoogleApiClient googleApiClient) {
        return Games.Snapshots.getMaxDataSize(googleApiClient);
    }

    public int getMaxCoverImageSize(GoogleApiClient googleApiClient) {
        return Games.Snapshots.getMaxCoverImageSize(googleApiClient);
    }

    public Intent getSelectSnapshotIntent(GoogleApiClient googleApiClient, String title, boolean allowAddButton, boolean allowDelete, int maxSnapshots) {
        return Games.Snapshots.getSelectSnapshotIntent(googleApiClient, title, allowAddButton, allowDelete, maxSnapshots);
    }

    public PendingResult<LoadSnapshotsResult> load(GoogleApiClient googleApiClient, boolean forceReload) {
        return Games.Snapshots.load(googleApiClient, forceReload);
    }

    public SnapshotMetadata getSnapshotFromBundle(Bundle bundle) {
        return Games.Snapshots.getSnapshotFromBundle(bundle);
    }

    public PendingResult<OpenSnapshotResult> resolveConflict(GoogleApiClient googleApiClient, String conflictId, String snapshotId, SnapshotMetadataChange snapshotMetadataChange, SnapshotContents snapshotContents) {
        throw new IllegalStateException("resolving conflicts with ids is not supported.");
    }

    public void discardAndClose(GoogleApiClient googleApiClient, Snapshot snapshot) {
        if (!isAlreadyOpen(snapshot.getMetadata().getUniqueName()) || isAlreadyClosing(snapshot.getMetadata().getUniqueName())) {
            throw new IllegalStateException(snapshot.getMetadata().getUniqueName() + " is not open or is busy");
        }
        Games.Snapshots.discardAndClose(googleApiClient, snapshot);
        Log.d(TAG, "Closed " + snapshot.getMetadata().getUniqueName());
        setClosed(snapshot.getMetadata().getUniqueName());
    }

    public PendingResult<OpenSnapshotResult> open(GoogleApiClient googleApiClient, final String filename, boolean createIfNotFound) {
        if (isAlreadyOpen(filename)) {
            throw new IllegalStateException(filename + " is already open");
        }
        setIsOpening(filename);
        try {
            return new CoordinatedPendingResult(Games.Snapshots.open(googleApiClient, filename, createIfNotFound), new ResultListener() {
                public void onResult(Result result) {
                    if (result.getStatus().isSuccess()) {
                        Log.d(SnapshotCoordinator.TAG, "Open successful: " + filename);
                        return;
                    }
                    Log.d(SnapshotCoordinator.TAG, "Open was not a success: " + result.getStatus() + " for filename " + filename);
                    SnapshotCoordinator.this.setClosed(filename);
                }
            });
        } catch (RuntimeException e) {
            setClosed(filename);
            throw e;
        }
    }

    public PendingResult<OpenSnapshotResult> open(GoogleApiClient googleApiClient, final String filename, boolean createIfNotFound, int conflictPolicy) {
        if (isAlreadyOpen(filename)) {
            throw new IllegalStateException(filename + " is already open");
        }
        setIsOpening(filename);
        try {
            return new CoordinatedPendingResult(Games.Snapshots.open(googleApiClient, filename, createIfNotFound, conflictPolicy), new ResultListener() {
                public void onResult(Result result) {
                    if (result.getStatus().isSuccess()) {
                        Log.d(SnapshotCoordinator.TAG, "Open successful: " + filename);
                        return;
                    }
                    Log.d(SnapshotCoordinator.TAG, "Open was not a success: " + result.getStatus() + " for filename " + filename);
                    SnapshotCoordinator.this.setClosed(filename);
                }
            });
        } catch (RuntimeException e) {
            setClosed(filename);
            throw e;
        }
    }

    public PendingResult<OpenSnapshotResult> open(GoogleApiClient googleApiClient, final SnapshotMetadata snapshotMetadata) {
        if (isAlreadyOpen(snapshotMetadata.getUniqueName())) {
            throw new IllegalStateException(snapshotMetadata.getUniqueName() + " is already open");
        }
        setIsOpening(snapshotMetadata.getUniqueName());
        try {
            return new CoordinatedPendingResult(Games.Snapshots.open(googleApiClient, snapshotMetadata), new ResultListener() {
                public void onResult(Result result) {
                    if (result.getStatus().isSuccess()) {
                        Log.d(SnapshotCoordinator.TAG, "Open was successful: " + snapshotMetadata.getUniqueName());
                        return;
                    }
                    Log.d(SnapshotCoordinator.TAG, "Open was not a success: " + result.getStatus() + " for filename " + snapshotMetadata.getUniqueName());
                    SnapshotCoordinator.this.setClosed(snapshotMetadata.getUniqueName());
                }
            });
        } catch (RuntimeException e) {
            setClosed(snapshotMetadata.getUniqueName());
            throw e;
        }
    }

    public PendingResult<OpenSnapshotResult> open(GoogleApiClient googleApiClient, final SnapshotMetadata snapshotMetadata, int conflictPolicy) {
        if (isAlreadyOpen(snapshotMetadata.getUniqueName())) {
            throw new IllegalStateException(snapshotMetadata.getUniqueName() + " is already open");
        }
        setIsOpening(snapshotMetadata.getUniqueName());
        try {
            return new CoordinatedPendingResult(Games.Snapshots.open(googleApiClient, snapshotMetadata, conflictPolicy), new ResultListener() {
                public void onResult(Result result) {
                    if (result.getStatus().isSuccess()) {
                        Log.d(SnapshotCoordinator.TAG, "Open was successful: " + snapshotMetadata.getUniqueName());
                        return;
                    }
                    Log.d(SnapshotCoordinator.TAG, "Open was not a success: " + result.getStatus() + " for filename " + snapshotMetadata.getUniqueName());
                    SnapshotCoordinator.this.setClosed(snapshotMetadata.getUniqueName());
                }
            });
        } catch (RuntimeException e) {
            setClosed(snapshotMetadata.getUniqueName());
            throw e;
        }
    }

    public PendingResult<CommitSnapshotResult> commitAndClose(GoogleApiClient googleApiClient, final Snapshot snapshot, SnapshotMetadataChange snapshotMetadataChange) {
        if (!isAlreadyOpen(snapshot.getMetadata().getUniqueName()) || isAlreadyClosing(snapshot.getMetadata().getUniqueName())) {
            throw new IllegalStateException(snapshot.getMetadata().getUniqueName() + " is either closed or is closing");
        }
        setIsClosing(snapshot.getMetadata().getUniqueName());
        try {
            return new CoordinatedPendingResult(Games.Snapshots.commitAndClose(googleApiClient, snapshot, snapshotMetadataChange), new ResultListener() {
                public void onResult(Result result) {
                    Log.d(SnapshotCoordinator.TAG, "CommitAndClose complete, closing " + snapshot.getMetadata().getUniqueName());
                    SnapshotCoordinator.this.setClosed(snapshot.getMetadata().getUniqueName());
                }
            });
        } catch (RuntimeException e) {
            setClosed(snapshot.getMetadata().getUniqueName());
            throw e;
        }
    }

    public PendingResult<DeleteSnapshotResult> delete(GoogleApiClient googleApiClient, final SnapshotMetadata snapshotMetadata) {
        if (isAlreadyOpen(snapshotMetadata.getUniqueName()) || isAlreadyClosing(snapshotMetadata.getUniqueName())) {
            throw new IllegalStateException(snapshotMetadata.getUniqueName() + " is either open or is busy");
        }
        setIsClosing(snapshotMetadata.getUniqueName());
        try {
            return new CoordinatedPendingResult(Games.Snapshots.delete(googleApiClient, snapshotMetadata), new ResultListener() {
                public void onResult(Result result) {
                    SnapshotCoordinator.this.setClosed(snapshotMetadata.getUniqueName());
                }
            });
        } catch (RuntimeException e) {
            setClosed(snapshotMetadata.getUniqueName());
            throw e;
        }
    }

    public PendingResult<OpenSnapshotResult> resolveConflict(GoogleApiClient googleApiClient, String conflictId, final Snapshot snapshot) {
        if (isAlreadyOpen(snapshot.getMetadata().getUniqueName()) || isAlreadyClosing(snapshot.getMetadata().getUniqueName())) {
            throw new IllegalStateException(snapshot.getMetadata().getUniqueName() + " is already open or is busy");
        }
        setIsOpening(snapshot.getMetadata().getUniqueName());
        try {
            return new CoordinatedPendingResult(Games.Snapshots.resolveConflict(googleApiClient, conflictId, snapshot), new ResultListener() {
                public void onResult(Result result) {
                    if (!result.getStatus().isSuccess()) {
                        SnapshotCoordinator.this.setClosed(snapshot.getMetadata().getUniqueName());
                    }
                }
            });
        } catch (RuntimeException e) {
            setClosed(snapshot.getMetadata().getUniqueName());
            throw e;
        }
    }
}
