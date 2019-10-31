package com.notepoint4u.rxjavatutorial;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private CompositeDisposable disposable = new CompositeDisposable();
    final Task task = new Task("Check  Job updates", false, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create single task observable:
        Observable<Task> singleTaskObservable = Observable
                .create(new ObservableOnSubscribe<Task>() {
                    @Override
                    public void subscribe(ObservableEmitter<Task> emitter) throws Exception {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(task);//Add the single task object
                            emitter.onComplete(); // Because it is a single task, make it complete.
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        singleTaskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: single task: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


        //Creating observable using a list:
        Observable<Task> taskListObservable = Observable
                .create(new ObservableOnSubscribe<Task>() {
                    @Override
                    public void subscribe(ObservableEmitter<Task> emitter) throws Exception {
                        // Inside the subscribe method iterate through the list of tasks and call onNext(task)
                        for (Task task : DataSource.createTasksList()) {
                            if (!emitter.isDisposed()) {
                                emitter.onNext(task);
                            }
                        }
                        // Once the loop is complete, call the onComplete() method
                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskListObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext:Task list: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        //Create observable using the fromIterable()
        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTasksList())
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Exception {
                        Thread.sleep(1000);
                        return task.isComplete();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: Called");
                //Adding Disposable.
                disposable.add(d);
            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: Called");
                Log.d(TAG, "onNext: " + task.getDescription());

                //This block the MainThread - UI thread.
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: Called");
                Log.d(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: Called");
            }
        });

        //Some other ways to add disposables and create the observables:
//        disposable.add(taskObservable.subscribe(new Consumer<Task>() {
//            @Override
//            public void accept(Task task) throws Exception {
//
//            }
//        }));

        //just() operator:
        Observable.just("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten")
                // .repeat(2) // Repeat it 2 times.
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: Called");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "onNext: just(): " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Called");
                    }
                });


        //range() operator:
        Observable<Integer> observableUsingRange = Observable
                .range(0, 11) //Include 0 but exclude 11.
                .repeat(3) //It will repeat 3 times
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observableUsingRange.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: observableUsingRange:" + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


        //repeat() operator:
        //It should be used with other operator. like the range.
        //It will repeat the number of times, the value is passed to it.
        //repeat() with out any parameter will execute for infinite times.



        /*Interval*/
        //It can be used,when we wat the code to run after some interval of time.
        //Instead of using handler in android, we can use interval() and timer() to achieve the same.

        Observable<Long> intervalObservable = Observable
                .interval(1, TimeUnit.SECONDS)// period is interval of time and Time unit is for the period, if the period is in second, minute, hour, days...
                .subscribeOn(Schedulers.io())
                .takeWhile(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return aLong < 5; // if interval is more than 5, break the loop.
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());


        intervalObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG, "onNext: intervalObservable:"+aLong);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });



        /*Timer:*/
        //The code will execute after a specified interval of time. --> The observable will start emitting the item after a specified time.
        //Emits single item after the specified time has elapsed.
        Observable<Long> timerObservable = Observable
                .timer(2,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        timerObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG, "onNext: timerObservable:"+aLong);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear(); // It will clear the disposables in the object.
        //  disposable.dispose(); // It will clear the disposables and also the remove the connection with the observer.
    }
}
